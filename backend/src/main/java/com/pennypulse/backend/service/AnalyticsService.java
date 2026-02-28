package com.pennypulse.backend.service;

import com.pennypulse.backend.domain.Expense;
import com.pennypulse.backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    private final ExpenseRepository expenseRepository;

    public AnalyticsService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Summary summary(YearMonth month) {
        List<Expense> expenses = list(month);
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double avg = expenses.isEmpty() ? 0 : total / expenses.size();
        return new Summary(expenses.size(), total, avg);
    }

    public List<CategoryTotal> categoryTotals(YearMonth month) {
        List<Expense> expenses = list(month);
        Map<String, Double> grouped = expenses.stream()
            .collect(Collectors.groupingBy(Expense::getCategory,
                Collectors.summingDouble(Expense::getAmount)));
        return grouped.entrySet().stream()
            .map(e -> new CategoryTotal(e.getKey(), e.getValue()))
            .sorted((a, b) -> Double.compare(b.total(), a.total()))
            .toList();
    }

    public List<WeeklyTotal> weeklyTotals(YearMonth month) {
        List<Expense> expenses = list(month);
        List<WeeklyTotal> result = new ArrayList<>();
        LocalDate cursor = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        while (!cursor.isAfter(end)) {
            LocalDate weekEnd = cursor.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            if (weekEnd.isAfter(end)) weekEnd = end;
            double total = expenses.stream()
                .filter(e -> !e.getExpenseDate().isBefore(cursor) && !e.getExpenseDate().isAfter(weekEnd))
                .mapToDouble(Expense::getAmount)
                .sum();
            result.add(new WeeklyTotal(cursor, weekEnd, total));
            cursor = weekEnd.plusDays(1);
        }
        return result;
    }

    private List<Expense> list(YearMonth month) {
        return expenseRepository.findForMonth(month.atDay(1), month.atEndOfMonth());
    }

    public record Summary(int count, double total, double average) {}
    public record CategoryTotal(String category, double total) {}
    public record WeeklyTotal(LocalDate weekStart, LocalDate weekEnd, double total) {}
}
