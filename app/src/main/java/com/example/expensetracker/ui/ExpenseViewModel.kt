package com.example.expensetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.CustomCategory
import com.example.expensetracker.data.ExpenseRepository
import com.example.expensetracker.data.RecurringExpense
import com.example.expensetracker.ui.theme.ThemeAccent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

sealed class Screen {
    data object List : Screen()
    data class Edit(val expenseId: Long?) : Screen()
    data object Insights : Screen()
}

data class CategoryOption(
    val label: String,
    val emoji: String
)

enum class ThemeMode {
    System,
    Light,
    Dark
}

data class ThemeSettings(
    val mode: ThemeMode = ThemeMode.System,
    val accent: ThemeAccent = ThemeAccent.Mint
)

private data class SettingsBundle(
    val theme: ThemeSettings,
    val remindersEnabled: Boolean
)

data class ExpenseFilters(
    val query: String = "",
    val selectedCategories: Set<String> = emptySet(),
    val minAmountInput: String = "",
    val maxAmountInput: String = "",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
) {
    fun isActive(): Boolean {
        return query.isNotBlank() ||
            selectedCategories.isNotEmpty() ||
            minAmountInput.isNotBlank() ||
            maxAmountInput.isNotBlank() ||
            startDate != null ||
            endDate != null
    }
}

private data class ExpenseBundle(
    val expenses: List<Expense>,
    val budget: Double?,
    val recurring: List<RecurringExpense>,
    val filters: ExpenseFilters
)

private data class ViewBundle(
    val expenseBundle: ExpenseBundle,
    val categories: List<CategoryOption>,
    val settings: SettingsBundle
)

data class ExpenseUiState(
    val expenses: List<Expense> = emptyList(),
    val filteredExpenses: List<Expense> = emptyList(),
    val month: YearMonth = YearMonth.now(),
    val total: Double = 0.0,
    val filteredTotal: Double = 0.0,
    val screen: Screen = Screen.List,
    val categories: List<CategoryOption> = ExpenseCategories.defaults,
    val budget: Double? = null,
    val recurringExpenses: List<RecurringExpense> = emptyList(),
    val filters: ExpenseFilters = ExpenseFilters(),
    val filtersActive: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.System,
    val themeAccent: ThemeAccent = ThemeAccent.Mint,
    val remindersEnabled: Boolean = false
)

object ExpenseCategories {
    val defaults = listOf(
        CategoryOption("Food", "🍜"),
        CategoryOption("Coffee", "☕"),
        CategoryOption("Transport", "🛵"),
        CategoryOption("Shopping", "🛍️"),
        CategoryOption("Bills", "💸"),
        CategoryOption("Health", "🧘"),
        CategoryOption("Entertainment", "🎮"),
        CategoryOption("Travel", "✈️"),
        CategoryOption("Other", "🧾")
    )

    private val emojiMap: Map<String, String> = defaults.associate { it.label to it.emoji }

    fun emojiFor(category: String): String = emojiMap[category] ?: "🧾"
}

class ExpenseViewModel(
    private val repository: ExpenseRepository,
    private val budgetAlertManager: BudgetAlertManager,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {
    private val selectedMonth = MutableStateFlow(YearMonth.now())
    private val screenState = MutableStateFlow<Screen>(Screen.List)
    private val filterState = MutableStateFlow(ExpenseFilters())
    private val themeState = MutableStateFlow(ThemeSettings())
    private val reminderState = MutableStateFlow(reminderScheduler.isEnabled())

    private val expensesForMonth = selectedMonth.flatMapLatest { month ->
        repository.expensesForMonth(month)
    }
    private val budgetForMonth = selectedMonth.flatMapLatest { month ->
        repository.budgetForMonth(month)
    }
    private val recurringExpenses = repository.recurringExpenses()
    private val customCategories = repository.customCategories()

    private val categoriesFlow = customCategories.map { custom ->
        mergeCategories(ExpenseCategories.defaults, custom)
    }

    private val expenseBundle = combine(
        expensesForMonth,
        budgetForMonth,
        recurringExpenses,
        filterState
    ) { expenses, budget, recurring, filters ->
        ExpenseBundle(
            expenses = expenses,
            budget = budget,
            recurring = recurring,
            filters = filters
        )
    }

    private val settingsBundle = combine(
        themeState,
        reminderState
    ) { theme, reminders ->
        SettingsBundle(theme = theme, remindersEnabled = reminders)
    }

    private val viewBundle = combine(
        expenseBundle,
        categoriesFlow,
        settingsBundle
    ) { expenseBundle, categories, settings ->
        ViewBundle(
            expenseBundle = expenseBundle,
            categories = categories,
            settings = settings
        )
    }

    val uiState: StateFlow<ExpenseUiState> = combine(
        viewBundle,
        selectedMonth,
        screenState
    ) { viewBundle, month, screen ->
        val bundle = viewBundle.expenseBundle
        val filtered = applyFilters(bundle.expenses, bundle.filters)
        ExpenseUiState(
            expenses = bundle.expenses,
            filteredExpenses = filtered,
            month = month,
            total = bundle.expenses.sumOf { it.amount },
            filteredTotal = filtered.sumOf { it.amount },
            screen = screen,
            budget = bundle.budget,
            recurringExpenses = bundle.recurring,
            filters = bundle.filters,
            filtersActive = bundle.filters.isActive(),
            categories = viewBundle.categories,
            themeMode = viewBundle.settings.theme.mode,
            themeAccent = viewBundle.settings.theme.accent,
            remindersEnabled = viewBundle.settings.remindersEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ExpenseUiState()
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            selectedMonth.collect { month ->
                repository.applyRecurringForMonth(month)
            }
        }

        viewModelScope.launch {
            combine(
                expensesForMonth,
                budgetForMonth,
                selectedMonth
            ) { expenses, budget, month ->
                Triple(month, expenses.sumOf { it.amount }, budget)
            }.collect { (month, total, budget) ->
                budgetAlertManager.checkAndNotify(month, total, budget)
            }
        }
    }

    fun nextMonth() {
        selectedMonth.value = selectedMonth.value.plusMonths(1)
    }

    fun previousMonth() {
        selectedMonth.value = selectedMonth.value.minusMonths(1)
    }

    fun openAddExpense() {
        screenState.value = Screen.Edit(null)
    }

    fun openEditExpense(id: Long) {
        screenState.value = Screen.Edit(id)
    }

    fun openInsights() {
        screenState.value = Screen.Insights
    }

    fun openHome() {
        screenState.value = Screen.List
    }

    fun closeEditor() {
        screenState.value = Screen.List
    }

    fun saveExpense(
        id: Long?,
        amount: Double,
        category: String,
        date: java.time.LocalDate,
        note: String
    ) {
        viewModelScope.launch {
            val expense = Expense(
                id = id ?: 0,
                amount = amount,
                category = category,
                date = date,
                note = note
            )
            if (id == null) {
                repository.insertExpense(expense)
            } else {
                repository.updateExpense(expense)
            }
            screenState.value = Screen.List
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }

    fun setBudget(amount: Double?) {
        viewModelScope.launch {
            val month = selectedMonth.value
            if (amount == null) {
                repository.clearBudget(month)
            } else {
                repository.setBudget(month, amount)
            }
        }
    }

    fun addRecurringExpense(
        amount: Double,
        category: String,
        note: String,
        dayOfMonth: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRecurringExpense(amount, category, note, dayOfMonth)
            repository.applyRecurringForMonth(selectedMonth.value)
        }
    }

    fun deleteRecurringExpense(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteRecurringExpense(id)
        }
    }

    fun updateSearch(query: String) {
        filterState.update { it.copy(query = query) }
    }

    fun toggleCategoryFilter(category: String) {
        filterState.update { current ->
            val updated = if (current.selectedCategories.contains(category)) {
                current.selectedCategories - category
            } else {
                current.selectedCategories + category
            }
            current.copy(selectedCategories = updated)
        }
    }

    fun updateMinAmount(input: String) {
        filterState.update { it.copy(minAmountInput = input) }
    }

    fun updateMaxAmount(input: String) {
        filterState.update { it.copy(maxAmountInput = input) }
    }

    fun updateStartDate(date: LocalDate?) {
        filterState.update { it.copy(startDate = date) }
    }

    fun updateEndDate(date: LocalDate?) {
        filterState.update { it.copy(endDate = date) }
    }

    fun clearFilters() {
        filterState.value = ExpenseFilters()
    }

    fun setThemeMode(mode: ThemeMode) {
        themeState.update { it.copy(mode = mode) }
    }

    fun setThemeAccent(accent: ThemeAccent) {
        themeState.update { it.copy(accent = accent) }
    }

    fun setRemindersEnabled(enabled: Boolean) {
        reminderScheduler.setEnabled(enabled)
        reminderState.value = enabled
    }

    fun addCustomCategory(label: String, emoji: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCustomCategory(label, emoji)
        }
    }
}

private fun applyFilters(expenses: List<Expense>, filters: ExpenseFilters): List<Expense> {
    val query = filters.query.trim().lowercase()
    val minAmount = filters.minAmountInput.toDoubleOrNull()
    val maxAmount = filters.maxAmountInput.toDoubleOrNull()
    val startDate = filters.startDate
    val endDate = filters.endDate

    return expenses.filter { expense ->
        val matchesQuery = if (query.isBlank()) {
            true
        } else {
            expense.category.lowercase().contains(query) ||
                expense.note.lowercase().contains(query)
        }
        val matchesCategory = if (filters.selectedCategories.isEmpty()) {
            true
        } else {
            filters.selectedCategories.contains(expense.category)
        }
        val matchesMin = minAmount?.let { expense.amount >= it } ?: true
        val matchesMax = maxAmount?.let { expense.amount <= it } ?: true
        val matchesStart = startDate?.let { !expense.date.isBefore(it) } ?: true
        val matchesEnd = endDate?.let { !expense.date.isAfter(it) } ?: true

        matchesQuery && matchesCategory && matchesMin && matchesMax && matchesStart && matchesEnd
    }
}

private fun mergeCategories(
    defaults: List<CategoryOption>,
    custom: List<CustomCategory>
): List<CategoryOption> {
    if (custom.isEmpty()) return defaults
    val customMap = custom.associateBy { it.label }
    val mergedDefaults = defaults.map { option ->
        customMap[option.label]?.let { CategoryOption(it.label, it.emoji) } ?: option
    }
    val extras = custom.filter { item ->
        defaults.none { it.label == item.label }
    }.map { CategoryOption(it.label, it.emoji) }
    return mergedDefaults + extras
}
