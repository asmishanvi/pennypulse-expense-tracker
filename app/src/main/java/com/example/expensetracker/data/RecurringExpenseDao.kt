package com.example.expensetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringExpenseDao {
    @Query("SELECT * FROM recurring_expenses ORDER BY dayOfMonth ASC")
    fun recurringExpenses(): Flow<List<RecurringExpenseEntity>>

    @Query("SELECT * FROM recurring_expenses WHERE isActive = 1")
    suspend fun activeRecurring(): List<RecurringExpenseEntity>

    @Insert
    suspend fun insertRecurring(expense: RecurringExpenseEntity): Long

    @Update
    suspend fun updateRecurring(expense: RecurringExpenseEntity)

    @Query("DELETE FROM recurring_expenses WHERE id = :id")
    suspend fun deleteRecurring(id: Long)
}
