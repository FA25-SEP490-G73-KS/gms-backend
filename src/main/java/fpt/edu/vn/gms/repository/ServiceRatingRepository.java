package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.ServiceRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ServiceRatingRepository extends JpaRepository<ServiceRating, Long> {

    @Query("SELECT COUNT(sr) FROM ServiceRating sr")
    long countAllRatings();

    long countByStars(int stars);
}
