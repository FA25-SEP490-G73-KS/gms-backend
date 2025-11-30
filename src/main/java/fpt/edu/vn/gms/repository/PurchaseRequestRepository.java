package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.entity.PurchaseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {

    @Query(
            value = """
        SELECT new fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto(
            pr.id,
            pr.code,
            pq.code,
            st.customer.fullName,
            v.licensePlate,
            st.createdBy.fullName,
            pr.status,
            pr.reviewStatus,
            pr.totalEstimatedAmount,
            pr.createdAt
        )
        FROM PurchaseRequest pr
        LEFT JOIN pr.relatedQuotation pq
        LEFT JOIN pq.serviceTicket st
        LEFT JOIN st.vehicle v
        ORDER BY pr.createdAt DESC
    """,
            countQuery = """
        SELECT COUNT(pr)
        FROM PurchaseRequest pr
        LEFT JOIN pr.relatedQuotation pq
        LEFT JOIN pq.serviceTicket st
        LEFT JOIN st.vehicle v
    """
    )
    Page<PurchaseRequestResponseDto> findAllCustom(Pageable pageable);
    }
