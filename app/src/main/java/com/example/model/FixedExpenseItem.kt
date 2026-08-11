package com.example.model

data class FixedExpenseItem(
    val id: String,
    val name: String,
    val amount: Double,
    val emoji: String = "🏠"
)
