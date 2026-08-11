package com.example.data

import com.example.model.ExpenseCategory
import com.example.model.ExpenseEntity
import com.example.model.GoalEntity
import com.example.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class M3akRepository(
    private val expenseDao: ExpenseDao,
    private val goalDao: GoalDao,
    private val userProfileDao: UserProfileDao
) {

    val userProfile: Flow<UserProfileEntity?> = userProfileDao.getUserProfile()
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    val allGoals: Flow<List<GoalEntity>> = goalDao.getAllGoals()

    suspend fun initializeDefaultDataIfNeeded() {
        val currentProfile = userProfileDao.getUserProfile().firstOrNull()
        if (currentProfile == null) {
            // Default Profile as required by prompt
            val defaultProfile = UserProfileEntity(
                id = 1,
                name = "Yassine",
                email = "yassine@example.com",
                monthlySalary = 8000.0,
                salaryDay = 25,
                fixedExpensesTotal = 2500.0,
                monthlySavingsTarget = 1000.0,
                language = "ar",
                currency = "DH",
                darkModeEnabled = false,
                notificationsEnabled = true,
                isOnboardingCompleted = true
            )
            userProfileDao.insertOrUpdateProfile(defaultProfile)

            // Sample initial expenses
            val now = System.currentTimeMillis()
            val dayInMillis = 86400000L

            val sampleExpenses = listOf(
                ExpenseEntity(
                    amount = 120.0,
                    categoryId = ExpenseCategory.FOOD.id,
                    description = "غداء مع الأصدقاء",
                    paymentMethod = "CASH",
                    dateTimestamp = now - (0 * dayInMillis)
                ),
                ExpenseEntity(
                    amount = 30.0,
                    categoryId = ExpenseCategory.TRANSPORT.id,
                    description = "طاكسي صغير",
                    paymentMethod = "CASH",
                    dateTimestamp = now - (0 * dayInMillis)
                ),
                ExpenseEntity(
                    amount = 260.0,
                    categoryId = ExpenseCategory.FOOD.id,
                    description = "تقضية المرجان الأسبوعية",
                    paymentMethod = "CARD",
                    dateTimestamp = now - (2 * dayInMillis)
                ),
                ExpenseEntity(
                    amount = 99.0,
                    categoryId = ExpenseCategory.BILLS.id,
                    description = "تعبئة الهاتف والإنترنت",
                    paymentMethod = "CARD",
                    dateTimestamp = now - (5 * dayInMillis)
                ),
                ExpenseEntity(
                    amount = 577.0,
                    categoryId = ExpenseCategory.FOOD.id,
                    description = "خضار وفواكه ومواد غذائية",
                    paymentMethod = "CASH",
                    dateTimestamp = now - (8 * dayInMillis)
                )
            )

            for (exp in sampleExpenses) {
                expenseDao.insertExpense(exp)
            }

            // Sample Goals
            val sampleGoals = listOf(
                GoalEntity(
                    title = "سفر تركيا ✈️",
                    targetAmount = 10000.0,
                    currentAmount = 6500.0,
                    deadlineDate = "ديسمبر 2026",
                    emojiIcon = "✈️"
                ),
                GoalEntity(
                    title = "صندوق الطوارئ 🛡️",
                    targetAmount = 15000.0,
                    currentAmount = 5000.0,
                    deadlineDate = "يونيو 2027",
                    emojiIcon = "💰"
                )
            )

            for (goal in sampleGoals) {
                goalDao.insertGoal(goal)
            }
        }
    }

    suspend fun updateProfile(profile: UserProfileEntity) {
        userProfileDao.insertOrUpdateProfile(profile)
    }

    suspend fun addExpense(expense: ExpenseEntity): Long {
        return expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun deleteExpenseById(id: Long) {
        expenseDao.deleteExpenseById(id)
    }

    suspend fun addGoal(goal: GoalEntity): Long {
        return goalDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: GoalEntity) {
        goalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: GoalEntity) {
        goalDao.deleteGoal(goal)
    }

    suspend fun resetAllData() {
        expenseDao.deleteAll()
        userProfileDao.insertOrUpdateProfile(
            UserProfileEntity(
                id = 1,
                name = "Yassine",
                monthlySalary = 8000.0,
                salaryDay = 25,
                fixedExpensesTotal = 2500.0,
                monthlySavingsTarget = 1000.0,
                isOnboardingCompleted = false
            )
        )
    }
}
