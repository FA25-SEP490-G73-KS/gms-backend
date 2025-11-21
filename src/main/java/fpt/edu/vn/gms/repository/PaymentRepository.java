package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query(value = """
        SELECT p FROM Payment p
        JOIN FETCH p.serviceTicket st
        JOIN FETCH st.customer c
        JOIN FETCH st.vehicle v
        """,
            countQuery = """
        SELECT COUNT(p) FROM Payment p
        """
    )
    Page<Payment> findAllWithRelations(Pageable pageable);


}
