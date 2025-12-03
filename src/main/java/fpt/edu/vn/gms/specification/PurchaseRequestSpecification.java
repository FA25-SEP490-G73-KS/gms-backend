package fpt.edu.vn.gms.specification;

import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import fpt.edu.vn.gms.entity.PurchaseRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PurchaseRequestSpecification {

    public static Specification<PurchaseRequest> build(String keyword, String status, String fromDate, String toDate) {
        Specification<PurchaseRequest> spec = null;

        if (keyword != null && !keyword.isBlank()) {
            spec = and(spec, keyword(keyword));
        }
        if (status != null && !status.isBlank()) {
            spec = and(spec, status(status));
        }
        if (fromDate != null && !fromDate.isBlank()) {
            spec = and(spec, createdFrom(fromDate));
        }
        if (toDate != null && !toDate.isBlank()) {
            spec = and(spec, createdTo(toDate));
        }
        return spec;
    }

    private static Specification<PurchaseRequest> and(Specification<PurchaseRequest> base, Specification<PurchaseRequest> next) {
        if (base == null) return next;
        if (next == null) return base;
        return base.and(next);
    }

    private static Specification<PurchaseRequest> keyword(String keyword) {
        return (root, query, cb) -> {
            String like = "%" + keyword.toLowerCase() + "%";
            var prCode = cb.lower(root.get("code"));
            var quotationCode = cb.lower(root.join("relatedQuotation").get("code"));
            var customerName = cb.lower(root.join("relatedQuotation").join("serviceTicket").join("customer").get("fullName"));
            return cb.or(
                    cb.like(prCode, like),
                    cb.like(quotationCode, like),
                    cb.like(customerName, like)
            );
        };
    }

    private static Specification<PurchaseRequest> status(String status) {
        return (root, query, cb) -> {
            ManagerReviewStatus enumStatus;
            switch (status.toLowerCase()) {
                case "chờ duyệt" -> enumStatus = ManagerReviewStatus.PENDING;
                case "đã duyệt" -> enumStatus = ManagerReviewStatus.APPROVED;
                case "từ chối" -> enumStatus = ManagerReviewStatus.REJECTED;
                default -> {
                    try {
                        enumStatus = ManagerReviewStatus.valueOf(status.toUpperCase());
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
            return cb.equal(root.get("reviewStatus"), enumStatus);
        };
    }

    private static Specification<PurchaseRequest> createdFrom(String from) {
        return (root, query, cb) -> {
            LocalDate date = LocalDate.parse(from, DateTimeFormatter.ISO_DATE);
            LocalDateTime start = date.atStartOfDay();
            return cb.greaterThanOrEqualTo(root.get("createdAt"), start);
        };
    }

    private static Specification<PurchaseRequest> createdTo(String to) {
        return (root, query, cb) -> {
            LocalDate date = LocalDate.parse(to, DateTimeFormatter.ISO_DATE);
            LocalDateTime end = date.atTime(23, 59, 59);
            return cb.lessThanOrEqualTo(root.get("createdAt"), end);
        };
    }
}
