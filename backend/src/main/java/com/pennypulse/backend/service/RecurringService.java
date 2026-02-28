package com.pennypulse.backend.service;

import com.pennypulse.backend.domain.Expense;
import com.pennypulse.backend.domain.RecurringApplied;
import com.pennypulse.backend.domain.RecurringExpense;
import com.pennypulse.backend.repository.ExpenseRepository;
import com.pennypulse.backend.repository.RecurringAppliedRepository;
import com.pennypulse.backend.repository.RecurringExpenseRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class RecurringService {
    private final RecurringExpenseRepository recurringExpenseRepository;
    private final RecurringAppliedRepository recurringAppliedRepository;
    private final ExpenseRepository expenseRepository;

    public RecurringService(
        RecurringExpenseRepository recurringExpenseRepository,
        RecurringAppliedRepository recurringAppliedRepository,
        ExpenseRepository expenseRepository
    ) {
        this.recurringExpenseRepository = recurringExpenseRepository;
        this.recurringAppliedRepository = recurringAppliedRepository;
        this.expenseRepository = expenseRepository;
    }

    public List<RecurringExpense> list() {
        return recurringExpenseRepository.findAll();
    }

    public RecurringExpense create(RecurringExpense recurringExpense) {
        return recurringExpenseRepository.save(recurringExpense);
    }

    public void delete(Long id) {
        recurringExpenseRepository.deleteById(id);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void applyForCurrentMonth() {
        applyForMonth(YearMonth.now());
    }

    @Scheduled(cron = "0 0 2 1 * *")
    public void scheduledApply() {
        applyForMonth(YearMonth.now());
    }

    public void applyForMonth(YearMonth month) {
        List<RecurringExpense> recurring = recurringExpenseRepository.findByActiveTrue();
        for (RecurringExpense item : recurring) {
            if (recurringAppliedRepository.findByRecurringIdAndMonth(item.getId(), month).isPresent()) {
                continue;
            }
            int day = Math.min(item.getDayOfMonth(), month.lengthOfMonth());
            LocalDate date = month.atDay(day);
            Expense expense = new Expense();
            expense.setAmount(item.getAmount());
            expense.setCategory(item.getCategory());
            expense.setNote(item.getNote());
            expense.setExpenseDate(date);
            expenseRepository.save(expense);

            RecurringApplied applied = new RecurringApplied();
            applied.setRecurringId(item.getId());
            applied.setMonth(month);
            recurringAppliedRepository.save(applied);
        }
    }
}
