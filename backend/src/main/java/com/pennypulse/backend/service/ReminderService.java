package com.pennypulse.backend.service;

import com.pennypulse.backend.domain.Expense;
import com.pennypulse.backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@Service
public class ReminderService {
    private final ExpenseRepository expenseRepository;

    public ReminderService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public ReminderStatus status() {
        LocalDate today = LocalDate.now();
        YearMonth month = YearMonth.from(today);
        List<Expense> expenses = expenseRepository.findForMonth(month.atDay(1), month.atEndOfMonth());
        boolean hasToday = expenses.stream().anyMatch(e -> e.getExpenseDate().equals(today));
        LocalDate last = expenses.stream()
            .map(Expense::getExpenseDate)
            .max(Comparator.naturalOrder())
            .orElse(null);
        return new ReminderStatus(!hasToday, last, hasToday ? "logged" : "missing");
    }

    public record ReminderStatus(boolean shouldRemind, LocalDate lastExpenseDate, String reason) {}
}
