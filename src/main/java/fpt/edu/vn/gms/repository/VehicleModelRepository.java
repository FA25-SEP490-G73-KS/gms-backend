package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleModelRepository extends JpaRepository<VehicleModel, Long> {

    List<VehicleModel> findByBrandBrandId(Long brandId);
}
