package com.daemonide.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusSummaryDTO {
    private String status;
    private Long count;
    private Double amount;
}