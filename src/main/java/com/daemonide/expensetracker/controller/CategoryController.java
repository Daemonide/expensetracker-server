package com.daemonide.expensetracker.controller;

import com.daemonide.expensetracker.dto.CategoryRequestDTO;
import com.daemonide.expensetracker.dto.CategoryResponseDTO;
import com.daemonide.expensetracker.pagination.PaginationRequest;
import com.daemonide.expensetracker.pagination.PagingResult;
import com.daemonide.expensetracker.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public PagingResult<CategoryResponseDTO> getCategories(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(defaultValue = "false") Boolean fetchAll,
            @RequestParam(required = false) String search
    ) {
        PaginationRequest request = PaginationRequest.builder()
                .page(page)
                .size(size)
                .sortField(sortField)
                .sortDirection(Sort.Direction.valueOf(sortDirection.toUpperCase()))
                .fetchAll(fetchAll)
                .build();

        return categoryService.getAllCategory(request, search);
    }

    @PostMapping
    public CategoryResponseDTO addCategory(@Valid @RequestBody CategoryRequestDTO category) {
        return categoryService.createCategory(category);
    }

    @GetMapping("/{id}")
    public CategoryResponseDTO getById(@PathVariable long id) {
        return categoryService.getCategoryById(id);
    }

    @PutMapping("/{id}")
    public CategoryResponseDTO editById(@PathVariable long id, @Valid @RequestBody CategoryRequestDTO updatedCategory) {
        return categoryService.editCategory(id, updatedCategory);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        categoryService.deleteCategoryById(id);
    }


}
