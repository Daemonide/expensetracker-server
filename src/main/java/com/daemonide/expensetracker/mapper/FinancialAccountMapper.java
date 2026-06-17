package com.daemonide.expensetracker.mapper;

import com.daemonide.expensetracker.dto.FinancialAccountRequestDTO;
import com.daemonide.expensetracker.dto.FinancialAccountResponseDTO;
import com.daemonide.expensetracker.model.FinancialAccount;

import java.util.List;

public class FinancialAccountMapper {
    public static FinancialAccount toEntity(FinancialAccountRequestDTO dto) {
        FinancialAccount account = new FinancialAccount();
        account.setName(dto.getName());
        account.setType(dto.getType());
        return account;
    }

    public static FinancialAccountResponseDTO toDTO(FinancialAccount account) {
        FinancialAccountResponseDTO dto = new FinancialAccountResponseDTO();
        dto.setId(account.getId());
        dto.setName(account.getName());
        dto.setType(account.getType());
        return dto;
    }

    public static List<FinancialAccountResponseDTO> toDTOList(List<FinancialAccount> accounts) {
        return accounts.stream()
                .map(FinancialAccountMapper::toDTO)
                .toList();
    }
}