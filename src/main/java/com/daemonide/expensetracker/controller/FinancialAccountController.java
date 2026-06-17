package com.daemonide.expensetracker.controller;

import com.daemonide.expensetracker.dto.FinancialAccountRequestDTO;
import com.daemonide.expensetracker.dto.FinancialAccountResponseDTO;
import com.daemonide.expensetracker.service.FinancialAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/financial-accounts")
public class FinancialAccountController {

    private final FinancialAccountService financialAccountService;

    @PostMapping
    public FinancialAccountResponseDTO createFinancialAccount(@Valid @RequestBody FinancialAccountRequestDTO dto) {
        return financialAccountService.addFinancialAccount(dto);
    }

    @GetMapping
    public List<FinancialAccountResponseDTO> getFinancialAccounts() {
        return financialAccountService.getAllFinancialAccounts();
    }

    @GetMapping("/{id}")
    public FinancialAccountResponseDTO getFinancialAccount(@PathVariable Long id) {
        return financialAccountService.getFinancialAccountById(id);
    }

    @PutMapping("/{id}")
    public FinancialAccountResponseDTO editFinancialAccount(@PathVariable Long id, @Valid @RequestBody FinancialAccountRequestDTO dto) {
        return financialAccountService.editFinancialAccount(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteFinancialAccount(@PathVariable Long id) {
        financialAccountService.deleteFinancialAccount(id);
    }

}