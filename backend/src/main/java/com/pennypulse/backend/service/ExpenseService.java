package com.pennypulse.backend.service;

import com.pennypulse.backend.domain.Expense;
import com.pennypulse.backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> listForMonth(YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return expenseRepository.findForMonth(start, end);
    }

    public Expense create(Expense expense) {
        return expenseRepository.save(expense);
    }

    public Expense update(Long id, Expense updated) {
        Expense existing = expenseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Expense not found"));
        existing.setAmount(updated.getAmount());
        existing.setCategory(updated.getCategory());
        existing.setNote(updated.getNote());
        existing.setExpenseDate(updated.getExpenseDate());
        return expenseRepository.save(existing);
    }

    public void delete(Long id) {
        expenseRepository.deleteById(id);
    }
}
