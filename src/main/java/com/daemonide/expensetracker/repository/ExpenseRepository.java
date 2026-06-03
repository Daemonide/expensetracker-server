package com.daemonide.expensetracker.repository;

import com.daemonide.expensetracker.model.AppUser;
import com.daemonide.expensetracker.model.Category;
import com.daemonide.expensetracker.model.Expense;
import com.daemonide.expensetracker.model.ExpenseStatus;
import com.daemonide.expensetracker.projection.CategorySummaryProjection;
import com.daemonide.expensetracker.projection.MonthlyTrendProjection;
import com.daemonide.expensetracker.projection.StatusSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long>,
        JpaSpecificationExecutor<Expense> {
    @Query("SELECT e FROM Expense e JOIN e.category c WHERE e.user = :user")
    Page<Expense> findByUser(@Param("user") AppUser user, Pageable pageable);

    @Query("SELECT e FROM Expense e JOIN e.category c WHERE e.category = :category AND e.user = :user")
    Page<Expense> findByCategoryAndUser(
            @Param("category") Category category,
            @Param("user") AppUser user,
            Pageable pageable
    );

    Optional<Expense> findByIdAndUser(Long id, AppUser user);

    @Query("""
                SELECT e FROM Expense e
                JOIN e.category c
                WHERE e.user = :user
                AND (
                    coalesce(:search, '') = ''
                    OR lower(e.title) LIKE lower(concat('%', :search, '%'))
                )
            """)
    Page<Expense> findByUserAndSearch(
            @Param("user") AppUser user,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
            SELECT COALESCE(SUM(e.amount),0)
            FROM Expense e
            WHERE e.user = :user
            """)
    Double getTotalSpent(@Param("user") AppUser user);

    @Query(value = """
            SELECT COALESCE(SUM(amount),0)
            FROM expense
            WHERE user_id = :userId
            AND date_trunc('month', date)
                = date_trunc('month', CURRENT_DATE)
            """, nativeQuery = true)
    Double getThisMonthSpent(@Param("userId") Long userId);

    @Query("""
            SELECT COALESCE(SUM(e.amount),0)
            FROM Expense e
            WHERE e.user = :user
            AND e.status = :status
            """)
    Double getAmountByStatus(
            @Param("user") AppUser user,
            @Param("status") ExpenseStatus status
    );

    List<Expense> findTop5ByUserOrderByDateDesc(
            AppUser user
    );

    long countByUser(AppUser user);

    @Query(value = """
            SELECT COUNT(*)
            FROM expense
            WHERE user_id = :userId
            AND date_trunc('month', date)
                = date_trunc('month', CURRENT_DATE)
            """, nativeQuery = true)
    Long countThisMonth(@Param("userId") Long userId);

    long countByUserAndStatus(AppUser user, ExpenseStatus expenseStatus);

    @Query(value = """
            SELECT
                TO_CHAR(date_trunc('month', e.date), 'YYYY-MM') AS month,
                COALESCE(SUM(e.amount), 0) AS amount
            FROM expense e
            WHERE e.user_id = :userId
              AND e.date >= date_trunc('month', CURRENT_DATE) - interval '5 months'
            GROUP BY date_trunc('month', e.date)
            ORDER BY date_trunc('month', e.date)
            """, nativeQuery = true)
    List<MonthlyTrendProjection> getMonthlyTrend(
            @Param("userId") Long userId
    );

    @Query(value = """
            SELECT
                c.name AS category,
                SUM(e.amount) AS amount
            FROM expense e
            JOIN category c
            ON c.id = e.category_id
            WHERE e.user_id = :userId
            GROUP BY c.name
            ORDER BY SUM(e.amount) DESC
            LIMIT 8
            """, nativeQuery = true)
    List<CategorySummaryProjection> getCategorySummary(
            @Param("userId") Long userId
    );

    @Query("""
            SELECT
                e.status as status,
                COUNT(e) as count,
                SUM(e.amount) as amount
            FROM Expense e
            WHERE e.user = :user
            GROUP BY e.status
            """)
    List<StatusSummaryProjection> getStatusSummary(
            @Param("user") AppUser user
    );
}