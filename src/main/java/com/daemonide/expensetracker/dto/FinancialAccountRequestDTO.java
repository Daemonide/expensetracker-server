package com.daemonide.expensetracker.dto;

import com.daemonide.expensetracker.model.FinancialAccountType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"name", "type"})
public class FinancialAccountRequestDTO {
    private String name;
    private FinancialAccountType type;
}