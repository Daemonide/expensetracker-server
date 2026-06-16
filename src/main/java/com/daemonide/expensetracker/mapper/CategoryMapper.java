package com.daemonide.expensetracker.mapper;

import com.daemonide.expensetracker.dto.CategoryRequestDTO;
import com.daemonide.expensetracker.dto.CategoryResponseDTO;
import com.daemonide.expensetracker.model.Category;

import java.util.List;

public class CategoryMapper {
    public static CategoryResponseDTO toDTO(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setCategoryId(category.getId());
        dto.setName(category.getName());
        dto.setIconName(category.getIconName());
        dto.setExpenseCount(category.getExpenseCount());
        return dto;
    }

    public static Category toEntity(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setIconName(dto.getIconName());
        return category;
    }

    public static List<CategoryResponseDTO> toDTOList(List<Category> categories) {
        return categories.stream()
                .map(CategoryMapper::toDTO)
                .toList();
    }
}
