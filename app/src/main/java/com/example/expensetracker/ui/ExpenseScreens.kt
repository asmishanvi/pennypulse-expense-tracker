@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)

package com.example.expensetracker.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.RecurringExpense
import com.example.expensetracker.ui.theme.AccentBlue
import com.example.expensetracker.ui.theme.AccentCoral
import com.example.expensetracker.ui.theme.AccentMint
import com.example.expensetracker.ui.theme.AccentYellow
import com.example.expensetracker.ui.theme.ThemeAccent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import kotlin.math.absoluteValue
import kotlin.math.max

@Composable
fun ExpenseListScreen(
    uiState: ExpenseUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddExpense: () -> Unit,
    onEditExpense: (Long) -> Unit,
    onDeleteExpense: (Long) -> Unit,
    onSearchChange: (String) -> Unit,
    onToggleCategory: (String) -> Unit,
    onMinAmountChange: (String) -> Unit,
    onMaxAmountChange: (String) -> Unit,
    onStartDateChange: (LocalDate?) -> Unit,
    onEndDateChange: (LocalDate?) -> Unit,
    onClearFilters: () -> Unit
) {
    var showFab by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        showFab = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    BrandHeader(
                        title = "PennyPulse",
                        subtitle = "Track. Flex. Repeat.",
                        pillText = "Glow mode"
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showFab,
                enter = scaleIn(initialScale = 0.9f) + fadeIn(),
                exit = scaleOut(targetScale = 0.9f) + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = onAddExpense,
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add expense"
                    )
                }
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MonthSwitcher(
                    monthLabel = formatMonth(uiState.month),
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth
                )
            }

            item {
                FiltersPanel(
                    filters = uiState.filters,
                    categories = uiState.categories,
                    onSearchChange = onSearchChange,
                    onToggleCategory = onToggleCategory,
                    onMinAmountChange = onMinAmountChange,
                    onMaxAmountChange = onMaxAmountChange,
                    onStartDateChange = onStartDateChange,
                    onEndDateChange = onEndDateChange,
                    onClearFilters = onClearFilters
                )
            }

            item {
                SummaryCard(
                    total = if (uiState.filtersActive) uiState.filteredTotal else uiState.total,
                    count = if (uiState.filtersActive) uiState.filteredExpenses.size else uiState.expenses.size,
                    subtitle = if (uiState.filtersActive) "Filtered spend" else "This month"
                )
            }

            if (uiState.filteredExpenses.isEmpty()) {
                item {
                    EmptyState(
                        isFiltered = uiState.filtersActive
                    )
                }
            } else {
                items(uiState.filteredExpenses, key = { it.id }) { expense ->
                    val emoji = emojiForCategory(expense.category, uiState.categories)
                    ExpenseRow(
                        expense = expense,
                        onEdit = { onEditExpense(expense.id) },
                        onDelete = { onDeleteExpense(expense.id) },
                        modifier = Modifier.animateItemPlacement(),
                        emoji = emoji
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun BrandHeader(
    title: String,
    subtitle: String,
    pillText: String
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(brush = gradient),
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        AccentPill(text = pillText)
    }
}

@Composable
private fun AccentPill(text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 20.dp,
    verticalSpacing: Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(26.dp)
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)
        )
    )
    Card(
        modifier = modifier
            .shadow(10.dp, shape, clip = false)
            .border(1.dp, borderBrush, shape),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.86f)
                        )
                    )
                )
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(verticalSpacing)
        ) {
            content()
        }
    }
}

@Composable
private fun SummaryCard(total: Double, count: Int, subtitle: String) {
    val average = if (count == 0) 0.0 else total / count
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val baseGradient = Brush.linearGradient(
        colors = listOf(primary, secondary)
    )
    val shimmerTransition = rememberInfiniteTransition(label = "summaryShimmer")
    val shimmerProgress by shimmerTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "summaryShimmerProgress"
    )
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(26.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.shadow(12.dp, RoundedCornerShape(26.dp), clip = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawWithCache {
                    val startX = -size.width + (size.width * 2f) * shimmerProgress
                    val endX = startX + size.width
                    val shimmer = Brush.linearGradient(
                        colors = listOf(
                            primary,
                            secondary,
                            primary
                        ),
                        start = Offset(startX, 0f),
                        end = Offset(endX, size.height)
                    )
                    onDrawBehind {
                        drawRect(baseGradient)
                        drawRect(shimmer, alpha = 0.35f)
                    }
                }
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatCurrency(total),
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryPill(label = "Expenses", value = count.toString())
                    SummaryPill(label = "Avg", value = formatCurrency(average))
                }
            }
        }
    }
}

@Composable
private fun MonthSwitcher(
    monthLabel: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val containerShape = RoundedCornerShape(24.dp)
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        )
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, containerShape, clip = false)
            .clip(containerShape)
            .background(gradient)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                shape = containerShape
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onPreviousMonth,
            modifier = Modifier
                .size(36.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous month"
            )
        }
        Text(
            text = monthLabel,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        IconButton(
            onClick = onNextMonth,
            modifier = Modifier
                .size(36.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next month"
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FiltersPanel(
    filters: ExpenseFilters,
    categories: List<CategoryOption>,
    onSearchChange: (String) -> Unit,
    onToggleCategory: (String) -> Unit,
    onMinAmountChange: (String) -> Unit,
    onMaxAmountChange: (String) -> Unit,
    onStartDateChange: (LocalDate?) -> Unit,
    onEndDateChange: (LocalDate?) -> Unit,
    onClearFilters: () -> Unit
) {
    var showStartPicker by rememberSaveable { mutableStateOf(false) }
    var showEndPicker by rememberSaveable { mutableStateOf(false) }

    val startPickerState = rememberDatePickerState(
        initialSelectedDateMillis = filters.startDate?.toEpochMillis()
    )
    val endPickerState = rememberDatePickerState(
        initialSelectedDateMillis = filters.endDate?.toEpochMillis()
    )
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
    )

    if (showStartPicker) {
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedMillis = startPickerState.selectedDateMillis
                    onStartDateChange(selectedMillis?.let { epochMillisToLocalDate(it) })
                    showStartPicker = false
                }) {
                    Text("Set")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = startPickerState)
        }
    }

    if (showEndPicker) {
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedMillis = endPickerState.selectedDateMillis
                    onEndDateChange(selectedMillis?.let { epochMillisToLocalDate(it) })
                    showEndPicker = false
                }) {
                    Text("Set")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = endPickerState)
        }
    }

    GlassCard(contentPadding = 16.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium
                )
                if (filters.isActive()) {
                    TextButton(onClick = onClearFilters) {
                        Text("Clear")
                    }
                }
            }

            OutlinedTextField(
                value = filters.query,
                onValueChange = onSearchChange,
                label = { Text("Search") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors
            )

            if (categories.isNotEmpty()) {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleMedium
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { option ->
                        val selected = filters.selectedCategories.contains(option.label)
                        FilterChip(
                            selected = selected,
                            onClick = { onToggleCategory(option.label) },
                            label = {
                                Text("${option.emoji} ${option.label}")
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = filters.minAmountInput,
                    onValueChange = onMinAmountChange,
                    label = { Text("Min") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors
                )
                OutlinedTextField(
                    value = filters.maxAmountInput,
                    onValueChange = onMaxAmountChange,
                    label = { Text("Max") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { showStartPicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = filters.startDate?.let { "From ${formatDate(it)}" } ?: "Start date"
                    )
                }
                OutlinedButton(
                    onClick = { showEndPicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = filters.endDate?.let { "To ${formatDate(it)}" } ?: "End date"
                    )
                }
            }
    }
}

@Composable
private fun SummaryPill(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
private fun EmptyState(isFiltered: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ReceiptLong,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (isFiltered) "No results" else "No expenses yet",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isFiltered) {
                "Try clearing filters or adjusting your search."
            } else {
                "Tap + to add your first expense for this month."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun ExpenseRow(
    expense: Expense,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    emoji: String
) {
    val accent = categoryColor(expense.category)
    val cardShape = RoundedCornerShape(22.dp)
    Card(
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
            .animateContentSize()
            .shadow(8.dp, cardShape, clip = false)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    cardShape
                )
                .border(1.dp, accent.copy(alpha = 0.22f), cardShape)
                .padding(start = 16.dp, top = 18.dp, bottom = 18.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                accent,
                                accent.copy(alpha = 0.2f)
                            )
                        )
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(accent.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = expense.category,
                    style = MaterialTheme.typography.titleMedium
                )
                if (expense.note.isNotBlank()) {
                    Text(
                        text = expense.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Tag(text = formatDate(expense.date), color = accent)
            }
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .widthIn(min = 140.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatCurrency(expense.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit expense"
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete expense",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Tag(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun CategoryPicker(
    categories: List<CategoryOption>,
    selected: String,
    onSelect: (CategoryOption) -> Unit,
    onAddCategory: (() -> Unit)? = null
) {
    val chipShape = RoundedCornerShape(16.dp)
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 220.dp)
    ) {
        gridItems(categories, key = { it.label }) { option ->
            val isSelected = option.label == selected
            val background = if (isSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
            val border = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
            Column(
                modifier = Modifier
                    .shadow(if (isSelected) 8.dp else 0.dp, chipShape, clip = false)
                    .clip(chipShape)
                    .background(background)
                    .border(1.dp, border, chipShape)
                    .clickable { onSelect(option) }
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = option.emoji, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (onAddCategory != null) {
            item {
                Column(
                    modifier = Modifier
                        .shadow(6.dp, chipShape, clip = false)
                        .clip(chipShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, chipShape)
                        .clickable { onAddCategory() }
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var label by rememberSaveable { mutableStateOf("") }
    var emoji by rememberSaveable { mutableStateOf("✨") }
    val canSave = label.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Category name") },
                    placeholder = { Text("e.g. Pets") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = emoji,
                    onValueChange = { emoji = it },
                    label = { Text("Emoji") },
                    placeholder = { Text("🐶") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(label.trim(), emoji.trim()) },
                enabled = canSave
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun emojiForCategory(category: String, categories: List<CategoryOption>): String {
    return categories.firstOrNull { it.label == category }?.emoji
        ?: ExpenseCategories.emojiFor(category)
}

private fun categoryColor(category: String): Color {
    val palette = listOf(
        AccentMint,
        AccentCoral,
        AccentBlue,
        AccentYellow,
        AccentMint
    )
    val index = category.hashCode().absoluteValue % palette.size
    return palette[index]
}

@Composable
fun InsightsScreen(
    uiState: ExpenseUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSetBudget: (Double?) -> Unit,
    onAddRecurring: (Double, String, String, Int) -> Unit,
    onDeleteRecurring: (Long) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onAccentChange: (com.example.expensetracker.ui.theme.ThemeAccent) -> Unit,
    onToggleReminders: (Boolean) -> Unit,
    onAddCategory: (String, String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    BrandHeader(
                        title = "Insights",
                        subtitle = "Glow up your budget",
                        pillText = "Deep dive"
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MonthSwitcher(
                    monthLabel = formatMonth(uiState.month),
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth
                )
            }

            item {
                AppearanceCard(
                    themeMode = uiState.themeMode,
                    accent = uiState.themeAccent,
                    onThemeModeChange = onThemeModeChange,
                    onAccentChange = onAccentChange
                )
            }

            item {
                RemindersCard(
                    enabled = uiState.remindersEnabled,
                    onToggle = onToggleReminders
                )
            }

            item {
                StreakCard(expenses = uiState.expenses)
            }

            item {
                BudgetCard(
                    total = uiState.total,
                    budget = uiState.budget,
                    month = uiState.month,
                    onSetBudget = onSetBudget
                )
            }

            item {
                RecurringExpensesCard(
                    recurringExpenses = uiState.recurringExpenses,
                    categories = uiState.categories,
                    onAddRecurring = onAddRecurring,
                    onDeleteRecurring = onDeleteRecurring,
                    onAddCategory = onAddCategory
                )
            }

            item {
                CategoryBreakdownCard(expenses = uiState.expenses)
            }

            item {
                WeeklyTrendCard(
                    expenses = uiState.expenses,
                    month = uiState.month
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun BudgetCard(
    total: Double,
    budget: Double?,
    month: YearMonth,
    onSetBudget: (Double?) -> Unit
) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        disabledContainerColor = MaterialTheme.colorScheme.surface,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
    )
    var budgetInput by rememberSaveable(month) {
        mutableStateOf(budget?.toString() ?: "")
    }
    val budgetValue = budgetInput.toDoubleOrNull()
    val normalizedBudget = budget ?: budgetValue
    val progress = if (normalizedBudget != null && normalizedBudget > 0.0) {
        (total / normalizedBudget).coerceIn(0.0, 1.0)
    } else {
        0.0
    }
    val remaining = if (normalizedBudget != null) normalizedBudget - total else null

    GlassCard(contentPadding = 20.dp, verticalSpacing = 16.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Monthly budget",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (normalizedBudget != null) {
                            "Budget set for ${formatMonth(month)}"
                        } else {
                            "Set a goal to stay on track"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(92.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = progress.toFloat(),
                            strokeWidth = 7.dp,
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "used",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Spent",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = formatCurrency(total),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Remaining",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = remaining?.let { formatCurrency(it) } ?: "--",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (remaining != null && remaining < 0) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            OutlinedTextField(
                value = budgetInput,
                onValueChange = { budgetInput = it },
                label = { Text("Budget amount") },
                placeholder = { Text("500") },
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        val value = budgetValue ?: return@Button
                        if (value > 0.0) {
                            onSetBudget(value)
                        }
                    },
                    enabled = budgetValue != null && budgetValue > 0.0
                ) {
                    Text("Save budget")
                }
                TextButton(
                    onClick = {
                        budgetInput = ""
                        onSetBudget(null)
                    },
                    enabled = budget != null
                ) {
                    Text("Clear")
                }
            }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AppearanceCard(
    themeMode: ThemeMode,
    accent: ThemeAccent,
    onThemeModeChange: (ThemeMode) -> Unit,
    onAccentChange: (ThemeAccent) -> Unit
) {
    GlassCard(contentPadding = 20.dp) {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Dark mode",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = when (themeMode) {
                            ThemeMode.System -> "Following system"
                            ThemeMode.Dark -> "On"
                            ThemeMode.Light -> "Off"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { onThemeModeChange(ThemeMode.System) }) {
                        Text("System")
                    }
                    Switch(
                        checked = themeMode == ThemeMode.Dark,
                        onCheckedChange = { checked ->
                            onThemeModeChange(if (checked) ThemeMode.Dark else ThemeMode.Light)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                    )
                }
            }

            Text(
                text = "Accent",
                style = MaterialTheme.typography.titleMedium
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ThemeAccent.values().forEach { option ->
                    val selected = option == accent
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onAccentChange(option) }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(option.primary)
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
    }
}

@Composable
private fun RemindersCard(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    GlassCard(contentPadding = 20.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Smart reminders",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Daily check-in at 8:00 PM",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
            )
        }
    }
}

@Composable
private fun StreakCard(expenses: List<Expense>) {
    val stats = remember(expenses) { streakStats(expenses) }
    GlassCard(contentPadding = 20.dp) {
            Text(
                text = "Daily streak",
                style = MaterialTheme.typography.titleMedium
            )
            if (stats.current == 0) {
                Text(
                    text = "Log an expense today to start a streak ✨",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                Text(
                    text = "${stats.current} day streak",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Best",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "${stats.best} days",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Pulse points",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = stats.points.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
    }
}

private data class StreakStats(
    val current: Int,
    val best: Int,
    val points: Int
)

private fun streakStats(expenses: List<Expense>, today: LocalDate = LocalDate.now()): StreakStats {
    if (expenses.isEmpty()) return StreakStats(0, 0, 0)
    val dates = expenses.map { it.date }.distinct().sorted()
    val dateSet = dates.toSet()

    var current = 0
    var cursor = today
    while (dateSet.contains(cursor)) {
        current++
        cursor = cursor.minusDays(1)
    }

    var best = 0
    var streak = 0
    var previous: LocalDate? = null
    for (date in dates) {
        streak = if (previous == null || date == previous.plusDays(1)) {
            streak + 1
        } else {
            1
        }
        best = max(best, streak)
        previous = date
    }

    val points = current * 10 + dates.size * 2
    return StreakStats(current, best, points)
}

@Composable
private fun RecurringExpensesCard(
    recurringExpenses: List<RecurringExpense>,
    categories: List<CategoryOption>,
    onAddRecurring: (Double, String, String, Int) -> Unit,
    onDeleteRecurring: (Long) -> Unit,
    onAddCategory: (String, String) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    if (showDialog) {
        AddRecurringDialog(
            categories = categories,
            onDismiss = { showDialog = false },
            onSave = { amount, category, note, day ->
                onAddRecurring(amount, category, note, day)
                showDialog = false
            },
            onAddCategory = onAddCategory
        )
    }

    GlassCard(contentPadding = 20.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Autorenew,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Recurring expenses",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                TextButton(onClick = { showDialog = true }) {
                    Text("Add")
                }
            }

            if (recurringExpenses.isEmpty()) {
                Text(
                    text = "No recurring expenses yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                recurringExpenses.forEach { recurring ->
                    val emoji = emojiForCategory(recurring.category, categories)
                    RecurringExpenseRow(
                        recurringExpense = recurring,
                        onDelete = { onDeleteRecurring(recurring.id) },
                        emoji = emoji
                    )
                }
            }
    }
}

@Composable
private fun RecurringExpenseRow(
    recurringExpense: RecurringExpense,
    onDelete: () -> Unit,
    emoji: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        categoryColor(recurringExpense.category).copy(alpha = 0.18f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 18.sp
                )
            }
            Column {
                Text(
                    text = recurringExpense.category,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Every month on day ${recurringExpense.dayOfMonth}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatCurrency(recurringExpense.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete recurring",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun AddRecurringDialog(
    categories: List<CategoryOption>,
    onDismiss: () -> Unit,
    onSave: (Double, String, String, Int) -> Unit,
    onAddCategory: (String, String) -> Unit
) {
    val safeCategories = if (categories.isEmpty()) {
        listOf(CategoryOption("Other", "🧾"))
    } else {
        categories
    }
    var amountInput by rememberSaveable { mutableStateOf("") }
    var dayInput by rememberSaveable { mutableStateOf("1") }
    var noteInput by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(safeCategories.first().label) }
    var showAddCategory by rememberSaveable { mutableStateOf(false) }

    val amountValue = amountInput.toDoubleOrNull()
    val dayValue = dayInput.toIntOrNull()?.coerceIn(1, 31)
    val canSave = amountValue != null && amountValue > 0.0 && dayValue != null

    if (showAddCategory) {
        AddCategoryDialog(
            onDismiss = { showAddCategory = false },
            onSave = { label, emoji ->
                onAddCategory(label, emoji)
                category = label
                showAddCategory = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add recurring") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount") },
                    placeholder = { Text("12.50") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleMedium
                )
                CategoryPicker(
                    categories = safeCategories,
                    selected = category,
                    onSelect = { option -> category = option.label },
                    onAddCategory = { showAddCategory = true }
                )
                OutlinedTextField(
                    value = dayInput,
                    onValueChange = { dayInput = it },
                    label = { Text("Day of month") },
                    placeholder = { Text("1 - 31") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("Note") },
                    placeholder = { Text("Optional") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountValue ?: return@Button
                    val day = dayValue ?: return@Button
                    onSave(amount, category, noteInput, day)
                },
                enabled = canSave
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CategoryBreakdownCard(expenses: List<Expense>) {
    val totals = remember(expenses) { categoryTotals(expenses) }
    val totalAmount = totals.sumOf { it.total }
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    GlassCard(contentPadding = 20.dp, verticalSpacing = 16.dp) {
            Text(
                text = "Category split",
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(132.dp)
                        .padding(start = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        if (totalAmount <= 0.0) {
                            drawArc(
                                color = trackColor,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 28.dp.toPx(), cap = StrokeCap.Round)
                            )
                        } else {
                            var startAngle = -90f
                            totals.forEach { slice ->
                                val sweep = (slice.total / totalAmount * 360f).toFloat()
                                drawArc(
                                    color = slice.color,
                                    startAngle = startAngle,
                                    sweepAngle = sweep,
                                    useCenter = false,
                                    style = Stroke(width = 28.dp.toPx(), cap = StrokeCap.Round)
                                )
                                startAngle += sweep
                            }
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatCurrency(totalAmount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "total",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (totals.isEmpty()) {
                        Text(
                            text = "No expenses yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    } else {
                        totals.take(5).forEach { slice ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(slice.color, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = slice.category,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = formatCurrency(slice.total),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
    }
}

@Composable
private fun WeeklyTrendCard(expenses: List<Expense>, month: YearMonth) {
    val weeks = remember(expenses, month) { weeklyTotals(expenses, month) }
    val max = weeks.maxOfOrNull { it.total } ?: 0.0
    GlassCard(contentPadding = 20.dp) {
            Text(
                text = "Weekly spend",
                style = MaterialTheme.typography.titleMedium
            )
            if (weeks.isEmpty()) {
                Text(
                    text = "No activity yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeks.forEach { week ->
                        val height = if (max == 0.0) {
                            6.dp
                        } else {
                            90.dp * (week.total / max).toFloat()
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(90.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(12.dp)
                                        .height(height)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.primary,
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                                )
                                            )
                                        )
                                )
                            }
                            Text(
                                text = week.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
    }
}

private data class CategorySlice(
    val category: String,
    val total: Double,
    val color: Color
)

private data class WeeklyTotal(
    val label: String,
    val total: Double
)

private fun categoryTotals(expenses: List<Expense>): List<CategorySlice> {
    val grouped = expenses.groupBy { it.category }
    return grouped.entries.map { (category, entries) ->
        CategorySlice(
            category = category,
            total = entries.sumOf { it.amount },
            color = categoryColor(category)
        )
    }.sortedByDescending { it.total }
}

private fun weeklyTotals(expenses: List<Expense>, month: YearMonth): List<WeeklyTotal> {
    if (expenses.isEmpty()) return emptyList()
    val results = mutableListOf<WeeklyTotal>()
    var cursor = month.atDay(1)
    val end = month.atEndOfMonth()
    while (!cursor.isAfter(end)) {
        val weekEndCandidate = cursor.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        val weekEnd = if (weekEndCandidate.isAfter(end)) end else weekEndCandidate
        val total = expenses.filter { expense ->
            !expense.date.isBefore(cursor) && !expense.date.isAfter(weekEnd)
        }.sumOf { it.amount }
        results.add(
            WeeklyTotal(
                label = "${cursor.dayOfMonth}-${weekEnd.dayOfMonth}",
                total = total
            )
        )
        cursor = weekEnd.plusDays(1)
    }
    return results
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEditScreen(
    expense: Expense?,
    categories: List<CategoryOption>,
    onSave: (Long?, Double, String, LocalDate, String) -> Unit,
    onClose: () -> Unit,
    onAddCategory: (String, String) -> Unit
) {
    val isEditing = expense != null
    val safeCategories = if (categories.isEmpty()) {
        listOf(CategoryOption("Other", "🧾"))
    } else {
        categories
    }

    var amountInput by rememberSaveable { mutableStateOf(expense?.amount?.toString() ?: "") }
    var category by rememberSaveable { mutableStateOf(expense?.category ?: safeCategories.first().label) }
    var note by rememberSaveable { mutableStateOf(expense?.note ?: "") }
    var date by rememberSaveable { mutableStateOf(expense?.date ?: LocalDate.now()) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showAddCategory by rememberSaveable { mutableStateOf(false) }
    val categoriesForPicker = remember(safeCategories, category) {
        if (category.isBlank() || safeCategories.any { it.label == category }) {
            safeCategories
        } else {
            safeCategories + CategoryOption(category, "✨")
        }
    }

    val amountValue = amountInput.toDoubleOrNull()
    val isAmountValid = amountValue != null && amountValue > 0.0
    val canSave = isAmountValid && category.isNotBlank()
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        disabledContainerColor = MaterialTheme.colorScheme.surface,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
    )

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date.toEpochMillis()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        date = epochMillisToLocalDate(selectedMillis)
                    }
                    showDatePicker = false
                }) {
                    Text("Set date")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showAddCategory) {
        AddCategoryDialog(
            onDismiss = { showAddCategory = false },
            onSave = { label, emoji ->
                onAddCategory(label, emoji)
                category = label
                showAddCategory = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Expense" else "New Expense") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassCard(contentPadding = 20.dp, verticalSpacing = 16.dp) {
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Amount") },
                        placeholder = { Text("12.50") },
                        isError = amountInput.isNotBlank() && !isAmountValid,
                        shape = RoundedCornerShape(16.dp),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        CategoryPicker(
                            categories = categoriesForPicker,
                            selected = category,
                            onSelect = { option -> category = option.label },
                            onAddCategory = { showAddCategory = true }
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Date",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatDate(date),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedButton(
                                onClick = { showDatePicker = true },
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Pick date")
                            }
                        }
                    }

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Note") },
                        placeholder = { Text("Optional") },
                        shape = RoundedCornerShape(16.dp),
                        colors = fieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 4
                    )
            }

            if (!isAmountValid && amountInput.isNotBlank()) {
                Text(
                    text = "Enter a valid amount greater than 0.",
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val amount = amountValue ?: return@Button
                    onSave(expense?.id, amount, category, date, note)
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Update expense" else "Save expense")
            }
        }
    }
}
