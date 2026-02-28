package com.pennypulse.backend.api;

import com.pennypulse.backend.domain.Category;
import com.pennypulse.backend.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> list() {
        return categoryService.all();
    }

    @PostMapping
    public Category create(@Valid @RequestBody CategoryRequest request) {
        Category category = new Category();
        category.setName(request.name());
        category.setEmoji(request.emoji());
        return categoryService.create(category);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }

    public record CategoryRequest(
        @NotBlank String name,
        @NotBlank String emoji
    ) {}
}
