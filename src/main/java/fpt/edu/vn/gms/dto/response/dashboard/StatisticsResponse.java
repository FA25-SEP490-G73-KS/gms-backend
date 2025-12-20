package fpt.edu.vn.gms.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    // Filter theo năm và tháng
    private Integer year;
    private Integer month;
    
    // Profit = totalRevenue - totalExpense
    private BigDecimal profit;
    private BigDecimal totalRevenue;
    private BigDecimal totalExpense;
    
    // Tổng số phiếu dịch vụ
    private Long totalServiceTickets;
    
    // Số phụ tùng sắp hết và hết hàng
    private Long lowStockPartsCount;
    
    // Tổng công nợ của khách
    private BigDecimal totalDebt;
    
    // Chi tiêu cho từng service type
    private List<ServiceTypeExpenseDto> serviceTypeExpenseDistribution;
    
    // Doanh thu theo năm (filter từ năm đến năm)
    private List<RevenueByYearDto> revenueByYear;
}

