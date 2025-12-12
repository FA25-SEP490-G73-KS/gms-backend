package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findByPhoneAndOtpCodeAndIsVerifiedFalseAndExpiresAtAfter(
            String phone, String otpCode, LocalDateTime now);

    Optional<OtpVerification> findTopByPhoneAndPurposeAndIsVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String phone, String purpose, LocalDateTime now);

    @Modifying
    @Query("""
        DELETE FROM OtpVerification o
        WHERE o.phone = :phone AND o.otpId <> :newOtpId
    """)
    void deleteOldOtps(@Param("phone") String phone, @Param("newOtpId")  Long newOtpId);


    @Modifying
    @Query("UPDATE OtpVerification o SET o.isVerified = true WHERE o.phone = :phone AND o.purpose = :purpose AND o.isVerified = false")
    void markAsVerified(@Param("phone") String phone, @Param("purpose") String purpose);
}

