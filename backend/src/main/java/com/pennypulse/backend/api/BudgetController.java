package com.pennypulse.backend.api;

import com.pennypulse.backend.domain.Budget;
import com.pennypulse.backend.service.BudgetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public BudgetResponse get(@RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return budgetService.getBudget(month)
            .map(b -> new BudgetResponse(b.getMonth().toString(), b.getAmount()))
            .orElse(null);
    }

    @PutMapping
    public BudgetResponse upsert(@Valid @RequestBody BudgetRequest request) {
        Budget budget = new Budget();
        budget.setMonth(YearMonth.parse(request.month()));
        budget.setAmount(request.amount());
        Budget saved = budgetService.upsert(budget);
        return new BudgetResponse(saved.getMonth().toString(), saved.getAmount());
    }

    public record BudgetRequest(
        @NotNull String month,
        @NotNull Double amount
    ) {}

    public record BudgetResponse(String month, Double amount) {}
}
