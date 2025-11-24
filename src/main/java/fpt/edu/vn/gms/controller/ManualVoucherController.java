package fpt.edu.vn.gms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.edu.vn.gms.common.annotations.CurrentUser;
import fpt.edu.vn.gms.dto.request.ManualVoucherCreateRequest;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.ManualVoucherResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.service.ManualVoucherService;
import fpt.edu.vn.gms.utils.AppRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "manual-voucher", description = "Phiếu thu-chi")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = AppRoutes.MANUAL_VOUCHER_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ManualVoucherController {

    ManualVoucherService manualVoucherService;
    private final ObjectMapper objectMapper;

    @Operation(
            summary = "Tạo phiếu THU / CHI thủ công",
            description = """
                Tạo phiếu thu hoặc chi thủ công (không phải giao dịch thật).
               \s
                • type = THU | CHI \s
                • amount: số tiền \s
                • target: đối tượng thu/chi \s
                • description: nội dung \s
                • attachmentUrl: link file chứng từ (nếu có) \s
                • approvedByEmployeeId: người duyệt
               \s
                Phiếu tạo ra sẽ có trạng thái PENDING.
               \s"""
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Tạo phiếu thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu không hợp lệ"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy người duyệt"
            )
    })
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ManualVoucherResponseDto>> createManualVoucher(
            @RequestPart("data") String jsonData,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @CurrentUser Employee creator
    ) throws JsonProcessingException {

        // parse JSON
        ManualVoucherCreateRequest request =
                objectMapper.readValue(jsonData, ManualVoucherCreateRequest.class);

        ManualVoucherResponseDto dto = manualVoucherService.create(request, file, creator);

        return ResponseEntity.status(201)
                .body(ApiResponse.created("Tạo phiếu thu/chi thành công", dto));
    }
}

