package com.daemonide.expensetracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Expense {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String title;

    @Positive
    private double amount;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Enumerated(EnumType.STRING)
    private ExpenseStatus status;

    @Formula("""
                CASE status
                    WHEN 'DONE'        THEN 1
                    WHEN 'IN_PROGRESS' THEN 2
                    WHEN 'PENDING'     THEN 3
                    WHEN 'CANCELLED'   THEN 4
                END
            """)
    private Integer statusOrder;

    @ManyToOne
    @JoinColumn(name = "financial_account_id")
    private FinancialAccount financialAccount;
}
