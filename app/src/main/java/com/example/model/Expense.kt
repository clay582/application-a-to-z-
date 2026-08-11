package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val categoryId: String,
    val description: String,
    val paymentMethod: String = "CASH", // CASH, CARD, TRANSFER
    val dateTimestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
