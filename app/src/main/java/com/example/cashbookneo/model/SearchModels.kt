package com.example.cashbookneo.model

import java.time.LocalDate

enum class PaymentChannel { ALL, CASH, CARD, BANK_WIRE }
enum class FlowType { ALL, INFLOW_ONLY, OUTFLOW_ONLY }
enum class DateHorizonPreset { THIS_MONTH, LAST_30_DAYS, CUSTOM_SPAN }

data class SearchFilterState(
    val searchQuery: String = "",
    val selectedLedgerId: String? = null, // null = "All Ledgers"
    val flowType: FlowType = FlowType.ALL,
    val datePreset: DateHorizonPreset = DateHorizonPreset.LAST_30_DAYS,
    val customStartDate: LocalDate? = LocalDate.now().minusDays(30),
    val customEndDate: LocalDate? = LocalDate.now(),
    val minAmount: Double = 0.0,
    val maxAmount: Double = 10000.0,
    val paymentChannel: PaymentChannel = PaymentChannel.ALL,
    val onlyWithReceipt: Boolean = false,
    val isMatrixCollapsed: Boolean = false
)

data class TransactionSearchResult(
    val id: String,
    val title: String,
    val ledgerName: String,
    val amount: Double,
    val flowType: FlowType,
    val currency: String,
    val categoryName: String,
    val channel: PaymentChannel,
    val timestamp: Long,
    val hasReceipt: Boolean,
    val isAudited: Boolean,
    val iconName: String
)
