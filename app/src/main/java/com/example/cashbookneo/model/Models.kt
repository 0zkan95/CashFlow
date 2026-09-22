package com.example.cashbookneo.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.cashbookneo.ui.theme.CashInGreen
import com.example.cashbookneo.ui.theme.CashOutRose
import java.time.LocalDateTime

data class Account(
    val id: String,
    val name: String,
    val ledgerType: String,
    val currency: String,
    val balance: Double = 0.0,
    val email: String = ""
) {
    val currencySymbol: String
        get() = when (currency) {
            "EUR" -> "€"
            "RSD" -> "ДИН"
            "USD" -> "$"
            else -> "$"
        }
}

data class Category(
    val id: String,
    val name: String,
    val color: Color,
    val iconRes: Int
)

data class Transaction(
    val id: String,
    val accountId: String,
    val payee: String,
    val category: Category,
    val dateTime: LocalDateTime,
    val amount: Double,
    val paymentMethod: String,
    val isCredit: Boolean,
    val status: TransactionStatus? = null,
    val attachments: Int = 0,
    val memo: String? = null,
    val detail: TransactionDetail? = null
)

enum class TransactionStatus {
    AUDITED, AUTO_CLEARED, PENDING_RETURN, SETTLED
}

data class TransactionDetail(
    val txid: String,
    val subtitle: String,
    val assignedLedger: String,
    val settlementStatus: String,
    val items: List<ReceiptLineItem>,
    val subtotal: Double,
    val tax: Double,
    val discounts: Double,
    val proofImageUrl: String? = null,
    val sha256: String,
    val auditTrail: List<AuditEvent>
)

data class ReceiptLineItem(
    val label: String,
    val category: String,
    val amount: Double
)

data class AuditEvent(
    val dateTime: LocalDateTime,
    val title: String,
    val description: String,
    val color: Color
)

data class ExpenseInsight(
    val title: String,
    val description: String,
    val hasActions: Boolean = false
)

data class WeeklyVelocity(
    val label: String,
    val amount: Double
)

data class CategoryLedgerSummary(
    val category: Category,
    val totalAmount: Double,
    val percentage: Double,
    val entriesCount: Int,
    val payees: List<String>,
    val details: String,
    val date: String
)

data class AnalyticsSummary(
    val netCashFlow: Double,
    val savingsPercentage: Double,
    val incomePercentage: Double,
    val outflowPercentage: Double,
    val totalInflow: Double,
    val inflowGrowth: Double,
    val totalOutflow: Double,
    val outflowCategoriesCount: Int
)

data class SideNavItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val trailingText: String? = null,
    val isGlow: Boolean = false,
    val statusColor: Color? = null
)

data class SideNavGroup(
    val title: String,
    val items: List<SideNavItem>
)

data class AssetDistribution(
    val label: String,
    val percentage: Float,
    val color: Color
)

data class SavingsVault(
    val id: String,
    val title: String,
    val type: String,
    val asset: String,
    val date: String,
    val amount: String,
    val amountUsd: String? = null,
    val growth: String? = null,
    val goal: String,
    val targetAmount: Double? = null,
    val progress: Float,
    val statusText: String,
    val isCompleted: Boolean = false,
    val isArchived: Boolean = false,
    val iconName: String = "vault",
    val categoryColor: Color
)

data class OnboardingData(
    val fullName: String,
    val email: String,
    val vaultLabel: String,
    val securityMode: String,
    val pin: String,
    val autoLockTimeout: String,
    val baseCurrency: String,
    val customCategories: List<Category>,
    val template: String,
    val startingBalance: Double
)

// Notes & Memos Models
sealed class FinancialMemo {
    abstract val id: String
    abstract val accountId: String
    abstract val title: String
    abstract val date: String
}

data class IOUData(
    override val id: String,
    override val accountId: String,
    override val title: String,
    override val date: String,
    val amount: String,
    val type: String, // e.g., "Receivable"
    val status: String,
    val autoDeduct: Boolean
) : FinancialMemo()

data class ChecklistMemo(
    override val id: String,
    override val accountId: String,
    override val title: String,
    override val date: String,
    val category: String,
    val items: List<ChecklistItem>,
    val totalEst: String
) : FinancialMemo()

data class ChecklistItem(
    val label: String,
    val amount: String,
    val isChecked: Boolean
)

data class ReceiptMemo(
    override val id: String,
    override val accountId: String,
    override val title: String,
    override val date: String,
    val category: String,
    val status: String
) : FinancialMemo()

data class SavingsWatchMemo(
    override val id: String,
    override val accountId: String,
    override val title: String,
    override val date: String,
    val category: String,
    val description: String,
    val target: String,
    val status: String
) : FinancialMemo()

val DummyUser = Account("acc1", "Hidayet Aslan", "Primary Ledger", "RSD", 10760.40, "hidayet.a2315@vault.io")
val DummyUser2 = Account("acc2", "Business Account", "Corporate Ledger", "EUR", 25000.00, "business@vault.io")

val CategoryFood = Category("food", "Food", Color(0xFF8B5CF6), 0)
val CategoryUtilities = Category("utilities", "Utilities", Color(0xFF10B981), 0)
val CategoryFreelance = Category("freelance", "Freelance", Color(0xFF10B981), 0)
val CategoryShopping = Category("shopping", "Shopping", Color(0xFF8B5CF6), 0)

val DummyDetailLidl = TransactionDetail(
    txid = "#TX-892410-LIDL",
    subtitle = "Groceries & Daily Essentials",
    assignedLedger = "Primary (Hidayet Aslan)",
    settlementStatus = "Cleared via Cash / Debit",
    items = listOf(
        ReceiptLineItem("Greek Yogurt 1kg", "Food & Dining", 4.20),
        ReceiptLineItem("Extra Virgin Olive Oil 750ml", "Food & Dining", 12.30),
        ReceiptLineItem("Fresh Sourdough Bread (hleb)", "Food & Dining", 2.10),
        ReceiptLineItem("Organic Chicken Breast 800g", "Food & Dining", 18.40),
        ReceiptLineItem("Sparkling Mineral Water (6x1.5L)", "Beverages", 6.00),
        ReceiptLineItem("Household Detergent", "Household", 13.00)
    ),
    subtotal = 56.00,
    tax = 9.33,
    discounts = 0.00,
    sha256 = "8f4b009e2cf31a77d...3a19",
    auditTrail = listOf(
        AuditEvent(LocalDateTime.of(2026, 9, 5, 20, 6), "Transaction Created", "Manual quick-entry recorded by Hidayet via mobile ledger keypad.", Color(0xFF818CF8)),
        AuditEvent(LocalDateTime.of(2026, 9, 5, 20, 8), "Receipt Attached & OCR Parsed", "Thermal photo processed on-device. Neural engine matched 6 line items totaling $56.00.", Color(0xFF10B981)),
        AuditEvent(LocalDateTime.of(2026, 9, 6, 9, 15), "Category Tagged", "Assigned to 'Food & Dining' based on recurring merchant classification heuristics.", Color(0xFF94A3B8)),
        AuditEvent(LocalDateTime.of(2026, 9, 7, 2, 20), "Local Vault Snapshot Committed", "Transaction ledger state serialized into local encrypted SQLite block #412.", Color(0xFF10B981))
    )
)

val DummyTransactions = listOf(
    Transaction("1", "acc1", "Lidl Supermarket", CategoryFood, LocalDateTime.of(2026, 9, 5, 20, 6), -5600.00, "Debit card", false, detail = DummyDetailLidl),
    Transaction("2", "acc1", "Public Power Corp.", CategoryUtilities, LocalDateTime.of(2026, 9, 1, 10, 30), -12000.00, "Bank transfer", false),
    Transaction("3", "acc1", "Internet Subscription", CategoryUtilities, LocalDateTime.of(2026, 8, 28, 16, 20), -2500.00, "Autopay", false),
    Transaction("4", "acc1", "hleb (Artisan Bakery)", CategoryFood, LocalDateTime.of(2026, 8, 24, 8, 15), -160.00, "Cash", false)
)

val DummySearchResults = listOf(
    Transaction(
        "s1", "acc1", "Lidl Supermarket - Groceries", CategoryFood, 
        LocalDateTime.of(2026, 9, 5, 14, 30), -56.00, "Cash", false, 
        status = null, attachments = 0, detail = DummyDetailLidl
    ),
    Transaction(
        "s2", "acc1", "Payment from Marko", CategoryFreelance, 
        LocalDateTime.of(2026, 9, 18, 10, 0), 250.00, "Bank Transfer", true, 
        status = TransactionStatus.PENDING_RETURN, attachments = 1, memo = "Memo attached"
    ),
    Transaction(
        "s3", "acc1", "Water & Utility Bill", CategoryUtilities, 
        LocalDateTime.of(2026, 9, 5, 9, 12), -25.00, "Bank Wire", false, 
        status = TransactionStatus.SETTLED, attachments = 0
    ),
    Transaction(
        "s4", "acc1", "Freelance Tech Equipment", CategoryFreelance, 
        LocalDateTime.of(2026, 9, 4, 17, 30), -418.00, "Card · 4120", false, 
        status = null, attachments = 2, memo = "Expense Claimed"
    )
)

val DummyMemos = listOf(
    IOUData("m1", "acc1", "Lent to Marko (Car Repair Share)", "Sep 18, 2026", "€250.00", "Receivable", "Pending repayment", true),
    ChecklistMemo("m2", "acc1", "Lidl & Supermarket Restock", "Bi-weekly", "Pantry", listOf(
        ChecklistItem("Fruits and Vegetables", "~$22.00", true),
        ChecklistItem("Dairy and Eggs", "~$10.00", true),
        ChecklistItem("Household Cleaning Supplies", "~$15.00", false)
    ), "$57.10"),
    ReceiptMemo("m3", "acc1", "Q3 Freelance Tech Equipment Receipts", "Yesterday, 18:20", "Tax & Invoices", "In Folder"),
    SavingsWatchMemo("m4", "acc1", "Flagship Phone Black Friday Deal", "Nov 12", "Savings Watch", "Compare prices between Tehnomanija and Gigatron when promo starts.", "RSD 20,000", "In Budget")
)

val DummyInsight = ExpenseInsight(
    "Budget Health Insight",
    "Utilities take 46.5% of your monthly expenditure. Seasonal heating cycle anticipated next week."
)

val DummyAnalyticsSummary = AnalyticsSummary(
    netCashFlow = 3740.00,
    savingsPercentage = 25.8,
    incomePercentage = 74.2,
    outflowPercentage = 25.8,
    totalInflow = 14500.00,
    inflowGrowth = 12.4,
    totalOutflow = 10760.00,
    outflowCategoriesCount = 2
)

val DummyWeeklyVelocity = listOf(
    WeeklyVelocity("W1", 1200.0),
    WeeklyVelocity("W2", 2800.0),
    WeeklyVelocity("W3", 1500.0),
    WeeklyVelocity("W4", 900.0)
)

val DummyLedgerSummaries = listOf(
    CategoryLedgerSummary(
        category = CategoryFood,
        totalAmount = -5760.00,
        percentage = 53.5,
        entriesCount = 2,
        payees = listOf("Lidl", "Local Market"),
        details = "Lidl Hypermarket ($5,600.00) · Bakery ($160.00)",
        date = "Sep 12"
    ),
    CategoryLedgerSummary(
        category = CategoryUtilities,
        totalAmount = -5000.00,
        percentage = 46.5,
        entriesCount = 1,
        payees = listOf("Public Power Corp"),
        details = "Main Grid Invoice ($5,000.00)",
        date = "Sep 04"
    )
)

val DummyAssetDistributions = listOf(
    AssetDistribution("Commodities", 42f, Color(0xFFF59E0B)),
    AssetDistribution("EUR", 38f, Color(0xFF8B5CF6)),
    AssetDistribution("RSD", 20f, Color(0xFF10B981))
)

val DummyVaults = listOf(
    SavingsVault(
        "1", "House Land Reserve", "Commodity", "Gold", "Sat, 05 Sep", 
        "5.0 Grams", "~$435.50 USD", null, "Goal: 20.0 Grams", 20.0, 0.25f, "25% Reached", false, false, "home", Color(0xFFF59E0B)
    ),
    SavingsVault(
        "2", "Summer Travel Europe", "Fiat", "EUR", "Sun, 06 Sep", 
        "€3,200.00", null, "+€150.00 this week", "Goal: €4,500.00", 4500.0, 0.71f, "71% Reached", false, false, "flight", Color(0xFF8B5CF6)
    ),
    SavingsVault(
        "3", "New Flagship Phone", "Fiat", "RSD", "Sun, 06 Sep", 
        "RSD 20,000.00", null, "Ready to spend", "Goal: RSD 20,000.00", 20000.0, 1.0f, "100% Completed", true, false, "smartphone", Color(0xFF10B981)
    ),
    SavingsVault(
        "4", "Mangal / Social BBQ", "Fiat", "RSD", "Recurring Fund", 
        "RSD 1,500.00", null, "Sep 12 (Upcoming)", "Goal: RSD 2,500.00", 2500.0, 0.60f, "60% Reached", false, false, "restaurant", Color(0xFFF43F5E)
    )
)
