package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query(value = """
            SELECT i FROM Invoice i
            JOIN FETCH i.serviceTicket st
            JOIN FETCH st.customer c
            JOIN FETCH st.vehicle v
            """, countQuery = """
            SELECT COUNT(i) FROM Invoice i
            """)
    Page<Invoice> findAllWithRelations(Pageable pageable);

}
