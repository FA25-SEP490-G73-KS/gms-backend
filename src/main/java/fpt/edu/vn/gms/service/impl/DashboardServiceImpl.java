package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.PriceQuotationStatus;
import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.response.DashboardResponse;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.DashboardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardServiceImpl implements DashboardService {

    ServiceTicketRepository serviceTicketRepository;
    PriceQuotationRepository priceQuotationRepository;
    AppointmentRepository appointmentRepository;
    ServiceRatingRepository serviceRatingRepository;
    CustomerRepository customerRepository;

    @Override
    public DashboardResponse getDashboardOverview() {
        // Thời gian hiện tại
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        // 1) Summary
        long totalTickets = serviceTicketRepository.countServiceTicketsInMonth(currentMonth.getYear(), currentMonth.getMonthValue());
        // "báo giá chờ duyệt" giả định là WAITING_CUSTOMER_CONFIRM
        long pendingQuotations = priceQuotationRepository.countByStatus(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM);
        // "xe đang sửa" giả định là trạng thái WAITING_FOR_DELIVERY (đang trong quá trình sửa/bàn giao)
        long vehiclesInService = serviceTicketRepository.countByStatus(ServiceTicketStatus.WAITING_FOR_QUOTATION);
        long appointmentsToday = appointmentRepository.countByAppointmentDate(today);

        DashboardResponse.SummaryDto summary = DashboardResponse.SummaryDto.builder()
                .totalTickets(totalTickets)
                .pendingQuotations(pendingQuotations)
                .vehiclesInService(vehiclesInService)
                .appointmentsToday(appointmentsToday)
                .build();

        // 2) Tickets by month
        List<Object[]> ticketsByMonthRaw = serviceTicketRepository.getTicketsByMonth();
        List<DashboardResponse.TicketsByMonthDto> ticketsByMonth = new ArrayList<>();
        for (Object[] row : ticketsByMonthRaw) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            long total = ((Number) row[2]).longValue();
            ticketsByMonth.add(DashboardResponse.TicketsByMonthDto.builder()
                    .year(year)
                    .month(month)
                    .total(total)
                    .build());
        }

        // 3) Service type distribution
        // Dùng query countTicketsByTypeForMonth cho tháng hiện tại
        List<Object[]> typeDistRaw = serviceTicketRepository.countTicketsByTypeForMonth(currentMonth.getYear(), currentMonth.getMonthValue());
        long totalTypeTickets = 0L;
        for (Object[] row : typeDistRaw) {
            totalTypeTickets += ((Number) row[1]).longValue();
        }
        List<DashboardResponse.ServiceTypeDistributionDto> serviceTypeDistribution = new ArrayList<>();
        for (Object[] row : typeDistRaw) {
            String name = (String) row[0];
            long total = ((Number) row[1]).longValue();
            double percentage = totalTypeTickets == 0 ? 0.0 : (total * 100.0) / totalTypeTickets;
            serviceTypeDistribution.add(DashboardResponse.ServiceTypeDistributionDto.builder()
                    .name(name)
                    .total(total)
                    .percentage(percentage)
                    .build());
        }

        // 4) Rating summary
        long totalRatings = serviceRatingRepository.countAllRatings();
        long star1 = serviceRatingRepository.countByStars(1);
        long star2 = serviceRatingRepository.countByStars(2);
        long star3 = serviceRatingRepository.countByStars(3);
        long star4 = serviceRatingRepository.countByStars(4);
        long star5 = serviceRatingRepository.countByStars(5);

        DashboardResponse.RatingSummaryDto ratingSummary = DashboardResponse.RatingSummaryDto.builder()
                .total(totalRatings)
                .star1(star1)
                .star2(star2)
                .star3(star3)
                .star4(star4)
                .star5(star5)
                .build();

        // 5) Top customers
        List<Customer> topCustomersEntities = customerRepository.findTop5ByIsActiveTrueOrderByTotalSpendingDesc();
        List<DashboardResponse.TopCustomerDto> topCustomers = new ArrayList<>();
        for (Customer c : topCustomersEntities) {
            BigDecimal spending = c.getTotalSpending() != null ? c.getTotalSpending() : BigDecimal.ZERO;
            topCustomers.add(DashboardResponse.TopCustomerDto.builder()
                    .name(c.getFullName())
                    .phone(c.getPhone())
                    .spending(spending)
                    .build());
        }

        return DashboardResponse.builder()
                .summary(summary)
                .ticketsByMonth(ticketsByMonth)
                .serviceTypeDistribution(serviceTypeDistribution)
                .rating(ratingSummary)
                .topCustomers(topCustomers)
                .build();
    }
}
