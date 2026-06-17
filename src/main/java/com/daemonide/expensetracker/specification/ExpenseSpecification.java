package com.daemonide.expensetracker.specification;

import com.daemonide.expensetracker.model.AppUser;
import com.daemonide.expensetracker.model.Expense;
import com.daemonide.expensetracker.model.ExpenseStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpenseSpecification {

    public static Specification<Expense> build(
            AppUser user,
            String search,
            String status,
            Long categoryId,
            Long financialAccountId,
            LocalDate dateFrom,
            LocalDate dateTo
    ) {
        return Specification
                .where(hasUser(user))
                .and(hasSearch(search))
                .and(hasStatus(status))
                .and(hasCategory(categoryId))
                .and(hasFinancialAccount(financialAccountId))
                .and(hasDateFrom(dateFrom))
                .and(hasDateTo(dateTo));
    }

    private static Specification<Expense> hasFinancialAccount(Long financialAccountId) {
        return (root, query, cb) ->
                financialAccountId == null ? cb.conjunction()
                        : cb.equal(root.get("financialAccount").get("id"), financialAccountId);
    }

    private static Specification<Expense> hasUser(AppUser user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }

    private static Specification<Expense> hasSearch(String search) {
        return (root, query, cb) ->
                (search == null || search.isBlank()) ? cb.conjunction()
                        : cb.like(cb.lower(root.get("title")), "%" + search.toLowerCase() + "%");
    }

    private static Specification<Expense> hasStatus(String status) {
        return (root, query, cb) ->
                (status == null || status.isBlank()) ? cb.conjunction()
                        : cb.equal(root.get("status"), ExpenseStatus.valueOf(status));
    }

    private static Specification<Expense> hasCategory(Long categoryId) {
        return (root, query, cb) ->
                categoryId == null ? cb.conjunction()
                        : cb.equal(root.get("category").get("id"), categoryId);
    }

    private static Specification<Expense> hasDateFrom(LocalDate dateFrom) {
        return (root, query, cb) ->
                dateFrom == null ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("date"), dateFrom);
    }

    private static Specification<Expense> hasDateTo(LocalDate dateTo) {
        return (root, query, cb) ->
                dateTo == null ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get("date"), dateTo);
    }
}