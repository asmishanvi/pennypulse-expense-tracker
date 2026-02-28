package com.pennypulse.backend.api;

import com.pennypulse.backend.domain.Expense;
import com.pennypulse.backend.service.ExpenseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<Expense> list(@RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return expenseService.listForMonth(month);
    }

    @PostMapping
    public Expense create(@Valid @RequestBody ExpenseRequest request) {
        Expense expense = toEntity(request);
        return expenseService.create(expense);
    }

    @PutMapping("/{id}")
    public Expense update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        Expense expense = toEntity(request);
        return expenseService.update(id, expense);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        expenseService.delete(id);
    }

    private Expense toEntity(ExpenseRequest request) {
        Expense expense = new Expense();
        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setNote(request.note());
        expense.setExpenseDate(request.expenseDate());
        return expense;
    }

    public record ExpenseRequest(
        @NotNull Double amount,
        @NotBlank String category,
        String note,
        @NotNull LocalDate expenseDate
    ) {}
}
