package com.pennypulse.backend.api;

import com.pennypulse.backend.domain.RecurringExpense;
import com.pennypulse.backend.service.RecurringService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recurring")
public class RecurringController {
    private final RecurringService recurringService;

    public RecurringController(RecurringService recurringService) {
        this.recurringService = recurringService;
    }

    @GetMapping
    public List<RecurringExpense> list() {
        return recurringService.list();
    }

    @PostMapping
    public RecurringExpense create(@Valid @RequestBody RecurringRequest request) {
        RecurringExpense recurring = new RecurringExpense();
        recurring.setAmount(request.amount());
        recurring.setCategory(request.category());
        recurring.setNote(request.note());
        recurring.setDayOfMonth(request.dayOfMonth());
        return recurringService.create(recurring);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        recurringService.delete(id);
    }

    public record RecurringRequest(
        @NotNull Double amount,
        @NotBlank String category,
        String note,
        @NotNull Integer dayOfMonth
    ) {}
}
