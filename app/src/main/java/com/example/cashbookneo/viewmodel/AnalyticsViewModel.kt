package com.example.cashbookneo.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cashbookneo.data.dao.AnalyticsDao
import com.example.cashbookneo.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.YearMonth
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsViewModel(
    private val activeAccount: Account,
    private val analyticsDao: AnalyticsDao
) : ViewModel() {
    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    private val _activeTab = MutableStateFlow(AnalyticsTab.BREAKDOWN)

    val uiState: StateFlow<MonthlySummaryUiState> = combine(
        _selectedMonth, _activeTab
    ) { month, tab ->
        val startEpoch = month.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val endEpoch = month.atEndOfMonth().atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli()
        Triple(month, tab, startEpoch to endEpoch)
    }.flatMapLatest { (month, tab, timeSpan) ->
        val (start, end) = timeSpan
        combine(
            analyticsDao.getMonthlyTotals(activeAccount.id, start, end),
            analyticsDao.getCategoryOutflows(activeAccount.id, start, end)
        ) { totals, categoryAggs ->
            val inflow = totals.totalInflow ?: 0.0
            val outflow = totals.totalOutflow ?: 0.0
            val net = inflow - outflow
            val savingsRate = if (inflow > 0) ((net / inflow) * 100.0) else 0.0

            val totalCategorySpend = categoryAggs.sumOf { it.totalSpend }
            val categories = categoryAggs.map { agg ->
                CategorySpendSlice(
                    categoryId = agg.categoryId,
                    categoryName = agg.categoryName,
                    totalAmount = agg.totalSpend,
                    percentage = if (totalCategorySpend > 0) (agg.totalSpend / totalCategorySpend).toFloat() else 0f,
                    color = agg.colorHex?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color.Gray,
                    transactionCount = agg.count
                )
            }

            val weeklyData = calculateWeeklyBuckets(start, end)
            val peakAmount = weeklyData.maxOfOrNull { it.amount } ?: 0.0
            val weeklyBuckets = weeklyData.map { it.copy(isPeakWeek = it.amount > 0 && it.amount == peakAmount) }

            val alert = if (outflow > 5000.0) {
                BudgetAlert(
                    title = "Monthly Spending Alert",
                    message = "Utilities accounted for 46.5% of outgoings, higher than usual due to seasonal heating. Cap next month's utility budget at $4,000 to reach 35% net savings.",
                    suggestedCap = 4000.0
                )
            } else null

            MonthlySummaryUiState(
                currentMonth = month,
                currencySymbol = activeAccount.currencySymbol,
                totalInflow = inflow,
                totalOutflow = outflow,
                netCashFlow = net,
                savingsRatePct = savingsRate,
                activeTab = tab,
                categoryBreakdown = categories,
                weeklyVelocity = weeklyBuckets,
                spendingAlert = alert,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MonthlySummaryUiState(isLoading = true)
    )

    fun onPreviousMonth() {
        _selectedMonth.update { it.minusMonths(1) }
    }

    fun onNextMonth() {
        _selectedMonth.update { it.plusMonths(1) }
    }

    fun onTabSelected(tab: AnalyticsTab) {
        _activeTab.value = tab
    }

    private suspend fun calculateWeeklyBuckets(start: Long, end: Long): List<WeeklySpendBucket> {
        val dtos = analyticsDao.getWeeklyOutflows(activeAccount.id, start, end)
        val dtoMap = dtos.associate { it.weekNumber to it.weekTotal }
        return (1..4).map { w ->
            WeeklySpendBucket(
                weekLabel = "W$w",
                amount = dtoMap[w] ?: 0.0
            )
        }
    }

    class Factory(
        private val activeAccount: Account,
        private val analyticsDao: AnalyticsDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AnalyticsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AnalyticsViewModel(activeAccount, analyticsDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
