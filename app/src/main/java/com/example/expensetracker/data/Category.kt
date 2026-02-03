package com.example.expensetracker.data

data class CustomCategory(
    val id: Long,
    val label: String,
    val emoji: String
)

fun CategoryEntity.toDomain(): CustomCategory {
    return CustomCategory(
        id = id,
        label = label,
        emoji = emoji
    )
}
