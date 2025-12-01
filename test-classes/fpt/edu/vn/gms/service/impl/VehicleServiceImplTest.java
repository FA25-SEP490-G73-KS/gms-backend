package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.BrandDto;
import fpt.edu.vn.gms.dto.VehicleInfoDto;
import fpt.edu.vn.gms.dto.VehicleModelDto;
import fpt.edu.vn.gms.dto.response.LicensePlateCheckResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.repository.BrandRepository;
import fpt.edu.vn.gms.repository.VehicleModelRepository;
import fpt.edu.vn.gms.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    VehicleRepository vehicleRep;
    @Mock
    VehicleModelRepository vehicleModelRepository;
    @Mock
    BrandRepository brandRepository;

    @InjectMocks
    VehicleServiceImpl service;

    @Test
    void getAllBrands_ShouldReturnListOfBrandDtos() {
        Brand brand1 = Brand.builder().brandId(1L).name("Toyota").build();
        Brand brand2 = Brand.builder().brandId(2L).name("Honda").build();
        when(brandRepository.findAll()).thenReturn(List.of(brand1, brand2));

        List<BrandDto> result = service.getAllBrands();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Toyota", result.get(0).name());
        assertEquals(2L, result.get(1).id());
        assertEquals("Honda", result.get(1).name());
        verify(brandRepository).findAll();
    }

    @Test
    void getModelsByBrand_ShouldReturnListOfVehicleModelDtos() {
        Brand brand = Brand.builder().brandId(1L).name("Toyota").build();
        VehicleModel model1 = VehicleModel.builder().vehicleModelId(10L).name("Camry").brand(brand).build();
        VehicleModel model2 = VehicleModel.builder().vehicleModelId(11L).name("Corolla").brand(brand).build();
        when(vehicleModelRepository.findByBrandBrandId(1L)).thenReturn(List.of(model1, model2));

        List<VehicleModelDto> result = service.getModelsByBrand(1L);

        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).id());
        assertEquals("Camry", result.get(0).name());
        assertEquals(11L, result.get(1).id());
        assertEquals("Corolla", result.get(1).name());
        verify(vehicleModelRepository).findByBrandBrandId(1L);
    }

    @Test
    void findByLicensePlate_ShouldReturnVehicleInfoDto_WhenFound() {
        Brand brand = Brand.builder().brandId(1L).name("Toyota").build();
        VehicleModel model = VehicleModel.builder()
                .vehicleModelId(10L)
                .name("Camry")
                .brand(brand)
                .build();
        Vehicle vehicle = Vehicle.builder()
                .vehicleId(100L)
                .licensePlate("30A-12345")
                .vehicleModel(model)
                .vin("VIN123456")
                .year(2020)
                .build();

        when(vehicleRep.findByLicensePlate("30A-12345")).thenReturn(Optional.of(vehicle));

        VehicleInfoDto result = service.findByLicensePlate("30A-12345");

        assertNotNull(result);
        assertEquals(100L, result.getVehicleId());
        assertEquals("30A-12345", result.getLicensePlate());
        assertEquals("Toyota", result.getBrandName());
        assertEquals("Camry", result.getModelName());
        assertEquals("VIN123456", result.getVin());
        assertEquals(2020, result.getYear());
        verify(vehicleRep).findByLicensePlate("30A-12345");
    }

    @Test
    void findByLicensePlate_ShouldThrow_WhenNotFound() {
        when(vehicleRep.findByLicensePlate("30A-99999")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.findByLicensePlate("30A-99999"));
        verify(vehicleRep).findByLicensePlate("30A-99999");
    }

    @Test
    void existsByLicensePlate_ShouldReturnTrue_WhenExists() {
        Vehicle vehicle = Vehicle.builder().vehicleId(1L).licensePlate("30A-12345").build();
        when(vehicleRep.findByLicensePlate("30A-12345")).thenReturn(Optional.of(vehicle));

        boolean result = service.existsByLicensePlate("30A-12345");

        assertTrue(result);
        verify(vehicleRep).findByLicensePlate("30A-12345");
    }

    @Test
    void existsByLicensePlate_ShouldReturnFalse_WhenNotExists() {
        when(vehicleRep.findByLicensePlate("30A-99999")).thenReturn(Optional.empty());

        boolean result = service.existsByLicensePlate("30A-99999");

        assertFalse(result);
        verify(vehicleRep).findByLicensePlate("30A-99999");
    }

    @Test
    void isLicensePlateOwnedByCustomer_ShouldReturnTrue_WhenOwned() {
        Customer customer = Customer.builder().customerId(1L).build();
        Vehicle vehicle = Vehicle.builder()
                .vehicleId(100L)
                .licensePlate("30A-12345")
                .customer(customer)
                .build();
        when(vehicleRep.findByLicensePlateAndCustomer_CustomerId("30A-12345", 1L))
                .thenReturn(Optional.of(vehicle));

        boolean result = service.isLicensePlateOwnedByCustomer("30A-12345", 1L);

        assertTrue(result);
        verify(vehicleRep).findByLicensePlateAndCustomer_CustomerId("30A-12345", 1L);
    }

    @Test
    void isLicensePlateOwnedByCustomer_ShouldReturnFalse_WhenNotOwned() {
        when(vehicleRep.findByLicensePlateAndCustomer_CustomerId("30A-12345", 1L))
                .thenReturn(Optional.empty());

        boolean result = service.isLicensePlateOwnedByCustomer("30A-12345", 1L);

        assertFalse(result);
        verify(vehicleRep).findByLicensePlateAndCustomer_CustomerId("30A-12345", 1L);
    }

    @Test
    void checkLicensePlateAndCustomer_ShouldReturnNotExists_WhenVehicleNotFound() {
        when(vehicleRep.findByLicensePlate("30A-99999")).thenReturn(Optional.empty());

        LicensePlateCheckResponseDto result = service.checkLicensePlateAndCustomer("30A-99999", 1L);

        assertNotNull(result);
        assertFalse(result.isExists());
        assertFalse(result.isSameCustomer());
        assertEquals("30A-99999", result.getLicensePlate());
        assertNull(result.getCustomerName());
        assertNull(result.getCustomerPhone());
        verify(vehicleRep).findByLicensePlate("30A-99999");
    }

    @Test
    void checkLicensePlateAndCustomer_ShouldReturnSameCustomer_WhenVehicleBelongsToCustomer() {
        Customer customer = Customer.builder()
                .customerId(1L)
                .fullName("Nguyen Van A")
                .phone("0912345678")
                .build();
        Brand brand = Brand.builder().brandId(1L).name("Toyota").build();
        VehicleModel model = VehicleModel.builder()
                .vehicleModelId(10L)
                .name("Camry")
                .brand(brand)
                .build();
        Vehicle vehicle = Vehicle.builder()
                .vehicleId(100L)
                .licensePlate("30A-12345")
                .customer(customer)
                .vehicleModel(model)
                .build();

        when(vehicleRep.findByLicensePlate("30A-12345")).thenReturn(Optional.of(vehicle));

        LicensePlateCheckResponseDto result = service.checkLicensePlateAndCustomer("30A-12345", 1L);

        assertNotNull(result);
        assertTrue(result.isExists());
        assertTrue(result.isSameCustomer());
        assertEquals("30A-12345", result.getLicensePlate());
        assertEquals("Nguyen Van A", result.getCustomerName());
        assertEquals("0912345678", result.getCustomerPhone());
        verify(vehicleRep).findByLicensePlate("30A-12345");
    }

    @Test
    void checkLicensePlateAndCustomer_ShouldReturnDifferentCustomer_WhenVehicleBelongsToOtherCustomer() {
        Customer customer = Customer.builder()
                .customerId(2L)
                .fullName("Tran Van B")
                .phone("0987654321")
                .build();
        Brand brand = Brand.builder().brandId(1L).name("Toyota").build();
        VehicleModel model = VehicleModel.builder()
                .vehicleModelId(10L)
                .name("Camry")
                .brand(brand)
                .build();
        Vehicle vehicle = Vehicle.builder()
                .vehicleId(100L)
                .licensePlate("30A-12345")
                .customer(customer)
                .vehicleModel(model)
                .build();

        when(vehicleRep.findByLicensePlate("30A-12345")).thenReturn(Optional.of(vehicle));

        LicensePlateCheckResponseDto result = service.checkLicensePlateAndCustomer("30A-12345", 1L);

        assertNotNull(result);
        assertTrue(result.isExists());
        assertFalse(result.isSameCustomer());
        assertEquals("30A-12345", result.getLicensePlate());
        assertEquals("Tran Van B", result.getCustomerName());
        assertEquals("0987654321", result.getCustomerPhone());
        verify(vehicleRep).findByLicensePlate("30A-12345");
    }

    @Test
    void checkLicensePlateAndCustomer_ShouldReturnNoCustomer_WhenVehicleHasNoCustomer() {
        Brand brand = Brand.builder().brandId(1L).name("Toyota").build();
        VehicleModel model = VehicleModel.builder()
                .vehicleModelId(10L)
                .name("Camry")
                .brand(brand)
                .build();
        Vehicle vehicle = Vehicle.builder()
                .vehicleId(100L)
                .licensePlate("30A-12345")
                .customer(null)
                .vehicleModel(model)
                .build();

        when(vehicleRep.findByLicensePlate("30A-12345")).thenReturn(Optional.of(vehicle));

        LicensePlateCheckResponseDto result = service.checkLicensePlateAndCustomer("30A-12345", 1L);

        assertNotNull(result);
        assertTrue(result.isExists());
        assertFalse(result.isSameCustomer());
        assertEquals("30A-12345", result.getLicensePlate());
        assertNull(result.getCustomerName());
        assertNull(result.getCustomerPhone());
        verify(vehicleRep).findByLicensePlate("30A-12345");
    }
}

