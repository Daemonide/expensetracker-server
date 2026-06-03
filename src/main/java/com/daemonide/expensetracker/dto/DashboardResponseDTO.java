package com.daemonide.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponseDTO {

    private double totalSpent;
    private double thisMonthSpent;
    private double pendingAmount;
    private double completedAmount;

    private long totalExpenses;
    private long thisMonthExpenses;
    private long pendingExpenses;
    private long completedExpenses;

    private List<MonthlyTrendDTO> monthlyTrend;

    private List<CategorySummaryDTO> categorySummary;

    private List<StatusSummaryDTO> statusSummary;

    private List<ExpenseResponseDTO> recentExpenses;
}