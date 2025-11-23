package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
