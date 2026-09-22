package com.example.cashbookneo.model

import androidx.compose.ui.graphics.Color
import java.time.YearMonth

data class MonthlySummaryUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val currencySymbol: String = "$",
    val totalInflow: Double = 0.0,
    val totalOutflow: Double = 0.0,
    val netCashFlow: Double = 0.0,
    val savingsRatePct: Double = 0.0,
    val activeTab: AnalyticsTab = AnalyticsTab.BREAKDOWN,
    val categoryBreakdown: List<CategorySpendSlice> = emptyList(),
    val weeklyVelocity: List<WeeklySpendBucket> = emptyList(),
    val spendingAlert: BudgetAlert? = null,
    val isLoading: Boolean = false
)

enum class AnalyticsTab {
    BREAKDOWN, CASH_FLOW, COMPARE
}

data class CategorySpendSlice(
    val categoryId: String,
    val categoryName: String,
    val totalAmount: Double,
    val percentage: Float, // 0.0f .. 1.0f
    val color: Color,
    val transactionCount: Int,
    val sampleNotes: List<String> = emptyList()
)

data class WeeklySpendBucket(
    val weekLabel: String, // "W1", "W2", "W3", "W4"
    val amount: Double,
    val isPeakWeek: Boolean = false
)

data class BudgetAlert(
    val title: String,
    val message: String,
    val suggestedCap: Double
)
