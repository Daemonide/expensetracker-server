package com.daemonide.expensetracker.projection;

public interface AccountMonthlyTrendProjection {
    String getMonth();

    Long getAccountId();

    String getAccountName();

    Double getAmount();
}