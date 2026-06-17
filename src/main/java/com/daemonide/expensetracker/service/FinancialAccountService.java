package com.daemonide.expensetracker.service;

import com.daemonide.expensetracker.dto.FinancialAccountRequestDTO;
import com.daemonide.expensetracker.dto.FinancialAccountResponseDTO;
import com.daemonide.expensetracker.exception.NoSuchFinancialAccountExistsException;
import com.daemonide.expensetracker.mapper.FinancialAccountMapper;
import com.daemonide.expensetracker.model.AppUser;
import com.daemonide.expensetracker.model.FinancialAccount;
import com.daemonide.expensetracker.repository.FinancialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialAccountService {

    private final FinancialAccountRepository financialAccountRepository;
    private final CustomUserDetailsService userDetailsService;

    public FinancialAccountResponseDTO addFinancialAccount(FinancialAccountRequestDTO dto) {
        AppUser currentUser = userDetailsService.getCurrentUser();
        FinancialAccount account = FinancialAccountMapper.toEntity(dto);
        account.setUser(currentUser);
        return FinancialAccountMapper.toDTO(financialAccountRepository.save(account));
    }

    public List<FinancialAccountResponseDTO> getAllFinancialAccounts() {
        AppUser currentUser = userDetailsService.getCurrentUser();
        return FinancialAccountMapper.toDTOList(financialAccountRepository.findAllByUser(currentUser));
    }

    public FinancialAccountResponseDTO getFinancialAccountById(Long id) {
        AppUser currentUser = userDetailsService.getCurrentUser();
        FinancialAccount account = financialAccountRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new NoSuchFinancialAccountExistsException("Financial Account not found or unauthorized"));
        return FinancialAccountMapper.toDTO(account);
    }

    public FinancialAccountResponseDTO editFinancialAccount(Long id, FinancialAccountRequestDTO dto) {
        AppUser currentUser = userDetailsService.getCurrentUser();
        FinancialAccount account = financialAccountRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new NoSuchFinancialAccountExistsException("Financial Account not found or unauthorized"));
        account.setName(dto.getName());
        account.setType(dto.getType());
        return FinancialAccountMapper.toDTO(financialAccountRepository.save(account));
    }

    public void deleteFinancialAccount(Long id) {
        AppUser currentUser = userDetailsService.getCurrentUser();
        FinancialAccount account = financialAccountRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new NoSuchFinancialAccountExistsException("Financial Account not found or unauthorized"));
        financialAccountRepository.delete(account);
    }
}