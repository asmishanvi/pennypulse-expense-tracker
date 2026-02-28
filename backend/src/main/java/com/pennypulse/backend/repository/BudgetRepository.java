package com.pennypulse.backend.repository;

import com.pennypulse.backend.domain.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;

public interface BudgetRepository extends JpaRepository<Budget, YearMonth> {
}
