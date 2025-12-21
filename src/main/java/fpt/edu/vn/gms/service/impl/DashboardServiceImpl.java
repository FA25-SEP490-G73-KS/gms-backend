package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.PriceQuotationStatus;
import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
import fpt.edu.vn.gms.common.enums.StockLevelStatus;
import fpt.edu.vn.gms.dto.response.DashboardResponse;
import fpt.edu.vn.gms.dto.response.dashboard.DashboardOverviewResponse;
import fpt.edu.vn.gms.dto.response.dashboard.DashboardSeriesPoint;
import fpt.edu.vn.gms.dto.response.dashboard.MonthlyRevenueDto;
import fpt.edu.vn.gms.dto.response.dashboard.RevenueByYearDto;
import fpt.edu.vn.gms.dto.response.dashboard.ServiceTypeExpenseDto;
import fpt.edu.vn.gms.dto.response.dashboard.StatisticsResponse;
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
        LedgerVoucherRepository ledgerVoucherRepository;
        PartRepository partRepository;

        @Override
        public DashboardResponse getDashboardOverview(Integer year) {
                // Thời gian hiện tại
                LocalDate today = LocalDate.now();
                YearMonth currentMonth = YearMonth.from(today);
                // Nếu không có năm được truyền vào, sử dụng năm hiện tại
                int targetYear = (year == null || year <= 0) ? currentMonth.getYear() : year;

                long totalTickets = serviceTicketRepository.countServiceTicketsInMonth(currentMonth.getYear(),
                                currentMonth.getMonthValue());
                long pendingQuotations = priceQuotationRepository
                                .countByStatus(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM);
                long vehiclesInService = serviceTicketRepository.countByStatus(ServiceTicketStatus.UNDER_REPAIR);
                long appointmentsToday = appointmentRepository.countByAppointmentDate(today);

                DashboardResponse.SummaryDto summary = DashboardResponse.SummaryDto.builder()
                                .totalTickets(totalTickets)
                                .pendingQuotations(pendingQuotations)
                                .vehiclesInService(vehiclesInService)
                                .appointmentsToday(appointmentsToday)
                                .build();

                // Filter ticketsByMonth theo năm nếu có
                List<Object[]> ticketsByMonthRaw = serviceTicketRepository.getTicketsByMonth(targetYear);
                List<DashboardResponse.TicketsByMonthDto> ticketsByMonth = new ArrayList<>();
                for (Object[] row : ticketsByMonthRaw) {
                        int rowYear = ((Number) row[0]).intValue();
                        int month = ((Number) row[1]).intValue();
                        long total = ((Number) row[2]).longValue();
                        ticketsByMonth.add(DashboardResponse.TicketsByMonthDto.builder()
                                        .year(rowYear)
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

                List<Customer> topCustomersEntities = customerRepository
                                .findTop5ByIsActiveTrueOrderByTotalSpendingDesc();
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
        public DashboardOverviewResponse getFinancialOverview(Integer year, Integer month) {
                int targetYear = (year == null || year <= 0) ? Year.now().getValue() : year;
                // Validate month: 1-12 hoặc null
                Integer targetMonth = (month != null && month >= 1 && month <= 12) ? month : null;

                // Tính revenue: filter theo năm và tháng nếu có
                BigDecimal totalRevenue;
                if (targetMonth != null) {
                        Map<Integer, BigDecimal> revenueByMonth = toMonthMap(
                                        transactionRepository.sumRevenueByMonth(targetYear));
                        totalRevenue = revenueByMonth.getOrDefault(targetMonth, BigDecimal.ZERO);
                } else {
                        totalRevenue = nvl(transactionRepository.sumRevenueByYear(targetYear));
                }

                // Tính expense: chỉ tính từ ledger voucher (không tính từ stock receipt)
                BigDecimal totalExpense = nvl(ledgerVoucherRepository.sumExpenseByYearAndMonth(
                                targetYear, targetMonth));
                BigDecimal totalDebt = nvl(debtRepository.sumOutstandingDebt());
                BigDecimal profit = totalRevenue.subtract(totalExpense);

                // Series chỉ filter theo năm (không filter theo tháng)
                Map<Integer, BigDecimal> revenueByMonth = toMonthMap(
                                transactionRepository.sumRevenueByMonth(targetYear));
                // Expense series: tính từ ledger voucher theo tháng
                Map<Integer, BigDecimal> expenseByMonth = new java.util.HashMap<>();
                for (int m = 1; m <= 12; m++) {
                        BigDecimal monthExpense = nvl(ledgerVoucherRepository.sumExpenseByYearAndMonth(
                                        targetYear, m));
                        expenseByMonth.put(m, monthExpense);
                }

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

                // Tính phần trăm chi tiêu cho các loại dịch vụ
                List<ServiceTypeExpenseDto> serviceTypeExpenseDistribution = calculateServiceTypeExpenseDistribution(
                                targetYear, targetMonth, totalExpense);

                return DashboardOverviewResponse.builder()
                                .year(targetYear)
                                .month(targetMonth)
                                .totalRevenue(totalRevenue)
                                .totalExpense(totalExpense)
                                .profit(profit)
                                .totalDebt(totalDebt)
                                .series(series)
                                .serviceTypeExpenseDistribution(serviceTypeExpenseDistribution)
                                .build();
        }

        private List<ServiceTypeExpenseDto> calculateServiceTypeExpenseDistribution(Integer year, Integer month,
                        BigDecimal totalExpense) {
                List<ServiceTypeExpenseDto> result = new ArrayList<>();

                if (totalExpense.compareTo(BigDecimal.ZERO) == 0) {
                        return result;
                }

                // Lấy số lượng tickets theo service type
                List<Object[]> ticketsByType;
                if (month != null) {
                        ticketsByType = serviceTicketRepository.countTicketsByTypeForMonth(year, month);
                } else {
                        // Nếu không có tháng, tính tổng cho cả năm
                        Map<String, Long> ticketsByTypeMap = new java.util.HashMap<>();
                        for (int m = 1; m <= 12; m++) {
                                List<Object[]> monthData = serviceTicketRepository.countTicketsByTypeForMonth(year, m);
                                for (Object[] row : monthData) {
                                        String typeName = (String) row[0];
                                        long count = ((Number) row[1]).longValue();
                                        ticketsByTypeMap.put(typeName,
                                                        ticketsByTypeMap.getOrDefault(typeName, 0L) + count);
                                }
                        }
                        ticketsByType = ticketsByTypeMap.entrySet().stream()
                                        .map(e -> new Object[] { e.getKey(), e.getValue() })
                                        .collect(Collectors.toList());
                }

                if (ticketsByType == null || ticketsByType.isEmpty()) {
                        return result;
                }

                // Tính tổng số tickets
                long totalTickets = ticketsByType.stream()
                                .mapToLong(row -> ((Number) row[1]).longValue())
                                .sum();

                if (totalTickets == 0) {
                        return result;
                }

                // Tính phần trăm và amount cho mỗi service type
                for (Object[] row : ticketsByType) {
                        String serviceTypeName = (String) row[0];
                        long ticketCount = ((Number) row[1]).longValue();
                        double percentage = (ticketCount * 100.0) / totalTickets;
                        // Tính amount dựa trên phần trăm của total expense
                        BigDecimal amount = totalExpense.multiply(BigDecimal.valueOf(percentage))
                                        .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);

                        result.add(ServiceTypeExpenseDto.builder()
                                        .serviceTypeName(serviceTypeName)
                                        .amount(amount)
                                        .percentage(percentage)
                                        .build());
                }

                return result;
        }

        private Map<Integer, BigDecimal> toMonthMap(List<Object[]> rows) {
                if (rows == null)
                        return Map.of();
                return rows.stream()
                                .collect(Collectors.toMap(
                                                r -> ((Number) r[0]).intValue(),
                                                r -> nvl(convertToBigDecimal(r[1])),
                                                BigDecimal::add));
        }

        @Override
        public StatisticsResponse getStatistics(Integer fromYear, Integer toYear, Integer year, Integer month) {
                // Validate và set default values
                int targetYear = (year == null || year <= 0) ? Year.now().getValue() : year;
                Integer targetMonth = (month != null && month >= 1 && month <= 12) ? month : null;

                // 1. Profit = totalRevenue - totalExpense
                BigDecimal totalRevenue;
                if (targetMonth != null) {
                        Map<Integer, BigDecimal> revenueByMonth = toMonthMap(
                                        transactionRepository.sumRevenueByMonth(targetYear));
                        totalRevenue = revenueByMonth.getOrDefault(targetMonth, BigDecimal.ZERO);
                } else {
                        totalRevenue = nvl(transactionRepository.sumRevenueByYear(targetYear));
                }

                // Tính expense: chỉ tính từ ledger voucher (không tính từ stock receipt)
                BigDecimal totalExpense = nvl(ledgerVoucherRepository.sumExpenseByYearAndMonth(
                                targetYear, targetMonth));
                BigDecimal profit = totalRevenue.subtract(totalExpense);

                // 2. Tổng số phiếu dịch vụ
                long totalServiceTickets = serviceTicketRepository.countServiceTicketsByYearAndMonth(
                                targetYear, targetMonth);

                // 3. Số phụ tùng sắp hết và hết hàng
                List<StockLevelStatus> lowStatuses = List.of(StockLevelStatus.LOW_STOCK,
                                StockLevelStatus.OUT_OF_STOCK);
                long lowStockPartsCount = partRepository.countByStatusIn(lowStatuses);

                // 4. Tổng công nợ của khách
                BigDecimal totalDebt = nvl(debtRepository.sumOutstandingDebt());

                // 5. Chi tiêu cho từng service type
                List<ServiceTypeExpenseDto> serviceTypeExpenseDistribution = calculateServiceTypeExpenseDistribution(
                                targetYear, targetMonth, totalExpense);

                // 6. Doanh thu theo năm (filter từ năm đến năm)
                List<RevenueByYearDto> revenueByYear = calculateRevenueByYear(fromYear, toYear);

                return StatisticsResponse.builder()
                                .year(targetYear)
                                .month(targetMonth)
                                .profit(profit)
                                .totalRevenue(totalRevenue)
                                .totalExpense(totalExpense)
                                .totalServiceTickets(totalServiceTickets)
                                .lowStockPartsCount(lowStockPartsCount)
                                .totalDebt(totalDebt)
                                .serviceTypeExpenseDistribution(serviceTypeExpenseDistribution)
                                .revenueByYear(revenueByYear)
                                .build();
        }

        /**
         * Tính doanh thu theo năm (từ năm đến năm) với chi tiết theo tháng
         */
        private List<RevenueByYearDto> calculateRevenueByYear(Integer fromYear, Integer toYear) {
                List<RevenueByYearDto> result = new ArrayList<>();

                // Lấy danh sách năm và tổng doanh thu
                List<Object[]> revenueByYearRaw = transactionRepository.sumRevenueByYearRange(fromYear, toYear);

                if (revenueByYearRaw == null || revenueByYearRaw.isEmpty()) {
                        return result;
                }

                // Với mỗi năm, lấy chi tiết doanh thu theo tháng
                for (Object[] row : revenueByYearRaw) {
                        int year = ((Number) row[0]).intValue();
                        BigDecimal totalRevenue = nvl(convertToBigDecimal(row[1]));

                        // Lấy doanh thu theo tháng của năm này
                        Map<Integer, BigDecimal> revenueByMonth = toMonthMap(
                                        transactionRepository.sumRevenueByMonth(year));

                        // Tạo danh sách monthly revenue
                        List<MonthlyRevenueDto> monthlyRevenue = new ArrayList<>();
                        for (int m = 1; m <= 12; m++) {
                                monthlyRevenue.add(MonthlyRevenueDto.builder()
                                                .month(m)
                                                .revenue(revenueByMonth.getOrDefault(m, BigDecimal.ZERO))
                                                .build());
                        }

                        result.add(RevenueByYearDto.builder()
                                        .year(year)
                                        .totalRevenue(totalRevenue)
                                        .monthlyRevenue(monthlyRevenue)
                                        .build());
                }

                return result;
        }

        private BigDecimal nvl(BigDecimal value) {
                return value == null ? BigDecimal.ZERO : value;
        }

        private BigDecimal convertToBigDecimal(Object value) {
                if (value == null) {
                        return BigDecimal.ZERO;
                }
                if (value instanceof BigDecimal) {
                        return (BigDecimal) value;
                }
                if (value instanceof Number) {
                        return BigDecimal.valueOf(((Number) value).doubleValue());
                }
                return BigDecimal.ZERO;
        }
}
