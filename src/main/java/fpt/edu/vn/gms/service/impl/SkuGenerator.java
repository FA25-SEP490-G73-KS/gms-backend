package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.utils.SkuUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SkuGenerator {

    public String generateSku(Part part) {
        String cat = SkuUtils.toCode(part.getName());
        String modelName = (part.getVehicleModel() == null || part.getVehicleModel().getName() == null)
                ? "ALL"
                : part.getVehicleModel().getName();
        String market = SkuUtils.toCode(part.getMarket().getName());
        return cat + "-" + modelName + "-" + market;
    }
}