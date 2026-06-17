package com.daemonide.expensetracker.controller;

import com.daemonide.expensetracker.dto.ExpenseRequestDTO;
import com.daemonide.expensetracker.dto.ExpenseResponseDTO;
import com.daemonide.expensetracker.exception.NoSuchCategoryExistsException;
import com.daemonide.expensetracker.exception.NoSuchFinancialAccountExistsException;
import com.daemonide.expensetracker.pagination.PaginationRequest;
import com.daemonide.expensetracker.pagination.PagingResult;
import com.daemonide.expensetracker.repository.CategoryRepository;
import com.daemonide.expensetracker.repository.FinancialAccountRepository;
import com.daemonide.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CategoryRepository categoryRepository;
    private final FinancialAccountRepository financialAccountRepository;

    @PostMapping
    public ExpenseResponseDTO createExpense(@Valid @RequestBody ExpenseRequestDTO expense) {
        return expenseService.addExpense(expense);
    }

    @GetMapping
    public PagingResult<ExpenseResponseDTO> getExpenses(
            PaginationRequest request,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long financialAccountId,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo
    ) {
        return expenseService.getAllExpense(request, search, status, categoryId, financialAccountId, dateFrom, dateTo);
    }

    @GetMapping("/financial-account/{financialAccountId}")
    public PagingResult<ExpenseResponseDTO> getByFinancialAccount(
            @PathVariable Long financialAccountId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "date") String sortField,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "false") Boolean fetchAll
    ) {
        PaginationRequest request = PaginationRequest.builder()
                .page(page).size(size).sortField(sortField)
                .sortDirection(Sort.Direction.valueOf(sortDirection.toUpperCase()))
                .fetchAll(fetchAll).build();

        return expenseService.getExpenseByFinancialAccount(
                financialAccountRepository.findById(financialAccountId)
                        .orElseThrow(() -> new NoSuchFinancialAccountExistsException("Financial Account not found")),
                request);
    }

    @GetMapping("/category/{categoryId}")
    public PagingResult<ExpenseResponseDTO> getByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "date") String sortField,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "false") Boolean fetchAll
    ) {
        PaginationRequest request = PaginationRequest.builder()
                .page(page)
                .size(size)
                .sortField(sortField)
                .sortDirection(Sort.Direction.valueOf(sortDirection.toUpperCase()))
                .fetchAll(fetchAll)
                .build();

        return expenseService.getExpenseByCategory(
                categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new NoSuchCategoryExistsException("Category not found")),
                request);
    }

    @GetMapping("/{id}")
    public ExpenseResponseDTO getById(@PathVariable long id) {
        return expenseService.getExpenseById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        expenseService.deleteExpense(id);
    }

    @PutMapping("/{id}")
    public ExpenseResponseDTO editExpense(@PathVariable long id, @Valid @RequestBody ExpenseRequestDTO updatedExpense) {
        return expenseService.editExpense(id, updatedExpense);
    }


}
