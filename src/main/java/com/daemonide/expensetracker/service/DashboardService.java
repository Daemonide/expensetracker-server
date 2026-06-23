package com.daemonide.expensetracker.service;

import com.daemonide.expensetracker.dto.*;
import com.daemonide.expensetracker.mapper.ExpenseMapper;
import com.daemonide.expensetracker.model.AppUser;
import com.daemonide.expensetracker.model.ExpenseStatus;
import com.daemonide.expensetracker.projection.CategorySummaryProjection;
import com.daemonide.expensetracker.projection.MonthlyTrendProjection;
import com.daemonide.expensetracker.projection.StatusSummaryProjection;
import com.daemonide.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExpenseRepository expenseRepository;
    private final CustomUserDetailsService userDetailsService;

    public DashboardResponseDTO getDashboard() {

        AppUser user = userDetailsService.getCurrentUser();

        // ── Monthly overall trend ──────────────────────────────────────────────
        Map<YearMonth, MonthlyTrendProjection> dbTrend =
                expenseRepository.getMonthlyTrend(user.getId())
                        .stream()
                        .collect(Collectors.toMap(
                                p -> YearMonth.parse(p.getMonth()),
                                p -> p
                        ));

        List<MonthlyTrendDTO> monthlyTrend =
                IntStream.rangeClosed(0, 5)
                        .mapToObj(i -> YearMonth.now().minusMonths(5 - i))
                        .map(ym -> {
                            MonthlyTrendProjection proj = dbTrend.get(ym);
                            return new MonthlyTrendDTO(
                                    ym.getMonth()
                                            .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                                            + " "
                                            + String.valueOf(ym.getYear()).substring(2),
                                    proj != null ? proj.getAmount() : 0.0,
                                    proj != null ? proj.getCount() : 0L
                            );
                        })
                        .toList();

        // ── Per-account monthly trend ──────────────────────────────────────────
        List<AccountMonthlyTrendDTO> accountMonthlyTrend =
                expenseRepository.getAccountMonthlyTrend(user.getId())
                        .stream()
                        .map(p -> {
                            YearMonth ym = YearMonth.parse(p.getMonth());
                            String label = ym.getMonth()
                                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                                    + " "
                                    + String.valueOf(ym.getYear()).substring(2);
                            return new AccountMonthlyTrendDTO(
                                    label,
                                    p.getAccountId(),
                                    p.getAccountName(),
                                    p.getAmount()
                            );
                        })
                        .toList();

        // ── Category summary ───────────────────────────────────────────────────
        List<CategorySummaryDTO> categorySummary =
                expenseRepository.getCategorySummary(user.getId())
                        .stream()
                        .map(this::mapCategory)
                        .toList();

        // ── Status summary ─────────────────────────────────────────────────────
        List<StatusSummaryDTO> rawStatuses =
                expenseRepository.getStatusSummary(user)
                        .stream()
                        .map(this::mapStatus)
                        .toList();

        List<String> order = List.of("DONE", "IN_PROGRESS", "PENDING", "CANCELLED");

        List<StatusSummaryDTO> statusSummary =
                order.stream()
                        .map(status ->
                                rawStatuses.stream()
                                        .filter(s -> s.getStatus().equals(status))
                                        .findFirst()
                                        .orElse(null)
                        )
                        .filter(Objects::nonNull)
                        .toList();

        return DashboardResponseDTO.builder()
                .totalSpent(expenseRepository.getTotalSpent(user))
                .thisMonthSpent(expenseRepository.getThisMonthSpent(user.getId()))
                .pendingAmount(expenseRepository.getAmountByStatus(user, ExpenseStatus.PENDING))
                .completedAmount(expenseRepository.getAmountByStatus(user, ExpenseStatus.DONE))
                .totalExpenses(expenseRepository.countByUser(user))
                .thisMonthExpenses(expenseRepository.countThisMonth(user.getId()))
                .pendingExpenses(expenseRepository.countByUserAndStatus(user, ExpenseStatus.PENDING))
                .completedExpenses(expenseRepository.countByUserAndStatus(user, ExpenseStatus.DONE))
                .monthlyTrend(monthlyTrend)
                .accountMonthlyTrend(accountMonthlyTrend)
                .categorySummary(categorySummary)
                .statusSummary(statusSummary)
                .recentExpenses(
                        ExpenseMapper.toDTOList(
                                expenseRepository.findTop5ByUserOrderByDateDesc(user)
                        )
                )
                .build();
    }

    private CategorySummaryDTO mapCategory(CategorySummaryProjection p) {
        return new CategorySummaryDTO(p.getCategory(), p.getAmount(), p.getCount());
    }

    private StatusSummaryDTO mapStatus(StatusSummaryProjection p) {
        return new StatusSummaryDTO(p.getStatus(), p.getCount(), p.getAmount());
    }
}