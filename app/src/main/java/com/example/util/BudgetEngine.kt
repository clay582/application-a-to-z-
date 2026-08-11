package com.example.util

import com.example.model.BudgetCalculation
import com.example.model.ExpenseEntity
import com.example.model.UserProfileEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max

object BudgetEngine {

    fun calculateBudget(
        profile: UserProfileEntity,
        allExpenses: List<ExpenseEntity>
    ): BudgetCalculation {
        val now = Calendar.getInstance()
        val salaryDay = profile.salaryDay.coerceIn(1, 31)

        // 1. Determine Cycle Start and End Date
        val cycleStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val todayDayOfMonth = now.get(Calendar.DAY_OF_MONTH)

        if (todayDayOfMonth >= salaryDay) {
            // Cycle started on salaryDay of this month
            val maxDaysThisMonth = cycleStart.getActualMaximum(Calendar.DAY_OF_MONTH)
            cycleStart.set(Calendar.DAY_OF_MONTH, salaryDay.coerceAtMost(maxDaysThisMonth))
        } else {
            // Cycle started on salaryDay of previous month
            cycleStart.add(Calendar.MONTH, -1)
            val maxDaysPrevMonth = cycleStart.getActualMaximum(Calendar.DAY_OF_MONTH)
            cycleStart.set(Calendar.DAY_OF_MONTH, salaryDay.coerceAtMost(maxDaysPrevMonth))
        }

        val cycleEnd = (cycleStart.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        val cycleStartMillis = cycleStart.timeInMillis
        val cycleEndMillis = cycleEnd.timeInMillis

        // 2. Filter expenses in this cycle
        val cycleExpenses = allExpenses.filter {
            it.dateTimestamp in cycleStartMillis..cycleEndMillis
        }

        val spentInCycle = cycleExpenses.sumOf { it.amount }

        // 3. Filter expenses today
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val todayEnd = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        val spentToday = allExpenses
            .filter { it.dateTimestamp in todayStart..todayEnd }
            .sumOf { it.amount }

        // 4. Calculate Days
        val totalDaysInCycle = max(1, ((cycleEndMillis - cycleStartMillis) / (1000 * 60 * 60 * 24)).toInt() + 1)
        
        // Days remaining including today
        val todayMillis = now.timeInMillis
        val daysRemainingInCycle = max(1, ((cycleEndMillis - todayMillis) / (1000 * 60 * 60 * 24)).toInt() + 1)

        // 5. Calculate Variable Budget & Safe Daily Spend
        val income = profile.monthlySalary
        val fixed = profile.fixedExpensesTotal
        val savings = profile.monthlySavingsTarget

        val totalAvailableVariableBudget = max(0.0, income - fixed - savings)
        val remainingCycleBudget = totalAvailableVariableBudget - spentInCycle

        val safeDailySpend = if (remainingCycleBudget > 0) {
            remainingCycleBudget / daysRemainingInCycle
        } else {
            0.0
        }

        val todayRemainingBudget = safeDailySpend - spentToday
        val isOverBudgetToday = spentToday > safeDailySpend && safeDailySpend > 0

        val dateFormat = SimpleDateFormat("dd MMM", Locale("ar"))
        val cycleStartDateFormatted = dateFormat.format(Date(cycleStartMillis))
        val cycleEndDateFormatted = dateFormat.format(Date(cycleEndMillis))

        val percentSpent = if (totalAvailableVariableBudget > 0) {
            (spentInCycle / totalAvailableVariableBudget).toFloat().coerceIn(0f, 1f)
        } else {
            0f
        }

        // 6. Calculate Money Health Score (0 to 100)
        var healthScore = 80
        if (income > 0) {
            val fixedRatio = fixed / income
            if (fixedRatio > 0.5) healthScore -= 15
            else if (fixedRatio > 0.35) healthScore -= 5

            val savingsRatio = savings / income
            if (savingsRatio >= 0.15) healthScore += 10
            else if (savingsRatio < 0.05) healthScore -= 10
        }

        if (isOverBudgetToday) healthScore -= 10
        if (remainingCycleBudget < 0) healthScore -= 25

        healthScore = healthScore.coerceIn(20, 100)

        return BudgetCalculation(
            monthlyIncome = income,
            fixedExpensesTotal = fixed,
            savingsTarget = savings,
            totalAvailableVariableBudget = totalAvailableVariableBudget,
            spentInCycle = spentInCycle,
            remainingCycleBudget = remainingCycleBudget,
            totalDaysInCycle = totalDaysInCycle,
            daysRemainingInCycle = daysRemainingInCycle,
            safeDailySpend = safeDailySpend,
            spentToday = spentToday,
            todayRemainingBudget = todayRemainingBudget,
            isOverBudgetToday = isOverBudgetToday,
            cycleStartDateFormatted = cycleStartDateFormatted,
            cycleEndDateFormatted = cycleEndDateFormatted,
            percentSpentOfVariableBudget = percentSpent,
            moneyHealthScore = healthScore
        )
    }
}
