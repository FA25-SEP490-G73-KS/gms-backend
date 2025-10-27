package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.service.PriceQuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
public class PriceQuotationController {

    private final PriceQuotationService priceQuotationService;

}