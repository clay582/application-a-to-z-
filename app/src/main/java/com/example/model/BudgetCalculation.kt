package com.example.model

data class BudgetCalculation(
    val monthlyIncome: Double,
    val fixedExpensesTotal: Double,
    val savingsTarget: Double,
    val totalAvailableVariableBudget: Double, // Income - Fixed - Savings
    val spentInCycle: Double,
    val remainingCycleBudget: Double, // Available - SpentInCycle
    val totalDaysInCycle: Int,
    val daysRemainingInCycle: Int,
    val safeDailySpend: Double, // remainingCycleBudget / max(1, daysRemainingInCycle)
    val spentToday: Double,
    val todayRemainingBudget: Double, // safeDailySpend - spentToday
    val isOverBudgetToday: Boolean,
    val cycleStartDateFormatted: String,
    val cycleEndDateFormatted: String,
    val percentSpentOfVariableBudget: Float, // 0.0f..1.0f
    val moneyHealthScore: Int // 0..100
)
