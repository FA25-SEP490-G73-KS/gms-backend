package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);

    /**
     * Tìm xe theo biển số và mã khách hàng
     * @param licensePlate
     * @param customerId
     * @return
     */
    Optional<Vehicle> findByLicensePlateAndCustomer_CustomerId(String licensePlate, Long customerId);
}
