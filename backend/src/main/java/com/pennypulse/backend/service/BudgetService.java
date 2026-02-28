package com.pennypulse.backend.service;

import com.pennypulse.backend.domain.Budget;
import com.pennypulse.backend.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Optional;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Optional<Budget> getBudget(YearMonth month) {
        return budgetRepository.findById(month);
    }

    public Budget upsert(Budget budget) {
        return budgetRepository.save(budget);
    }
}
