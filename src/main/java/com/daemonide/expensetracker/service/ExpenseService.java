package com.daemonide.expensetracker.service;

import com.daemonide.expensetracker.dto.ExpenseRequestDTO;
import com.daemonide.expensetracker.dto.ExpenseResponseDTO;
import com.daemonide.expensetracker.exception.NoSuchCategoryExistsException;
import com.daemonide.expensetracker.exception.NoSuchExpenseExistsException;
import com.daemonide.expensetracker.exception.NoSuchFinancialAccountExistsException;
import com.daemonide.expensetracker.mapper.ExpenseMapper;
import com.daemonide.expensetracker.model.AppUser;
import com.daemonide.expensetracker.model.Category;
import com.daemonide.expensetracker.model.Expense;
import com.daemonide.expensetracker.model.FinancialAccount;
import com.daemonide.expensetracker.pagination.PaginationRequest;
import com.daemonide.expensetracker.pagination.PaginationUtils;
import com.daemonide.expensetracker.pagination.PagingResult;
import com.daemonide.expensetracker.repository.CategoryRepository;
import com.daemonide.expensetracker.repository.ExpenseRepository;
import com.daemonide.expensetracker.repository.FinancialAccountRepository;
import com.daemonide.expensetracker.specification.ExpenseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final CustomUserDetailsService userDetailsService;
    private final FinancialAccountRepository financialAccountRepository;

    public ExpenseResponseDTO addExpense(ExpenseRequestDTO expenseDto) {
        AppUser currentUser = userDetailsService.getCurrentUser();


        Category category = categoryRepository.findByIdAndUser(expenseDto.getCategoryId(), currentUser)
                .orElseThrow(() -> new NoSuchCategoryExistsException("Category not found or unauthorized"));

        FinancialAccount financialAccount = financialAccountRepository.findByIdAndUser(expenseDto.getFinancialAccountId(), currentUser)
                .orElseThrow(() -> new NoSuchFinancialAccountExistsException("Financial Account not found or unauthorized"));

        Expense expense = ExpenseMapper.toEntity(expenseDto, category, financialAccount);
        expense.setUser(currentUser);

        return ExpenseMapper.toDTO(expenseRepository.save(expense));
    }

    public ExpenseResponseDTO getExpenseById(long id) {
        AppUser currentUser = userDetailsService.getCurrentUser();
        Expense expense = expenseRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new NoSuchExpenseExistsException("Expense not found or unauthorized"));
        return ExpenseMapper.toDTO(expense);
    }

    public PagingResult<ExpenseResponseDTO> getAllExpense(
            PaginationRequest request,
            String search,
            String status,
            Long categoryId,
            Long financialAccountId,
            String dateFrom,
            String dateTo
    ) {
        AppUser currentUser = userDetailsService.getCurrentUser();
        Pageable pageable = PaginationUtils.getPageable(resolveSortField(request));

        LocalDate from = (dateFrom != null && !dateFrom.isBlank()) ? LocalDate.parse(dateFrom) : null;
        LocalDate to = (dateTo != null && !dateTo.isBlank()) ? LocalDate.parse(dateTo) : null;
        String searchParam = (search == null || search.isBlank()) ? null : search;

        Page<Expense> expenses = expenseRepository.findAll(
                ExpenseSpecification.build(currentUser, searchParam, status, categoryId, financialAccountId, from, to),
                pageable
        );

        return new PagingResult<>(
                ExpenseMapper.toDTOList(expenses.getContent()),
                expenses.getTotalPages(), expenses.getTotalElements(), expenses.getSize(),
                expenses.getNumber(), expenses.isEmpty(),
                request.getSortField(), request.getSortDirection().name()
        );
    }

    public PagingResult<ExpenseResponseDTO> getExpenseByFinancialAccount(
            FinancialAccount financialAccount,
            PaginationRequest request
    ) {
        AppUser currentUser = userDetailsService.getCurrentUser();
        Pageable pageable = PaginationUtils.getPageable(request);

        Page<Expense> expenses = expenseRepository.findByFinancialAccountAndUser(financialAccount, currentUser, pageable);
        List<ExpenseResponseDTO> dtoList = ExpenseMapper.toDTOList(expenses.getContent());

        return new PagingResult<>(
                dtoList, expenses.getTotalPages(), expenses.getTotalElements(), expenses.getSize(),
                expenses.getNumber(), expenses.isEmpty(),
                request.getSortField(), request.getSortDirection().name()
        );
    }

    // Keep this helper from the status-sort task
    private PaginationRequest resolveSortField(PaginationRequest request) {
        if ("status".equalsIgnoreCase(request.getSortField())) {
            return PaginationRequest.builder()
                    .page(request.getPage())
                    .size(request.getSize())
                    .sortField("statusOrder")
                    .sortDirection(request.getSortDirection())
                    .fetchAll(request.getFetchAll())
                    .build();
        }
        return request;
    }

    public PagingResult<ExpenseResponseDTO> getExpenseByCategory(
            Category category,
            PaginationRequest request
    ) {

        AppUser currentUser = userDetailsService.getCurrentUser();

        Pageable pageable = PaginationUtils.getPageable(request);

        Page<Expense> expenses =
                expenseRepository.findByCategoryAndUser(category, currentUser, pageable);

        List<ExpenseResponseDTO> dtoList =
                ExpenseMapper.toDTOList(expenses.getContent());

        return new PagingResult<>(
                dtoList,
                expenses.getTotalPages(),
                expenses.getTotalElements(),
                expenses.getSize(),
                expenses.getNumber(),
                expenses.isEmpty(),
                request.getSortField(),
                request.getSortDirection().name()
        );
    }

    public void deleteExpense(Long id) {
        AppUser currentUser = userDetailsService.getCurrentUser();
        Expense expense = expenseRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new NoSuchExpenseExistsException("Expense not found or unauthorized"));
        expenseRepository.delete(expense);
    }

    public ExpenseResponseDTO editExpense(long id, ExpenseRequestDTO updatedExpense) {
        AppUser currentUser = userDetailsService.getCurrentUser();

        Expense expense = expenseRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new NoSuchExpenseExistsException("Expense not found or unauthorized"));

        Category category = categoryRepository.findByIdAndUser(updatedExpense.getCategoryId(), currentUser)
                .orElseThrow(() -> new NoSuchCategoryExistsException("Category not found or unauthorized"));

        FinancialAccount financialAccount = financialAccountRepository.findByIdAndUser(updatedExpense.getFinancialAccountId(), currentUser)
                .orElseThrow(() -> new NoSuchFinancialAccountExistsException("Financial Account not found or unauthorized"));

        expense.setTitle(updatedExpense.getTitle());
        expense.setAmount(updatedExpense.getAmount());
        expense.setDate(updatedExpense.getDate());
        expense.setCategory(category);
        expense.setStatus(updatedExpense.getStatus());
        expense.setFinancialAccount(financialAccount);

        return ExpenseMapper.toDTO(expenseRepository.save(expense));
    }

    private String resolveSortField(String sortField) {
        if ("status".equalsIgnoreCase(sortField)) {
            return "statusOrder";
        }
        return sortField;
    }
}
