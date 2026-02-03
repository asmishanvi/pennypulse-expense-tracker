package com.example.expensetracker.data

data class RecurringExpense(
    val id: Long,
    val amount: Double,
    val category: String,
    val note: String,
    val dayOfMonth: Int,
    val isActive: Boolean
)

fun RecurringExpenseEntity.toDomain(): RecurringExpense {
    return RecurringExpense(
        id = id,
        amount = amount,
        category = category,
        note = note,
        dayOfMonth = dayOfMonth,
        isActive = isActive
    )
}

fun RecurringExpense.toEntity(lastAppliedMonthEpochDay: Long = 0): RecurringExpenseEntity {
    return RecurringExpenseEntity(
        id = id,
        amount = amount,
        category = category,
        note = note,
        dayOfMonth = dayOfMonth,
        isActive = isActive,
        lastAppliedMonthEpochDay = lastAppliedMonthEpochDay
    )
}
