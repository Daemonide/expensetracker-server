package com.daemonide.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountMonthlyTrendDTO {
    private String month;
    private Long accountId;
    private String accountName;
    private Double amount;
}