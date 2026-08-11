package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BudgetCalculation
import com.example.model.ExpenseCategory
import com.example.model.ExpenseEntity
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SkyBlue

@Composable
fun AnalyticsScreen(
    budget: BudgetCalculation,
    expenses: List<ExpenseEntity>,
    lang: String,
    modifier: Modifier = Modifier
) {
    val totalSpent = budget.spentInCycle
    val avgDaily = if (budget.totalDaysInCycle > 0) totalSpent / budget.totalDaysInCycle else 0.0

    val categoryMap = ExpenseCategory.entries.associateWith { cat ->
        expenses.filter { it.categoryId.equals(cat.id, ignoreCase = true) }.sumOf { it.amount }
    }

    val topCategoryPair = categoryMap.maxByOrNull { it.value }
    val topCategoryName = topCategoryPair?.key?.arabicName ?: "لا يوجد"

    val savingsRate = if (budget.monthlyIncome > 0) {
        ((budget.savingsTarget / budget.monthlyIncome) * 100).toInt()
    } else 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "التحليلات المالية 📊",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "فهم دقيق لنمط مصاريفك هذا الشهر",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Summary Metric Cards Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "مجموع المصاريف",
                    value = "${totalSpent.toInt()} DH",
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "معدل الصرف اليومي",
                    value = "${avgDaily.toInt()} DH",
                    accentColor = SkyBlue,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "أعلى فئة صرف",
                    value = topCategoryName,
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "نسبة الادخار",
                    value = "$savingsRate%",
                    accentColor = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Category Breakdown Chart
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "التوزيع حسب الفئات",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val nonZeroCategories = categoryMap.filter { it.value > 0 }

                    if (nonZeroCategories.isEmpty()) {
                        Text("لا توجد مصاريف مسجلة هذا الشهر.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        val maxCatSpent = nonZeroCategories.values.maxOrNull() ?: 1.0

                        nonZeroCategories.forEach { (cat, amount) ->
                            val pct = (amount / totalSpent.coerceAtLeast(1.0) * 100).toInt()
                            val barWidthFactor = (amount / maxCatSpent).toFloat().coerceIn(0.05f, 1f)

                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(cat.emoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(cat.arabicName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text("${amount.toInt()} DH ($pct%)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                LinearProgressIndicator(
                                    progress = { barWidthFactor },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    color = Color(cat.colorHex),
                                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = accentColor
            )
        }
    }
}
