package com.example.expensetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensetracker.data.ExpenseRepository

class ExpenseViewModelFactory(
    private val repository: ExpenseRepository,
    private val budgetAlertManager: BudgetAlertManager,
    private val reminderScheduler: ReminderScheduler
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repository, budgetAlertManager, reminderScheduler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
