package com.example.cashbookneo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cashbookneo.model.*
import com.example.cashbookneo.ui.components.*
import com.example.cashbookneo.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.math.abs

@Composable
fun DashboardScreen(
    activeAccount: Account,
    transactions: List<Transaction>,
    onNavigate: (String) -> Unit,
    onTransactionClick: (Transaction) -> Unit = {},
    onDeleteTransaction: (Transaction) -> Unit = {},
    onEditTransaction: (Transaction) -> Unit = {}
) {
    var selectedPeriod by remember { mutableStateOf("Monthly") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    val filteredTransactions = remember(selectedPeriod, selectedCategory, transactions) {
        val referenceDate = LocalDateTime.of(2026, 9, 5, 23, 59)
        val periodFiltered = when (selectedPeriod) {
            "Daily" -> transactions.filter { it.dateTime.toLocalDate() == referenceDate.toLocalDate() }
            "Weekly" -> transactions.filter { it.dateTime.isAfter(referenceDate.minusDays(7)) }
            "Monthly" -> transactions.filter { it.dateTime.month == referenceDate.month && it.dateTime.year == referenceDate.year }
            "Yearly" -> transactions.filter { it.dateTime.year == referenceDate.year }
            else -> transactions
        }
        
        if (selectedCategory != null) {
            periodFiltered.filter { it.category == selectedCategory }
        } else {
            periodFiltered
        }
    }

    val totalOutflow = remember(filteredTransactions) {
        filteredTransactions.filter { !it.isCredit }.sumOf { abs(it.amount) }
    }

    val categorySummaries = remember(transactions, totalOutflow, selectedPeriod) {
        val referenceDate = LocalDateTime.of(2026, 9, 5, 23, 59)
        val periodFiltered = when (selectedPeriod) {
            "Daily" -> transactions.filter { it.dateTime.toLocalDate() == referenceDate.toLocalDate() }
            "Weekly" -> transactions.filter { it.dateTime.isAfter(referenceDate.minusDays(7)) }
            "Monthly" -> transactions.filter { it.dateTime.month == referenceDate.month && it.dateTime.year == referenceDate.year }
            "Yearly" -> transactions.filter { it.dateTime.year == referenceDate.year }
            else -> transactions
        }
        
        val outflowOnly = periodFiltered.filter { !it.isCredit }
        val periodTotalOutflow = outflowOnly.sumOf { abs(it.amount) }
        
        outflowOnly
            .groupBy { it.category }
            .map { (category, txs) ->
                val amount = txs.sumOf { abs(it.amount) }
                CategoryLedgerSummary(
                    category = category,
                    totalAmount = -amount,
                    percentage = if (periodTotalOutflow > 0) (amount / periodTotalOutflow) * 100 else 0.0,
                    entriesCount = txs.size,
                    payees = txs.map { it.payee }.distinct(),
                    details = "",
                    date = ""
                )
            }.sortedByDescending { abs(it.totalAmount) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            AccountSelector(
                account = activeAccount,
                onAccountClick = { onNavigate("ManageAccounts") },
                onExportClick = { onNavigate("BackupRestore") },
                onCalendarClick = { /* Show Calendar Dialog */ }
            )
        }
        item {
            PeriodFilterRow(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = { 
                    selectedPeriod = it
                    selectedCategory = null // Reset category filter on period change
                }
            )
        }
        item {
            ExpenseBreakdownCard(
                totalOutflow = totalOutflow,
                summaries = categorySummaries,
                currencySymbol = activeAccount.currencySymbol,
                onCategoryClick = { 
                    selectedCategory = if (selectedCategory == it) null else it 
                }
            )
        }
        item {
            CashActionButtons(onNavigate = onNavigate)
        }
        item {
            SectionHeader(
                title = if (selectedCategory != null) "Entries: ${selectedCategory?.name}" else "Recent Entries",
                count = filteredTransactions.size,
                onSeeAllClick = { onNavigate("Search") }
            )
        }
        items(filteredTransactions, key = { it.id }) { transaction ->
            TransactionItem(
                transaction = transaction,
                currencySymbol = activeAccount.currencySymbol,
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = onTransactionClick,
                onDelete = onDeleteTransaction,
                onEdit = onEditTransaction
            )
        }
        item {
            BudgetInsightCard(insight = DummyInsight)
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    count: Int,
    modifier: Modifier = Modifier,
    onSeeAllClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = title, style = Typography.headlineSmall, color = MidnightOnSurface)
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = CircleShape,
                color = MidnightSurfaceContainerHigh,
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = count.toString(), style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                }
            }
        }
        TextButton(onClick = onSeeAllClick) {
            Text(text = "See All >", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
        }
    }
}
