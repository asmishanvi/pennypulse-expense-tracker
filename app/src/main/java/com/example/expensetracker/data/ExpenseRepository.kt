package com.example.expensetracker.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.YearMonth

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao,
    private val recurringExpenseDao: RecurringExpenseDao,
    private val categoryDao: CategoryDao
) {
    fun expensesForMonth(month: YearMonth): Flow<List<Expense>> {
        val start = month.atDay(1).toEpochDay()
        val end = month.atEndOfMonth().toEpochDay()
        return expenseDao.expensesForRange(start, end).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun budgetForMonth(month: YearMonth): Flow<Double?> {
        val key = month.atDay(1).toEpochDay()
        return budgetDao.budgetForMonth(key).map { it?.amount }
    }

    fun recurringExpenses(): Flow<List<RecurringExpense>> {
        return recurringExpenseDao.recurringExpenses().map { list ->
            list.map { it.toDomain() }
        }
    }

    fun customCategories(): Flow<List<CustomCategory>> {
        return categoryDao.categories().map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense.toEntity())
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense.toEntity())
    }

    suspend fun deleteExpense(id: Long) {
        expenseDao.deleteExpense(id)
    }

    suspend fun setBudget(month: YearMonth, amount: Double) {
        val key = month.atDay(1).toEpochDay()
        budgetDao.upsertBudget(BudgetEntity(monthEpochDay = key, amount = amount))
    }

    suspend fun clearBudget(month: YearMonth) {
        val key = month.atDay(1).toEpochDay()
        budgetDao.deleteBudget(key)
    }

    suspend fun addRecurringExpense(
        amount: Double,
        category: String,
        note: String,
        dayOfMonth: Int
    ) {
        recurringExpenseDao.insertRecurring(
            RecurringExpenseEntity(
                amount = amount,
                category = category,
                note = note,
                dayOfMonth = dayOfMonth.coerceIn(1, 31)
            )
        )
    }

    suspend fun deleteRecurringExpense(id: Long) {
        recurringExpenseDao.deleteRecurring(id)
    }

    suspend fun addCustomCategory(label: String, emoji: String) {
        val cleanLabel = label.trim()
        val cleanEmoji = emoji.ifBlank { "✨" }
        if (cleanLabel.isBlank()) return
        categoryDao.insertCategory(
            CategoryEntity(
                label = cleanLabel,
                emoji = cleanEmoji
            )
        )
    }

    suspend fun applyRecurringForMonth(month: YearMonth) {
        val monthKey = month.atDay(1).toEpochDay()
        val active = recurringExpenseDao.activeRecurring()
        active.forEach { recurring ->
            if (recurring.lastAppliedMonthEpochDay == monthKey) return@forEach
            val day = recurring.dayOfMonth.coerceAtMost(month.lengthOfMonth())
            val date = month.atDay(day)
            expenseDao.insertExpense(
                ExpenseEntity(
                    amount = recurring.amount,
                    category = recurring.category,
                    dateEpochDay = date.toEpochDay(),
                    note = recurring.note
                )
            )
            recurringExpenseDao.updateRecurring(
                recurring.copy(lastAppliedMonthEpochDay = monthKey)
            )
        }
    }
}
