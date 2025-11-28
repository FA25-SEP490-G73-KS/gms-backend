package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.dto.VehicleInfoDto;
import fpt.edu.vn.gms.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);

    Optional<Vehicle> findByLicensePlateAndCustomer_CustomerId(String licensePlate, Long customerId);

    @Query("""
    SELECT v
    FROM Vehicle v
    LEFT JOIN FETCH v.vehicleModel vm
    LEFT JOIN FETCH vm.brand
    WHERE v.vehicleId = :id
""")
    Optional<Vehicle> findDetailById(@Param("id") Long id);

    @Query("""
    SELECT new fpt.edu.vn.gms.dto.VehicleInfoDto(
        v.licensePlate,
        vm.brand.name,
        vm.name,
        v.vin,
        v.year
    )
    FROM Vehicle v
    JOIN v.vehicleModel vm
    WHERE v.customer.customerId = :customerId
    """)
    List<VehicleInfoDto> getCustomerVehicles(Long customerId);
}
