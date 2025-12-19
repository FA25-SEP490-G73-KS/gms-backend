package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.PriceQuotationStatus;
import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.response.DashboardResponse;
import fpt.edu.vn.gms.dto.response.dashboard.DashboardOverviewResponse;
import fpt.edu.vn.gms.dto.response.dashboard.DashboardSeriesPoint;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.DashboardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    // Existing dependencies for service-advisor dashboard
    ServiceTicketRepository serviceTicketRepository;
    PriceQuotationRepository priceQuotationRepository;
    AppointmentRepository appointmentRepository;
    ServiceRatingRepository serviceRatingRepository;
    CustomerRepository customerRepository;

    // New dependencies for financial overview
    TransactionRepository transactionRepository;
    StockReceiptRepository stockReceiptRepository;
    DebtRepository debtRepository;

    @Override
    public DashboardResponse getDashboardOverview() {
        // Thời gian hiện tại
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        long totalTickets = serviceTicketRepository.countServiceTicketsInMonth(currentMonth.getYear(),
                currentMonth.getMonthValue());
        long pendingQuotations = priceQuotationRepository.countByStatus(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM);
        long vehiclesInService = serviceTicketRepository.countByStatus(ServiceTicketStatus.QUOTING);
        long appointmentsToday = appointmentRepository.countByAppointmentDate(today);

        DashboardResponse.SummaryDto summary = DashboardResponse.SummaryDto.builder()
                .totalTickets(totalTickets)
                .pendingQuotations(pendingQuotations)
                .vehiclesInService(vehiclesInService)
                .appointmentsToday(appointmentsToday)
                .build();

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

        List<Object[]> typeDistRaw = serviceTicketRepository.countTicketsByTypeForMonth(currentMonth.getYear(),
                currentMonth.getMonthValue());
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

    @Override
    public DashboardOverviewResponse getFinancialOverview(Integer year) {
        int targetYear = (year == null || year <= 0) ? Year.now().getValue() : year;

        BigDecimal totalRevenue = nvl(transactionRepository.sumRevenueByYear(targetYear));
        BigDecimal totalExpense = nvl(stockReceiptRepository.sumExpenseByYear(targetYear));
        BigDecimal totalDebt = nvl(debtRepository.sumOutstandingDebt());
        BigDecimal profit = totalRevenue.subtract(totalExpense);

        Map<Integer, BigDecimal> revenueByMonth = toMonthMap(transactionRepository.sumRevenueByMonth(targetYear));
        Map<Integer, BigDecimal> expenseByMonth = toMonthMap(stockReceiptRepository.sumExpenseByMonth(targetYear));

        List<DashboardSeriesPoint> series = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            series.add(DashboardSeriesPoint.builder()
                    .month(m)
                    .revenue(revenueByMonth.getOrDefault(m, BigDecimal.ZERO))
                    .expense(expenseByMonth.getOrDefault(m, BigDecimal.ZERO))
                    .build());
        }

        series = series.stream()
                .sorted(Comparator.comparingInt(DashboardSeriesPoint::getMonth))
                .toList();

        return DashboardOverviewResponse.builder()
                .year(targetYear)
                .totalRevenue(totalRevenue)
                .totalExpense(totalExpense)
                .profit(profit)
                .totalDebt(totalDebt)
                .series(series)
                .build();
    }

    private Map<Integer, BigDecimal> toMonthMap(List<Object[]> rows) {
        if (rows == null)
            return Map.of();
        return rows.stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).intValue(),
                        r -> nvl((BigDecimal) r[1]),
                        BigDecimal::add));
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
