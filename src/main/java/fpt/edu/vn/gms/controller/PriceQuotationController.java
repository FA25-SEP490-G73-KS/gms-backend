package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.Public;
import fpt.edu.vn.gms.dto.request.ChangeQuotationStatusReqDto;
import fpt.edu.vn.gms.dto.request.PriceQuotationRequestDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.service.PriceQuotationService;
import fpt.edu.vn.gms.service.WarehouseQuotationService;
import fpt.edu.vn.gms.service.auth.JwtService;
import fpt.edu.vn.gms.service.zalo.OneTimeTokenService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

import static fpt.edu.vn.gms.utils.AppRoutes.PRICE_QUOTATIONS_PREFIX;

@Tag(name = "price-quotations", description = "Quản lý báo giá và phê duyệt báo giá")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = PRICE_QUOTATIONS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PriceQuotationController {

        PriceQuotationService priceQuotationService;
        JwtService jwtService;
        OneTimeTokenService oneTimeTokenService;
        WarehouseQuotationService warehouseQuotationService;
        PriceQuotationRepository priceQuotationRepository;

        @GetMapping("/pending")
        @Operation(summary = "Lấy báo giá đang chờ xử lý", description = "Lấy danh sách các báo giá đang chờ xử lý từ kho với phân trang.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách báo giá thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<Page<PriceQuotationResponseDto>>> getPendingQuotations(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "6") int size) {

                Page<PriceQuotationResponseDto> quotations = warehouseQuotationService.getPendingQuotations(page, size);

                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Success", quotations));
        }

        @PostMapping
        @Operation(summary = "Tạo báo giá mới", description = "Tạo một phiếu báo giá mới từ một phiếu dịch vụ.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tạo báo giá thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu dịch vụ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<ServiceTicketResponseDto>> createQuotation(
                        @RequestParam("ticketId") Long ticketId) {

                ServiceTicketResponseDto responseDto = priceQuotationService.createQuotation(ticketId);
                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Tạo phiếu báo giá thành công!!!", responseDto));
        }

        @PatchMapping("/{id}")
        @Operation(summary = "Cập nhật các mục trong báo giá", description = "Cập nhật các mục trong một phiếu báo giá đã có.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật báo giá thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy báo giá", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<ServiceTicketResponseDto>> updateItems(
                        @PathVariable Long id,
                        @RequestBody PriceQuotationRequestDto dto) {

                ServiceTicketResponseDto response = priceQuotationService.updateQuotationItems(id, dto);

                return ResponseEntity.ok(ApiResponse.success("Cập nhật báo giá thành công!", response));
        }

        @PatchMapping("/{id}/recalculate-estimate")
        @Operation(summary = "Tính toán lại tổng tiền dự kiến", description = "Tính toán lại tổng số tiền dự kiến của một phiếu báo giá.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật tổng dự kiến thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy báo giá", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<PriceQuotationResponseDto>> recalculateEstimateAmount(
                        @PathVariable Long id) {

                PriceQuotationResponseDto updatedQuotation = priceQuotationService
                                .recalculateEstimateAmount(id);
                return ResponseEntity.ok(ApiResponse.success("Cập nhật tổng dự kiến thành công", updatedQuotation));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Lấy báo giá theo ID", description = "Lấy thông tin chi tiết của một phiếu báo giá bằng ID.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy báo giá thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy báo giá", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<PriceQuotationResponseDto>> getById(@PathVariable Long id) {
                PriceQuotationResponseDto response = priceQuotationService.getById(id);
                return ResponseEntity.ok(ApiResponse.success("Lấy báo giá thành công!", response));
        }

        @PatchMapping("/{id}/status")
        @Operation(summary = "Cập nhật trạng thái báo giá thủ công", description = "Cập nhật trạng thái của một phiếu báo giá một cách thủ công.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật trạng thái thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy báo giá", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<PriceQuotationResponseDto>> updateQuotationStatusManual(
                        @PathVariable Long id,
                        @RequestBody ChangeQuotationStatusReqDto dto) {
                PriceQuotationResponseDto responseDto = priceQuotationService.updateQuotationStatusManual(id, dto);

                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Successfully!!", responseDto));
        }

        @PostMapping("/{id}/send-to-customer")
        @Operation(summary = "Gửi báo giá cho khách hàng", description = "Gửi phiếu báo giá cho khách hàng và cập nhật trạng thái.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gửi báo giá thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy báo giá", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<PriceQuotationResponseDto>> sendToCustomer(
                        @PathVariable Long id) {

                PriceQuotationResponseDto response = priceQuotationService.sendQuotationToCustomer(id);
                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Successfully!!", response));
        }

        @PostMapping("/{id}/confirm")
        @Operation(summary = "Khách hàng xác nhận báo giá", description = "Khách hàng xác nhận phiếu báo giá.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xác nhận thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy báo giá", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<PriceQuotationResponseDto>> confirmByCustomer(
                        @PathVariable Long id) {

                PriceQuotationResponseDto response = priceQuotationService.confirmQuotationByCustomer(id);

                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Successfully!!", response));
        }

        @PostMapping("/{id}/reject")
        @Operation(summary = "Khách hàng từ chối báo giá", description = "Khách hàng từ chối phiếu báo giá và cung cấp lý do.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Từ chối thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy báo giá", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<PriceQuotationResponseDto>> rejectByCustomer(
                        @PathVariable Long id,
                        @RequestBody String note) {

                PriceQuotationResponseDto response = priceQuotationService.rejectQuotationByCustomer(id, note);

                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Successfully!!", response));
        }

        @GetMapping("/count-waiting")
        @Operation(summary = "Đếm số phiếu báo giá đang chờ khách hàng duyệt", description = "Cố vấn dịch vụ gửi báo giá chờ khách duyệt")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Số báo giá chờ khách duyệt"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<Long>> countWaiting() {
                long count = priceQuotationService.countWaitingCustomerConfirm();
                return ResponseEntity.ok(
                                ApiResponse.success("Số báo giá khách hàng chưa xác nhận", count));
        }

        @GetMapping("/count-vehicle-repairing")
        @Operation(summary = "Đếm số phiếu báo giá của xe đang sửa chữa", description = "Cố vấn dịch vụ đếm số báo giá của xe đang trong quá trình sửa chữa.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Số báo giá của xe đang sửa chữa"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<Long>> countVehicleRepairing() {
                long count = priceQuotationService.countVehicleInRepairingStatus();
                return ResponseEntity.ok(
                                ApiResponse.success("Số báo giá của xe đang sửa chữa", count));
        }

        @Operation(summary = "Xuất PDF báo giá", description = """
                        API xuất file PDF Báo giá sửa chữa từ template HTML.
                        - Tự động bind dữ liệu: khách hàng, xe, items, tổng tiền.
                        - Tải về file PDF theo mẫu đã thiết kế.
                        """)
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xuất PDF thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy báo giá")
        })
        @GetMapping("/{id}/pdf")
        public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) {

                byte[] pdfBytes = priceQuotationService.exportPdfQuotation(id);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData(
                                "attachment",
                                "quotation-" + id + ".pdf");

                return ResponseEntity.ok()
                                .headers(headers)
                                .body(pdfBytes);
        }

        @Public
        @PostMapping("/confirm-quotation/{one_time_token}")
        @Operation(summary = "Xác nhận báo giá", description = "API callback khi khách hàng ấn nút xác nhận báo giá từ ZNS.")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Token không hợp lệ"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        public ResponseEntity<?> confirmQuotation(
                        @PathVariable("one_time_token") String token) {

                try {
                        // 1. Giải mã token
                        Claims claims = jwtService.extractAllClaims(token);

                        String quotationCode = claims.get("quotation_code", String.class);

                        // 2. Tìm quotation từ code
                        PriceQuotation quotation = priceQuotationRepository.findByCode(quotationCode)
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Không tìm thấy báo giá với mã: " + quotationCode));

                        // 3. Gọi xử lý business xác nhận báo giá
                        priceQuotationService.confirmQuotationByCustomer(quotation.getPriceQuotationId());

                        // 4. Xóa token sau khi xử lý thành công
                        oneTimeTokenService.deleteToken(token);

                        // 5. Trả về status để Zalo hiện tin nhắn cho khách
                        Map<String, Object> response = Map.of(
                                        "error", 0,
                                        "message", "OK",
                                        "data", Map.of("status", "CONFIRMED"));

                        return ResponseEntity.ok(response);

                } catch (ResourceNotFoundException e) {
                        Map<String, Object> error = Map.of(
                                        "error", -1,
                                        "message", e.getMessage());
                        return ResponseEntity.badRequest().body(error);
                } catch (Exception e) {
                        Map<String, Object> error = Map.of(
                                        "error", -1,
                                        "message", "Invalid token or processing error: " + e.getMessage());
                        return ResponseEntity.badRequest().body(error);
                }
        }

        @Public
        @PostMapping("/reject-quotation/{one_time_token}")
        @Operation(summary = "Từ chối báo giá", description = "API callback khi khách hàng ấn nút từ chối báo giá từ ZNS.")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Token không hợp lệ"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
        })
        public ResponseEntity<?> rejectQuotation(
                        @PathVariable("one_time_token") String token,
                        @RequestParam(required = false) String reason) {

                try {
                        // 1. Giải mã token
                        Claims claims = jwtService.extractAllClaims(token);

                        String quotationCode = claims.get("quotation_code", String.class);
                        // Lấy reason từ token nếu không có trong request param
                        String rejectionReason = reason != null ? reason : claims.get("reason", String.class);

                        // 2. Tìm quotation từ code
                        PriceQuotation quotation = priceQuotationRepository.findByCode(quotationCode)
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Không tìm thấy báo giá với mã: " + quotationCode));

                        // 3. Gọi xử lý business từ chối báo giá
                        priceQuotationService.rejectQuotationByCustomer(
                                        quotation.getPriceQuotationId(),
                                        rejectionReason);

                        // 4. Xóa token sau khi xử lý thành công
                        oneTimeTokenService.deleteToken(token);

                        // 5. Trả về status để Zalo hiện tin nhắn cho khách
                        Map<String, Object> response = Map.of(
                                        "error", 0,
                                        "message", "OK",
                                        "data", Map.of("status", "REJECTED"));

                        return ResponseEntity.ok(response);

                } catch (ResourceNotFoundException e) {
                        Map<String, Object> error = Map.of(
                                        "error", -1,
                                        "message", e.getMessage());
                        return ResponseEntity.badRequest().body(error);
                } catch (Exception e) {
                        Map<String, Object> error = Map.of(
                                        "error", -1,
                                        "message", "Invalid token or processing error: " + e.getMessage());
                        return ResponseEntity.badRequest().body(error);
                }
        }

        @PatchMapping("/{id}/draft")
        @Operation(summary = "Cập nhật báo giá về trạng thái DRAFT", description = "Chỉ cho phép cập nhật về DRAFT nếu phiếu dịch vụ chưa hoàn tất. Nếu phiếu dịch vụ đang chờ bàn giao xe thì chuyển về chờ báo giá.")
        public ResponseEntity<ApiResponse<PriceQuotationResponseDto>> updateToDraft(@PathVariable Long id) {
                PriceQuotationResponseDto responseDto = priceQuotationService.updateQuotationToDraft(id);
                return ResponseEntity.ok(ApiResponse.success("Cập nhật báo giá về DRAFT thành công", responseDto));
        }
}