package com.daemonide.expensetracker.projection;

public interface StatusSummaryProjection {

    String getStatus();

    Long getCount();

    Double getAmount();
}