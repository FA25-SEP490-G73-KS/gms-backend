package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.PaymentVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentVoucherRepository extends JpaRepository<PaymentVoucher, Long> {

    @Query("SELECT v FROM PaymentVoucher v WHERE v.quotationId.priceQuotationId = :quotationId AND v.type = :type")
    Optional<PaymentVoucher> findByQuotationIdAndType(
            @Param("quotationId") Long quotationId,
            @Param("type") PaymentVoucher.VoucherType type
    );

    @Query("""
        SELECT v FROM PaymentVoucher v
        LEFT JOIN v.serviceTicketId st
        LEFT JOIN st.vehicle veh
        WHERE (:phone IS NULL OR st.customerPhone LIKE %:phone%)
          AND (:vehicle IS NULL OR veh.licensePlate LIKE %:vehicle%)
        """)
    List<PaymentVoucher> search(
            @Param("phone") String phone,
            @Param("vehicle") String vehicle
    );

    List<PaymentVoucher> findByQuotationId_PriceQuotationId(Long quotationIdPriceQuotationId);
}
