package com.example.util

import com.example.BuildConfig
import com.example.model.BudgetCalculation
import com.example.model.ExpenseCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ParsedExpense(
    val amount: Double,
    val category: ExpenseCategory,
    val description: String
)

object GeminiService {

    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun parseExpenseFromText(promptText: String): ParsedExpense? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Fallback parsing locally if key is not configured yet
            return@withContext fallbackLocalParse(promptText)
        }

        val systemInstruction = """
            You are a Moroccan financial assistant parser.
            Extract the amount (in DH/MAD), expense category, and a short description from text in Moroccan Darija, French, or English.
            Categories must be one of: FOOD, TRANSPORT, SHOPPING, BILLS, ENTERTAINMENT, HOUSING, SAVINGS, EDUCATION, TRAVEL, OTHER.
            Return ONLY a raw JSON object with keys: "amount" (number), "category" (string), "description" (string). Do not add markdown backticks.
        """.trimIndent()

        val jsonPayload = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
            })
            put("contents", JSONArray().put(JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", promptText)))
            }))
        }

        try {
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful && responseBody.isNotEmpty()) {
                val jsonResp = JSONObject(responseBody)
                val candidates = jsonResp.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        var text = parts.getJSONObject(0).optString("text", "")
                        text = text.replace("```json", "").replace("```", "").trim()
                        val parsed = JSONObject(text)
                        val amount = parsed.optDouble("amount", 0.0)
                        val catStr = parsed.optString("category", "OTHER")
                        val desc = parsed.optString("description", promptText)

                        return@withContext ParsedExpense(
                            amount = amount,
                            category = ExpenseCategory.fromId(catStr),
                            description = desc
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext fallbackLocalParse(promptText)
    }

    private fun fallbackLocalParse(promptText: String): ParsedExpense {
        val numbers = Regex("(\\d+(\\.\\d+)?)").findAll(promptText).mapNotNull { it.value.toDoubleOrNull() }.toList()
        val amount = numbers.firstOrNull() ?: 20.0
        val category = ExpenseCategory.parseFromString(promptText)
        return ParsedExpense(
            amount = amount,
            category = category,
            description = promptText.ifBlank { category.arabicName }
        )
    }

    suspend fun getAiCoachResponse(
        userMessage: String,
        budget: BudgetCalculation,
        userName: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineCoachReply(userMessage, budget)
        }

        val systemContext = """
            You are M3AK AI, a friendly, encouraging Moroccan personal financial coach.
            Reply in warm, concise Moroccan Darija (mixed with French/English if helpful).
            Current User Financial Context:
            - Name: $userName
            - Monthly Income: ${budget.monthlyIncome} DH
            - Fixed Expenses: ${budget.fixedExpensesTotal} DH
            - Monthly Savings Target: ${budget.savingsTarget} DH
            - Remaining Budget for this cycle: ${budget.remainingCycleBudget} DH
            - Days left in cycle: ${budget.daysRemainingInCycle}
            - Today's Safe Daily Spend: ${budget.safeDailySpend} DH
            - Spent Today so far: ${budget.spentToday} DH
            - Money Health Score: ${budget.moneyHealthScore} / 100

            RULES:
            1. Never invent fake data or misquote their numbers. Always base advice on their real figures above.
            2. Be friendly, practical, and clear. Emphasize their daily safe spending amount.
            3. Keep answers concise (under 4 sentences) and easy to read.
        """.trimIndent()

        val jsonPayload = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", systemContext)))
            })
            put("contents", JSONArray().put(JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
            }))
        }

        try {
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful && responseBody.isNotEmpty()) {
                val jsonResp = JSONObject(responseBody)
                val candidates = jsonResp.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val reply = parts.getJSONObject(0).optString("text", "")
                        if (reply.isNotBlank()) return@withContext reply.trim()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext getOfflineCoachReply(userMessage, budget)
    }

    private fun getOfflineCoachReply(msg: String, budget: BudgetCalculation): String {
        val lower = msg.lowercase()
        return when {
            lower.contains("تلفون") || lower.contains("شراء") || lower.contains("نشري") || lower.contains("acheter") -> {
                "حالياً عندك ${budget.remainingCycleBudget.toInt()} DH متاحة فهاد الدورة. إذا شريتي هاد المنتج، تقدر تفوت budget ديالك وتأثر على مصاريف الأيام المتبقية (${budget.daysRemainingInCycle} يوم)."
            }
            lower.contains("اليوم") || lower.contains("شحال") || lower.contains("today") -> {
                "اليوم تقدر تصرف بأمان حتى لـ ${budget.safeDailySpend.toInt()} DH. صرفتي منها لحد الآن ${budget.spentToday.toInt()} DH."
            }
            lower.contains("وفر") || lower.contains("توفير") || lower.contains("ادخار") -> {
                "هدف الادخار ديالك هو ${budget.savingsTarget.toInt()} DH فالشهر. تبارك الله عليك غادين فالمسار الصحيح! 👏"
            }
            else -> {
                "أنا M3AK 👋 معك باش نحافظو على الاستقرار المالي ديالك. ميزانيتك المتبقية هاد الشهر هي ${budget.remainingCycleBudget.toInt()} DH، وبمعدل آمن ديال ${budget.safeDailySpend.toInt()} DH كل نهار."
            }
        }
    }
}
