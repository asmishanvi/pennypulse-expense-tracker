package com.example.expensetracker

import android.app.Application
import com.example.expensetracker.data.AppDatabase
import com.example.expensetracker.data.ExpenseRepository

class ExpenseTrackerApplication : Application() {
    lateinit var repository: ExpenseRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        repository = ExpenseRepository(
            expenseDao = database.expenseDao(),
            budgetDao = database.budgetDao(),
            recurringExpenseDao = database.recurringExpenseDao(),
            categoryDao = database.categoryDao()
        )
    }
}
