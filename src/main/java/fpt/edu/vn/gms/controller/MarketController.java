package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.entity.Market;
import fpt.edu.vn.gms.service.MarketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static fpt.edu.vn.gms.utils.AppRoutes.MARKET_PREFIX;

@Tag(name = "part_market", description = "Quản lý thị trường nhập linh kiện")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = MARKET_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarketController {

    MarketService marketService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Market>>> getAllMarkets() {

        List<Market> markets = marketService.getAll();
        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách thị trường", markets)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Market>> getMarketById(
            @PathVariable Long id
    ) {

        Market markets = marketService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Thị trường với id " + id, markets)
        );
    }
}
