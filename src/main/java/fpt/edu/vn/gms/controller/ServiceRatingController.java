package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.ServiceRatingRequest;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.ServiceRatingResponse;
import fpt.edu.vn.gms.service.ServiceRatingService;
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Tạo đánh giá dịch vụ", description = "Khách hàng gửi đánh giá cho phiếu dịch vụ.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Tạo đánh giá thành công",
                    content = @Content(schema = @Schema(implementation = ServiceRatingResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy phiếu dịch vụ hoặc khách hàng",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    public ResponseEntity<ApiResponse<ServiceRatingResponse>> createRating(
            @Valid @RequestBody ServiceRatingRequest request) {

        ServiceRatingResponse response = serviceRatingService.createRating(request);

        return ResponseEntity.status(201)
                .body(ApiResponse.created("Tạo đánh giá thành công", response));
    }
}

