package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.Market;
import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.entity.VehicleModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SkuGeneratorTest {

    @InjectMocks
    SkuGenerator generator;

    @Test
    void generateSku_ShouldGenerateSkuWithVehicleModel() {
        Market market = Market.builder()
                .id(1L)
                .name("OEM")
                .build();
        VehicleModel model = VehicleModel.builder()
                .vehicleModelId(1L)
                .name("Camry")
                .build();
        Part part = Part.builder()
                .partId(1L)
                .name("Brake Pad")
                .market(market)
                .vehicleModel(model)
                .build();

        String sku = generator.generateSku(part);

        assertNotNull(sku);
        assertTrue(sku.contains("BRAKE"));
        assertTrue(sku.contains("Camry"));
        assertTrue(sku.contains("OEM"));
        assertTrue(sku.contains("-"));
    }

    @Test
    void generateSku_ShouldUseAll_WhenVehicleModelIsNull() {
        Market market = Market.builder()
                .id(1L)
                .name("Aftermarket")
                .build();
        Part part = Part.builder()
                .partId(1L)
                .name("Oil Filter")
                .market(market)
                .vehicleModel(null)
                .build();

        String sku = generator.generateSku(part);

        assertNotNull(sku);
        assertTrue(sku.contains("ALL"));
        assertTrue(sku.contains("AFTERMARKET"));
    }

    @Test
    void generateSku_ShouldUseAll_WhenVehicleModelNameIsNull() {
        Market market = Market.builder()
                .id(1L)
                .name("OEM")
                .build();
        VehicleModel model = VehicleModel.builder()
                .vehicleModelId(1L)
                .name(null)
                .build();
        Part part = Part.builder()
                .partId(1L)
                .name("Air Filter")
                .market(market)
                .vehicleModel(model)
                .build();

        String sku = generator.generateSku(part);

        assertNotNull(sku);
        assertTrue(sku.contains("ALL"));
    }
}

