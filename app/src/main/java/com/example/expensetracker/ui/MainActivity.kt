package com.example.expensetracker.ui

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.expensetracker.ExpenseTrackerApplication
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.ui.theme.backgroundBrush

class MainActivity : ComponentActivity() {
    private val viewModel: ExpenseViewModel by viewModels {
        val app = application as ExpenseTrackerApplication
        ExpenseViewModelFactory(
            app.repository,
            BudgetAlertManager(applicationContext),
            ReminderScheduler(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (uiState.themeMode) {
                ThemeMode.Dark -> true
                ThemeMode.Light -> false
                ThemeMode.System -> systemDark
            }
            ExpenseTrackerTheme(
                darkTheme = darkTheme,
                accent = uiState.themeAccent
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundBrush(900.dp, darkTheme, uiState.themeAccent))
                    ) {
                        BackgroundOrbs(
                            modifier = Modifier.fillMaxSize(),
                            darkTheme = darkTheme,
                            accent = uiState.themeAccent
                        )
                        ExpenseTrackerApp(
                            uiState = uiState,
                            onPreviousMonth = viewModel::previousMonth,
                            onNextMonth = viewModel::nextMonth,
                            onAddExpense = viewModel::openAddExpense,
                            onEditExpense = viewModel::openEditExpense,
                            onDeleteExpense = viewModel::deleteExpense,
                            onSaveExpense = viewModel::saveExpense,
                            onCloseEditor = viewModel::closeEditor,
                            onOpenInsights = viewModel::openInsights,
                            onOpenHome = viewModel::openHome,
                            onSetBudget = viewModel::setBudget,
                            onAddRecurring = viewModel::addRecurringExpense,
                            onDeleteRecurring = viewModel::deleteRecurringExpense,
                            onSearchChange = viewModel::updateSearch,
                            onToggleCategory = viewModel::toggleCategoryFilter,
                            onMinAmountChange = viewModel::updateMinAmount,
                            onMaxAmountChange = viewModel::updateMaxAmount,
                            onStartDateChange = viewModel::updateStartDate,
                            onEndDateChange = viewModel::updateEndDate,
                            onClearFilters = viewModel::clearFilters,
                            onThemeModeChange = viewModel::setThemeMode,
                            onAccentChange = viewModel::setThemeAccent,
                            onToggleReminders = viewModel::setRemindersEnabled,
                            onAddCategory = viewModel::addCustomCategory
                        )
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}

@Composable
private fun ExpenseTrackerApp(
    uiState: ExpenseUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddExpense: () -> Unit,
    onEditExpense: (Long) -> Unit,
    onDeleteExpense: (Long) -> Unit,
    onSaveExpense: (Long?, Double, String, java.time.LocalDate, String) -> Unit,
    onCloseEditor: () -> Unit,
    onOpenInsights: () -> Unit,
    onOpenHome: () -> Unit,
    onSetBudget: (Double?) -> Unit,
    onAddRecurring: (Double, String, String, Int) -> Unit,
    onDeleteRecurring: (Long) -> Unit,
    onSearchChange: (String) -> Unit,
    onToggleCategory: (String) -> Unit,
    onMinAmountChange: (String) -> Unit,
    onMaxAmountChange: (String) -> Unit,
    onStartDateChange: (java.time.LocalDate?) -> Unit,
    onEndDateChange: (java.time.LocalDate?) -> Unit,
    onClearFilters: () -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onAccentChange: (com.example.expensetracker.ui.theme.ThemeAccent) -> Unit,
    onToggleReminders: (Boolean) -> Unit,
    onAddCategory: (String, String) -> Unit
) {
    AnimatedContent(
        targetState = uiState.screen,
        transitionSpec = {
            if (targetState is Screen.Edit || initialState is Screen.Edit) {
                (slideInVertically { it / 6 } + fadeIn()) togetherWith
                    (slideOutVertically { -it / 6 } + fadeOut())
            } else {
                fadeIn() togetherWith fadeOut()
            }
        },
        label = "screen-transition"
    ) { screen ->
        when (screen) {
            is Screen.Edit -> {
                val expense = uiState.expenses.firstOrNull { it.id == screen.expenseId }
                ExpenseEditScreen(
                    expense = expense,
                    categories = uiState.categories,
                    onSave = onSaveExpense,
                    onClose = onCloseEditor,
                    onAddCategory = onAddCategory
                )
            }
            else -> {
                Scaffold(
                    bottomBar = {
                        AppBottomBar(
                            current = screen,
                            onOpenHome = onOpenHome,
                            onOpenInsights = onOpenInsights
                        )
                    },
                    containerColor = Color.Transparent
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (screen) {
                        is Screen.List -> ExpenseListScreen(
                            uiState = uiState,
                            onPreviousMonth = onPreviousMonth,
                            onNextMonth = onNextMonth,
                            onAddExpense = onAddExpense,
                            onEditExpense = onEditExpense,
                            onDeleteExpense = onDeleteExpense,
                            onSearchChange = onSearchChange,
                            onToggleCategory = onToggleCategory,
                            onMinAmountChange = onMinAmountChange,
                            onMaxAmountChange = onMaxAmountChange,
                            onStartDateChange = onStartDateChange,
                            onEndDateChange = onEndDateChange,
                            onClearFilters = onClearFilters
                        )
                        is Screen.Insights -> InsightsScreen(
                            uiState = uiState,
                            onPreviousMonth = onPreviousMonth,
                            onNextMonth = onNextMonth,
                            onSetBudget = onSetBudget,
                            onAddRecurring = onAddRecurring,
                            onDeleteRecurring = onDeleteRecurring,
                            onThemeModeChange = onThemeModeChange,
                            onAccentChange = onAccentChange,
                            onToggleReminders = onToggleReminders,
                            onAddCategory = onAddCategory
                        )
                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppBottomBar(
    current: Screen,
    onOpenHome: () -> Unit,
    onOpenInsights: () -> Unit
) {
    val shape = RoundedCornerShape(28.dp)
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .shadow(10.dp, shape, clip = false)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
    ) {
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = current is Screen.List,
                onClick = onOpenHome,
                icon = {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
                },
                label = { Text("Home") },
                colors = itemColors
            )
            NavigationBarItem(
                selected = current is Screen.Insights,
                onClick = onOpenInsights,
                icon = {
                    Icon(imageVector = Icons.Default.BarChart, contentDescription = "Insights")
                },
                label = { Text("Insights") },
                colors = itemColors
            )
        }
    }
}
