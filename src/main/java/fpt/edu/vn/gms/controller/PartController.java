package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.PartReqDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PartResDto;
import fpt.edu.vn.gms.service.PartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping("/api/parts")
@RequiredArgsConstructor
public class PartController {

    private final PartService partService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PartResDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
            ) {

        Page<PartResDto> partList = partService.getAllPart(page, size);

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Danh sách linh kiện", partList));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PartResDto>> createPart(
            @RequestBody PartReqDto part
    ) {

        PartResDto resDto = partService.createPart(part);
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Thêm linh kiện!!!!", resDto));
    }

    @GetMapping("/category")
    public ResponseEntity<ApiResponse<Page<PartResDto>>> getPartByCategory(
            @RequestParam String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {

        Page<PartResDto> resDtoPage = partService.getPartByCategory(categoryName, page, size);
        return ResponseEntity.status(200)
                .body(ApiResponse.success("Part có category " + categoryName, resDtoPage));
    }
}
