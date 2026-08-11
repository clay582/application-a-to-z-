package com.example.util

object LanguageManager {

    fun getTranslation(key: String, lang: String): String {
        val l = lang.lowercase()
        return when (key) {
            "tagline" -> when (l) {
                "fr" -> "Gérez votre argent en toute sérénité"
                "en" -> "Keep your money under control"
                else -> "M3AK — فلوسك تحت السيطرة"
            }
            "question_daily" -> when (l) {
                "fr" -> "Combien puis-je dépenser aujourd'hui ?"
                "en" -> "How much can I spend today?"
                else -> "شحال نقدر نصرف اليوم؟"
            }
            "budget_today_title" -> when (l) {
                "fr" -> "Budget Aujourd'hui"
                "en" -> "Today's Budget"
                else -> "ميزانيتك اليوم"
            }
            "safe_spending" -> when (l) {
                "fr" -> "Dépense sécurisée"
                "en" -> "Safe daily spending"
                else -> "تقدر تصرف بأمان"
            }
            "available_cycle" -> when (l) {
                "fr" -> "Disponible ce cycle"
                "en" -> "Available this cycle"
                else -> "المتاح هذه الدورة"
            }
            "days_left" -> when (l) {
                "fr" -> "jours restants"
                "en" -> "days left"
                else -> "أيام متبقية"
            }
            "income" -> when (l) {
                "fr" -> "Revenu"
                "en" -> "Income"
                else -> "الدخل"
            }
            "spent" -> when (l) {
                "fr" -> "Dépensé"
                "en" -> "Spent"
                else -> "المصاريف"
            }
            "saved" -> when (l) {
                "fr" -> "Épargné"
                "en" -> "Saved"
                else -> "الادخار"
            }
            "nav_home" -> when (l) {
                "fr" -> "Accueil"
                "en" -> "Home"
                else -> "الرئيسية"
            }
            "nav_expenses" -> when (l) {
                "fr" -> "Dépenses"
                "en" -> "Expenses"
                else -> "المصاريف"
            }
            "nav_goals" -> when (l) {
                "fr" -> "Objectifs"
                "en" -> "Goals"
                else -> "الأهداف"
            }
            "nav_ai" -> when (l) {
                "fr" -> "M3AK AI"
                "en" -> "M3AK AI"
                else -> "M3AK AI"
            }
            "nav_profile" -> when (l) {
                "fr" -> "Compte"
                "en" -> "Account"
                else -> "الحساب"
            }
            "nav_analytics" -> when (l) {
                "fr" -> "Analyses"
                "en" -> "Analytics"
                else -> "التحليلات"
            }
            "add_expense" -> when (l) {
                "fr" -> "Ajouter une dépense"
                "en" -> "Add Expense"
                else -> "إضافة مصروف"
            }
            "natural_language_hint" -> when (l) {
                "fr" -> "Ex: 'J'ai payé 50 DH pour un café'"
                "en" -> "Ex: 'Spent 50 DH on coffee'"
                else -> "مثال: 'خلصت 50 درهم فالقهوة'"
            }
            "over_budget_warning" -> when (l) {
                "fr" -> "⚠️ Dépassement de votre budget aujourd'hui"
                "en" -> "⚠️ Over today's budget"
                else -> "⚠️ صرفتي أكثر من budget اليوم"
            }
            "over_budget_sub" -> when (l) {
                "fr" -> "Essayez de réduire vos dépenses demain."
                "en" -> "Try to spend less tomorrow."
                else -> "حاول تخفف المصاريف غداً."
            }
            "money_health_title" -> when (l) {
                "fr" -> "Score Santé Financière"
                "en" -> "Money Health Score"
                else -> "صحة فلوسك (Score)"
            }
            "confirm" -> when (l) {
                "fr" -> "Confirmer"
                "en" -> "Confirm"
                else -> "تأكيد"
            }
            "edit" -> when (l) {
                "fr" -> "Modifier"
                "en" -> "Edit"
                else -> "تعديل"
            }
            "save" -> when (l) {
                "fr" -> "Enregistrer"
                "en" -> "Save"
                else -> "حفظ"
            }
            else -> key
        }
    }
}
