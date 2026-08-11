package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfileEntity
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SkyBlue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    initialProfile: UserProfileEntity,
    onCompleteOnboarding: (UserProfileEntity) -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1..6

    var userName by remember { mutableStateOf(initialProfile.name) }
    var salaryText by remember { mutableStateOf("8000") }
    var salaryDay by remember { mutableIntStateOf(25) }

    var rentAmount by remember { mutableDoubleStateOf(1500.0) }
    var utilitiesAmount by remember { mutableDoubleStateOf(500.0) }
    var otherFixedAmount by remember { mutableDoubleStateOf(500.0) }

    var savingsTargetText by remember { mutableStateOf("1000") }

    val totalFixed = rentAmount + utilitiesAmount + otherFixedAmount
    val income = salaryText.toDoubleOrNull() ?: 8000.0
    val savings = savingsTargetText.toDoubleOrNull() ?: 1000.0
    val available = (income - totalFixed - savings).coerceAtLeast(0.0)

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Progress dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..6).forEach { i ->
                    val isActive = i == step
                    val isPassed = i < step
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(6.dp)
                            .width(if (isActive) 24.dp else 12.dp)
                            .background(
                                color = when {
                                    isActive -> MaterialTheme.colorScheme.primary
                                    isPassed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                },
                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Step Content
            AnimatedContent(targetState = step, label = "OnboardingSteps") { currentStep ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (currentStep) {
                        1 -> {
                            // Step 1: Welcome
                            Text("سلام 👋", fontSize = 36.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("مرحبا بك فـ M3AK", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "التطبيق اللي غادي يعاونك تتحكم ففلوسك وتبقى مرتاح حتى لآخر الشهر.",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            OutlinedTextField(
                                value = userName,
                                onValueChange = { userName = it },
                                label = { Text("شنو سميتك؟") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(0.9f)
                            )
                        }

                        2 -> {
                            // Step 2: Salary Amount
                            Text("شحال كتدخل فالشهر؟ 💰", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("أدخل مجموع الدخل الشهري ديالك بالدرهم (DH)", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Spacer(modifier = Modifier.height(32.dp))

                            OutlinedTextField(
                                value = salaryText,
                                onValueChange = { salaryText = it },
                                placeholder = { Text("8000 DH") },
                                textStyle = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(0.85f)
                            )
                        }

                        3 -> {
                            // Step 3: Salary Day (1 - 31)
                            Text("نهاراش كيدخل ليك salaire؟ 📅", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("اختر يوم دخول الدخل الشهري لتحديد بداية الدورة المالية", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "يوم $salaryDay من كل شهر",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(7),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                            ) {
                                items((1..31).toList()) { day ->
                                    val isSelected = day == salaryDay
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .size(38.dp)
                                            .background(
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                                shape = CircleShape
                                            )
                                            .clickable { salaryDay = day }
                                    ) {
                                        Text(
                                            text = "$day",
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        4 -> {
                            // Step 4: Fixed Expenses
                            Text("شنو المصاريف الثابتة ديالك؟ 🏠", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("الكراء، الضوء، الماء، الإنترنت، والقروض...", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Spacer(modifier = Modifier.height(24.dp))

                            OutlinedTextField(
                                value = rentAmount.toInt().toString(),
                                onValueChange = { rentAmount = it.toDoubleOrNull() ?: 0.0 },
                                label = { Text("🏠 الكراء والسكن (DH)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = utilitiesAmount.toInt().toString(),
                                onValueChange = { utilitiesAmount = it.toDoubleOrNull() ?: 0.0 },
                                label = { Text("💡 الضوء، الماء، الإنترنت والفيوز (DH)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = otherFixedAmount.toInt().toString(),
                                onValueChange = { otherFixedAmount = it.toDoubleOrNull() ?: 0.0 },
                                label = { Text("💳 القروض والعائلة (DH)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        5 -> {
                            // Step 5: Savings Target
                            Text("بغيت توفر على شنو؟ 🎯", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("حدد المبلغ اللي حاب توفرو كل شهر لتنفيذ أهدافك", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Spacer(modifier = Modifier.height(24.dp))

                            OutlinedTextField(
                                value = savingsTargetText,
                                onValueChange = { savingsTargetText = it },
                                placeholder = { Text("1000 DH") },
                                textStyle = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldGreen,
                                    textAlign = TextAlign.Center
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(0.85f)
                            )
                        }

                        6 -> {
                            // Step 6: Summary & Confirmation
                            Text("الملخص المالي ديالك 📊", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    SummaryRow("الدخل الشهري:", "${income.toInt()} DH", MaterialTheme.colorScheme.onSurface)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    SummaryRow("المصاريف الثابتة:", "${totalFixed.toInt()} DH", MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    SummaryRow("مبلغ الادخار:", "${savings.toInt()} DH", EmeraldGreen)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    SummaryRow("الميزانية المتغيرة المتاحة:", "${available.toInt()} DH", SkyBlue)
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "من اليوم M3AK غادي يحسب ليك شحال تقدر تصرف كل نهار بأمان. 🚀",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1) {
                    Button(
                        onClick = { step-- },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("السابق")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Button(
                    onClick = {
                        if (step < 6) {
                            step++
                        } else {
                            val updatedProfile = initialProfile.copy(
                                name = userName.ifBlank { "Yassine" },
                                monthlySalary = income,
                                salaryDay = salaryDay,
                                fixedExpensesTotal = totalFixed,
                                monthlySavingsTarget = savings,
                                isOnboardingCompleted = true
                            )
                            onCompleteOnboarding(updatedProfile)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = if (step == 1) "نبداو 🚀" else if (step == 6) "دخول بالتطبيق" else "التالي",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
