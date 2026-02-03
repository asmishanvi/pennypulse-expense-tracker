package com.example.expensetracker.data

import java.time.LocalDate


data class Expense(
    val id: Long,
    val amount: Double,
    val category: String,
    val date: LocalDate,
    val note: String
)

fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        id = id,
        amount = amount,
        category = category,
        date = LocalDate.ofEpochDay(dateEpochDay),
        note = note
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        amount = amount,
        category = category,
        dateEpochDay = date.toEpochDay(),
        note = note
    )
}
