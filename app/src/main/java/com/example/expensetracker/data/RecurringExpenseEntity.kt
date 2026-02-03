package com.example.expensetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_expenses")
data class RecurringExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val note: String,
    val dayOfMonth: Int,
    val isActive: Boolean = true,
    val lastAppliedMonthEpochDay: Long = 0
)
