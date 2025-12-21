package fpt.edu.vn.gms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestQuotationId implements Serializable {
    private Long purchaseRequest;
    private Long priceQuotation;
}

