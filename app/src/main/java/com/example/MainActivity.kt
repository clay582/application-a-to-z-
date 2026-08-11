package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.AddExpenseDialog
import com.example.ui.components.M3akBottomNavigationBar
import com.example.ui.components.M3akSidebarNavigation
import com.example.ui.screens.AiCoachScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.GoalsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.M3AKTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            val budgetState by viewModel.budgetState.collectAsStateWithLifecycle()
            val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
            val allGoals by viewModel.allGoals.collectAsStateWithLifecycle()
            val aiMessages by viewModel.aiMessages.collectAsStateWithLifecycle()
            val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
            val parsedCandidate by viewModel.parsedExpenseCandidate.collectAsStateWithLifecycle()
            val isParsing by viewModel.isParsingExpense.collectAsStateWithLifecycle()

            val windowSizeClass = calculateWindowSizeClass(this)
            val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

            var currentRoute by remember { mutableStateOf(Screen.Home.route) }
            var showAddExpenseDialog by remember { mutableStateOf(false) }

            // Force Right-To-Left direction for Moroccan Darija & Arabic
            val layoutDirection = if (userProfile.language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                M3AKTheme(darkTheme = userProfile.darkModeEnabled) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if (!userProfile.isOnboardingCompleted) {
                            OnboardingScreen(
                                initialProfile = userProfile,
                                onCompleteOnboarding = { updatedProfile ->
                                    viewModel.updateProfile(updatedProfile)
                                }
                            )
                        } else {
                            Scaffold(
                                bottomBar = {
                                    if (!isExpanded) {
                                        M3akBottomNavigationBar(
                                            currentRoute = currentRoute,
                                            onNavigate = { route -> currentRoute = route },
                                            onAddExpenseClick = { showAddExpenseDialog = true },
                                            lang = userProfile.language
                                        )
                                    }
                                }
                            ) { paddingValues ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingValues)
                                ) {
                                    if (isExpanded) {
                                        M3akSidebarNavigation(
                                            currentRoute = currentRoute,
                                            onNavigate = { route -> currentRoute = route },
                                            onAddExpenseClick = { showAddExpenseDialog = true },
                                            lang = userProfile.language
                                        )
                                    }

                                    Box(modifier = Modifier.weight(1f)) {
                                        AnimatedContent(targetState = currentRoute, label = "ScreenTransition") { route ->
                                            when (route) {
                                                Screen.Home.route -> HomeScreen(
                                                    profile = userProfile,
                                                    budget = budgetState,
                                                    expenses = allExpenses,
                                                    onAddExpenseClick = { showAddExpenseDialog = true },
                                                    onViewAllExpensesClick = { currentRoute = Screen.Expenses.route },
                                                    onOpenAiCoachClick = { currentRoute = Screen.AiCoach.route },
                                                    lang = userProfile.language
                                                )

                                                Screen.Expenses.route -> ExpensesScreen(
                                                    expenses = allExpenses,
                                                    onDeleteExpense = { exp -> viewModel.deleteExpense(exp) },
                                                    lang = userProfile.language
                                                )

                                                Screen.Goals.route -> GoalsScreen(
                                                    goals = allGoals,
                                                    onAddGoal = { title, target, current, deadline, emoji ->
                                                        viewModel.addGoal(title, target, current, deadline, emoji)
                                                    },
                                                    onUpdateGoalAmount = { goal, delta ->
                                                        viewModel.updateGoalAmount(goal, delta)
                                                    },
                                                    onDeleteGoal = { goal -> viewModel.deleteGoal(goal) },
                                                    lang = userProfile.language
                                                )

                                                Screen.Analytics.route -> AnalyticsScreen(
                                                    budget = budgetState,
                                                    expenses = allExpenses,
                                                    lang = userProfile.language
                                                )

                                                Screen.AiCoach.route -> AiCoachScreen(
                                                    messages = aiMessages,
                                                    isLoading = isAiLoading,
                                                    budget = budgetState,
                                                    onSendMessage = { text -> viewModel.sendAiMessage(text) },
                                                    lang = userProfile.language
                                                )

                                                Screen.Settings.route -> SettingsScreen(
                                                    profile = userProfile,
                                                    onUpdateProfile = { prof -> viewModel.updateProfile(prof) },
                                                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                                                    onChangeLanguage = { lang -> viewModel.changeLanguage(lang) },
                                                    onResetData = { viewModel.resetAllData() },
                                                    lang = userProfile.language
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Global Add Expense Modal Dialog
                            if (showAddExpenseDialog) {
                                AddExpenseDialog(
                                    onDismiss = { showAddExpenseDialog = false },
                                    onSaveExpense = { amount, category, desc, paymentMethod, note ->
                                        viewModel.addExpense(amount, category, desc, paymentMethod, notes = note)
                                    },
                                    onParseNaturalLanguage = { text ->
                                        viewModel.parseNaturalLanguageExpense(text)
                                    },
                                    parsedCandidate = parsedCandidate,
                                    isParsing = isParsing,
                                    onConfirmParsedCandidate = {
                                        viewModel.confirmParsedExpense()
                                    },
                                    onCancelCandidate = {
                                        viewModel.cancelParsedExpense()
                                    },
                                    lang = userProfile.language
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
