package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.M3akRepository
import com.example.model.BudgetCalculation
import com.example.model.ExpenseCategory
import com.example.model.ExpenseEntity
import com.example.model.GoalEntity
import com.example.model.UserProfileEntity
import com.example.util.BudgetEngine
import com.example.util.GeminiService
import com.example.util.ParsedExpense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AiChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Expenses : Screen("expenses")
    object Goals : Screen("goals")
    object Analytics : Screen("analytics")
    object AiCoach : Screen("ai_coach")
    object Settings : Screen("settings")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = M3akRepository(db.expenseDao(), db.goalDao(), db.userProfileDao())

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded()
        }
    }

    val userProfile: StateFlow<UserProfileEntity> = repository.userProfile
        .combine(MutableStateFlow(Unit)) { profile, _ ->
            profile ?: UserProfileEntity()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfileEntity()
        )

    val allExpenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allGoals: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Calculated Budget state combined reactively whenever profile or expenses change!
    val budgetState: StateFlow<BudgetCalculation> = combine(userProfile, allExpenses) { profile, expenses ->
        BudgetEngine.calculateBudget(profile, expenses)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetEngine.calculateBudget(UserProfileEntity(), emptyList())
    )

    // AI Chat Messages State
    private val _aiMessages = MutableStateFlow<List<AiChatMessage>>(
        listOf(
            AiChatMessage(
                text = "سلام! أنا M3AK AI 🤖 مستشار المالي الشخصي. كنزول أي تساؤل على budget ديالك، مصاريفك، ولا شحال تقدر تصرف اليوم.",
                isUser = false
            )
        )
    )
    val aiMessages: StateFlow<List<AiChatMessage>> = _aiMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Natural Language Expense Dialog State
    private val _parsedExpenseCandidate = MutableStateFlow<ParsedExpense?>(null)
    val parsedExpenseCandidate: StateFlow<ParsedExpense?> = _parsedExpenseCandidate.asStateFlow()

    private val _isParsingExpense = MutableStateFlow(false)
    val isParsingExpense: StateFlow<Boolean> = _isParsingExpense.asStateFlow()

    fun updateProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.updateProfile(profile)
        }
    }

    fun addExpense(
        amount: Double,
        category: ExpenseCategory,
        description: String,
        paymentMethod: String = "CASH",
        dateTimestamp: Long = System.currentTimeMillis(),
        notes: String = ""
    ) {
        viewModelScope.launch {
            val expense = ExpenseEntity(
                amount = amount,
                categoryId = category.id,
                description = description,
                paymentMethod = paymentMethod,
                dateTimestamp = dateTimestamp,
                notes = notes
            )
            repository.addExpense(expense)
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun parseNaturalLanguageExpense(text: String) {
        viewModelScope.launch {
            _isParsingExpense.value = true
            val parsed = GeminiService.parseExpenseFromText(text)
            _parsedExpenseCandidate.value = parsed
            _isParsingExpense.value = false
        }
    }

    fun confirmParsedExpense() {
        val candidate = _parsedExpenseCandidate.value
        if (candidate != null) {
            addExpense(
                amount = candidate.amount,
                category = candidate.category,
                description = candidate.description
            )
            _parsedExpenseCandidate.value = null
        }
    }

    fun cancelParsedExpense() {
        _parsedExpenseCandidate.value = null
    }

    fun sendAiMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = AiChatMessage(text = userText, isUser = true)
        _aiMessages.value = _aiMessages.value + userMsg

        viewModelScope.launch {
            _isAiLoading.value = true
            val currentBudget = budgetState.value
            val userName = userProfile.value.name
            val replyText = GeminiService.getAiCoachResponse(userText, currentBudget, userName)
            
            val aiMsg = AiChatMessage(text = replyText, isUser = false)
            _aiMessages.value = _aiMessages.value + aiMsg
            _isAiLoading.value = false
        }
    }

    fun addGoal(title: String, targetAmount: Double, currentAmount: Double, deadline: String, emoji: String) {
        viewModelScope.launch {
            val goal = GoalEntity(
                title = title,
                targetAmount = targetAmount,
                currentAmount = currentAmount,
                deadlineDate = deadline,
                emojiIcon = emoji
            )
            repository.addGoal(goal)
        }
    }

    fun updateGoalAmount(goal: GoalEntity, deltaAmount: Double) {
        viewModelScope.launch {
            val updated = goal.copy(
                currentAmount = (goal.currentAmount + deltaAmount).coerceAtLeast(0.0)
            )
            repository.updateGoal(updated)
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    fun toggleDarkMode() {
        val current = userProfile.value
        updateProfile(current.copy(darkModeEnabled = !current.darkModeEnabled))
    }

    fun changeLanguage(lang: String) {
        val current = userProfile.value
        updateProfile(current.copy(language = lang))
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
        }
    }
}
