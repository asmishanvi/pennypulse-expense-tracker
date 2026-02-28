package com.pennypulse.backend.repository;

import com.pennypulse.backend.domain.RecurringExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecurringExpenseRepository extends JpaRepository<RecurringExpense, Long> {
    List<RecurringExpense> findByActiveTrue();
}
