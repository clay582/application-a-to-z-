package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ExpenseCategory(
    val id: String,
    val arabicName: String,
    val frenchName: String,
    val englishName: String,
    val emoji: String,
    val icon: ImageVector,
    val colorHex: Long
) {
    FOOD("food", "أكل ومشروبات", "Alimentation", "Food & Dining", "🍔", Icons.Default.Restaurant, 0xFFF59E0B),
    TRANSPORT("transport", "النقل والتنقل", "Transport", "Transportation", "🚕", Icons.Default.DirectionsCar, 0xFF0EA5E9),
    SHOPPING("shopping", "التسوق", "Shopping", "Shopping", "🛍️", Icons.Default.ShoppingBag, 0xFFEC4899),
    BILLS("bills", "الفواتير والاشتراكات", "Factures", "Bills & Utilities", "📄", Icons.Default.Receipt, 0xFF8B5CF6),
    ENTERTAINMENT("entertainment", "الترفيه والرياضة", "Loisirs", "Entertainment", "🎮", Icons.Default.SportsEsports, 0xFF6366F1),
    HOUSING("housing", "السكن والكراء", "Logement", "Housing & Rent", "🏠", Icons.Default.Home, 0xFF10B981),
    SAVINGS("savings", "الادخار والاستثمار", "Épargne", "Savings & Invest", "💰", Icons.Default.Savings, 0xFF22C55E),
    EDUCATION("education", "الدراسة والتعليم", "Éducation", "Education", "🎓", Icons.Default.School, 0xFF3B82F6),
    TRAVEL("travel", "السفر والعطلات", "Voyage", "Travel", "✈️", Icons.Default.Flight, 0xFF06B6D4),
    OTHER("other", "مصاريف أخرى", "Autres", "Other", "📦", Icons.Default.MoreHoriz, 0xFF64748B);

    fun nameForLang(lang: String): String = when (lang.lowercase()) {
        "fr" -> frenchName
        "en" -> englishName
        else -> arabicName
    }

    companion object {
        fun fromId(id: String): ExpenseCategory {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: OTHER
        }

        fun parseFromString(text: String): ExpenseCategory {
            val lower = text.lowercase()
            return when {
                lower.contains("أكل") || lower.contains("قهوة") || lower.contains("غداء") || lower.contains("عشاء") || lower.contains("food") || lower.contains("resto") || lower.contains("café") -> FOOD
                lower.contains("طاكسي") || lower.contains("نقل") || lower.contains("مازوط") || lower.contains("essence") || lower.contains("taxi") || lower.contains("transport") -> TRANSPORT
                lower.contains("شراء") || lower.contains("حوايج") || lower.contains("shopping") || lower.contains("vêtement") -> SHOPPING
                lower.contains("ضو") || lower.contains("ماء") || lower.contains("إنترنت") || lower.contains("فاتورة") || lower.contains("bill") || lower.contains("recharge") || lower.contains("تعبئة") -> BILLS
                lower.contains("لعب") || lower.contains("سينما") || lower.contains("game") -> ENTERTAINMENT
                lower.contains("كراء") || lower.contains("دار") || lower.contains("rent") -> HOUSING
                lower.contains("سفر") || lower.contains("عطلة") || lower.contains("travel") -> TRAVEL
                else -> OTHER
            }
        }
    }
}
