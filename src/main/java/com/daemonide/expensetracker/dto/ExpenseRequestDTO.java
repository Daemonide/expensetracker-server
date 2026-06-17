package com.daemonide.expensetracker.dto;

import com.daemonide.expensetracker.model.ExpenseStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonPropertyOrder({
        "title",
        "amount",
        "date",
        "categoryId",
        "status",
        "financialAccountId"
})
public class ExpenseRequestDTO {
    private String title;
    private double amount;
    private LocalDate date;
    private long categoryId;
    private ExpenseStatus status;
    private long financialAccountId;
}
