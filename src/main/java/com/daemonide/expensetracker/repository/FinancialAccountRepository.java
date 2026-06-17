package com.daemonide.expensetracker.repository;

import com.daemonide.expensetracker.model.AppUser;
import com.daemonide.expensetracker.model.FinancialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinancialAccountRepository extends JpaRepository<FinancialAccount, Long> {
    Optional<FinancialAccount> findByIdAndUser(Long id, AppUser user);

    List<FinancialAccount> findAllByUser(AppUser user);
}
