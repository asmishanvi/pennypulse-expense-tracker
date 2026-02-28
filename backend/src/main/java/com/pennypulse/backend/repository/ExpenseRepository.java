package com.pennypulse.backend.repository;

import com.pennypulse.backend.domain.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("select e from Expense e where e.expenseDate between :start and :end order by e.expenseDate desc")
    List<Expense> findForMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("select sum(e.amount) from Expense e where e.expenseDate between :start and :end")
    Double sumForMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
