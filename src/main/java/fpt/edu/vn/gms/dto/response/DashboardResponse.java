package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardResponse {

    private SummaryDto summary;
    private List<TicketsByMonthDto> ticketsByMonth;
    private List<ServiceTypeDistributionDto> serviceTypeDistribution;
    private RatingSummaryDto rating;
    private List<TopCustomerDto> topCustomers;

    @Data
    @Builder
    public static class SummaryDto {
        private long totalTickets;
        private long pendingQuotations;
        private long vehiclesInService;
        private long appointmentsToday;
    }

    @Data
    @Builder
    public static class TicketsByMonthDto {
        private int year;
        private int month;
        private long total;
    }

    @Data
    @Builder
    public static class ServiceTypeDistributionDto {
        private String name;
        private long total;
        private double percentage;
    }

    @Data
    @Builder
    public static class RatingSummaryDto {
        private long total;
        private long star1;
        private long star2;
        private long star3;
        private long star4;
        private long star5;
    }

    @Data
    @Builder
    public static class TopCustomerDto {
        private String name;
        private String phone;
        private java.math.BigDecimal spending;
    }
}

