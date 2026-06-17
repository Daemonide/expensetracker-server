package com.daemonide.expensetracker.dto;

import com.daemonide.expensetracker.model.ExpenseStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonPropertyOrder({
        "expenseID",
        "title",
        "amount",
        "date",
        "categoryId",
        "categoryName",
        "status",
        "financialAccountId",
        "financialAccountName"
})
public class ExpenseResponseDTO {
    private long expenseID;
    private String title;
    private double amount;
    private LocalDate date;
    private long categoryId;
    private String categoryName;
    private ExpenseStatus status;
    private long financialAccountId;
    private String financialAccountName;
}
