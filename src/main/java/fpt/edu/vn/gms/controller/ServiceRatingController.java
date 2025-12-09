package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.Public;
import fpt.edu.vn.gms.dto.request.ServiceRatingRequest;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.ServiceRatingResponse;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.service.ServiceRatingService;
import fpt.edu.vn.gms.service.auth.JwtService;
import fpt.edu.vn.gms.service.zalo.OneTimeTokenService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "rating-controller", description = "API đánh giá dịch vụ")
@RestController
@RequestMapping(path = "/api/ratings", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ServiceRatingController {

        private final ServiceRatingService serviceRatingService;
        private final JwtService jwtService;
        private final OneTimeTokenService oneTimeTokenService;

        @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Tạo đánh giá dịch vụ", description = "Khách hàng gửi đánh giá cho phiếu dịch vụ.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo đánh giá thành công", content = @Content(schema = @Schema(implementation = ServiceRatingResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu dịch vụ hoặc khách hàng", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<ServiceRatingResponse>> createRating(
                        @Valid @RequestBody ServiceRatingRequest request) {

                ServiceRatingResponse response = serviceRatingService.createRating(request);

                return ResponseEntity.status(201)
                                .body(ApiResponse.created("Tạo đánh giá thành công", response));
        }

        @Public
        @PostMapping("/callback/{one_time_token}")
        @Operation(summary = "Callback đánh giá từ Zalo", description = "Khách hàng nhấn nút đánh giá trong Zalo, hệ thống nhận token và tạo đánh giá.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tạo đánh giá thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Token không hợp lệ hoặc thiếu dữ liệu", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu dịch vụ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<ServiceRatingResponse>> createRatingFromZalo(
                        @PathVariable("one_time_token") String token,
                        @RequestParam(required = false) Integer stars,
                        @RequestParam(required = false) String feedback) {

                try {
                        Claims claims = jwtService.extractAllClaims(token);

                        Long serviceTicketId = claims.get("service_id", Long.class);
                        Integer tokenStars = claims.get("stars", Integer.class);
                        String tokenFeedback = claims.get("feedback", String.class);

                        // Ưu tiên param từ request, fallback claims
                        Integer finalStars = stars != null ? stars : tokenStars;
                        String finalFeedback = feedback != null ? feedback : tokenFeedback;

                        if (serviceTicketId == null || finalStars == null) {
                                return ResponseEntity.badRequest()
                                                .body(ApiResponse.error(400,
                                                                "Thiếu service_ticket_id hoặc stars trong token"));
                        }

                        ServiceRatingRequest request = new ServiceRatingRequest();
                        request.setServiceTicketId(serviceTicketId);
                        request.setStars(finalStars);
                        request.setFeedback(finalFeedback);

                        ServiceRatingResponse response = serviceRatingService.createRating(request);

                        // 2. Xóa token sau khi xử lý thành công
                        oneTimeTokenService.deleteToken(token);

                        return ResponseEntity.ok(ApiResponse.success("Tạo đánh giá thành công", response));

                } catch (ResourceNotFoundException e) {
                        return ResponseEntity.status(404)
                                        .body(ApiResponse.error(404, e.getMessage()));
                } catch (Exception e) {
                        return ResponseEntity.badRequest()
                                        .body(ApiResponse.error(400,
                                                        "Token không hợp lệ hoặc xử lý lỗi: " + e.getMessage()));
                }
        }
}
