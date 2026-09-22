package com.example.cashbookneo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashbookneo.model.*
import com.example.cashbookneo.ui.theme.*
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

private val transactionDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy · HH:mm", Locale.US)

@Composable
fun AccountSelector(
    account: Account,
    modifier: Modifier = Modifier,
    onAccountClick: () -> Unit = {},
    onExportClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MidnightSurfaceContainer)
                .clickable { onAccountClick() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MidnightSurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.AccountBalanceWallet,
                    contentDescription = null,
                    tint = MidnightPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = account.name,
                        style = Typography.labelLarge,
                        color = MidnightOnSurface
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MidnightOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "${account.ledgerType} · ${account.currency}",
                    style = Typography.labelSmall,
                    color = CashInGreen
                )
            }
        }

        Row {
            IconButton(
                onClick = onExportClick,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MidnightSurfaceContainer)
            ) {
                Icon(Icons.Rounded.FileDownload, contentDescription = null, tint = MidnightOnSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onCalendarClick,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MidnightSurfaceContainer)
            ) {
                Icon(Icons.Rounded.CalendarToday, contentDescription = null, tint = MidnightOnSurfaceVariant)
            }
        }
    }
}

@Composable
fun PeriodFilterRow(
    selectedPeriod: String = "Monthly",
    onPeriodSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val filters = listOf("All", "Daily", "Weekly", "Monthly", "Yearly", "Custom")
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        filters.forEach { filter ->
            val isSelected = filter == selectedPeriod
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(if (isSelected) MidnightPrimary.copy(alpha = 0.8f) else MidnightSurfaceContainer)
                    .clickable { onPeriodSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = filter,
                        style = Typography.labelMedium,
                        color = if (isSelected) MidnightOnPrimary else MidnightOnSurfaceVariant
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MidnightOnPrimary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseBreakdownCard(
    totalOutflow: Double,
    summaries: List<CategoryLedgerSummary>,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    onCategoryClick: (Category) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MidnightPrimary))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EXPENSE BREAKDOWN",
                        style = Typography.labelSmall,
                        color = MidnightOnSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MidnightSurfaceContainerHigh)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "Sep 2026", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.Center
            ) {
                RingChart(
                    data = summaries.map { 
                        RingChartData(it.percentage.toFloat(), it.category.color, it.category) 
                    }.ifEmpty { 
                        listOf(RingChartData(100f, MidnightSurfaceContainerHigh)) 
                    },
                    modifier = Modifier.size(200.dp),
                    thickness = 24.dp,
                    onSliceClick = { data -> (data.tag as? Category)?.let { onCategoryClick(it) } }
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TOTAL OUTFLOW",
                        style = Typography.labelSmall,
                        color = MidnightOnSurfaceVariant
                    )
                    Text(
                        text = String.format(Locale.US, "%s%,.2f", currencySymbol, totalOutflow),
                        style = CurrencyDisplay,
                        color = MidnightOnSurface
                    )
                }
            }

            if (summaries.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    summaries.take(2).forEach { summary ->
                        BreakdownLegend(
                            color = summary.category.color,
                            label = summary.category.name,
                            percentage = String.format(Locale.US, "%.1f%%", summary.percentage),
                            amount = String.format(Locale.US, "%s%,.2f", currencySymbol, abs(summary.totalAmount)),
                            modifier = Modifier.weight(1f),
                            onClick = { onCategoryClick(summary.category) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BreakdownLegend(color: Color, label: String, percentage: String, amount: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MidnightSurfaceContainer)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = label, style = Typography.labelMedium, color = MidnightOnSurface)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = percentage, style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
            }
            Text(text = amount, style = CurrencyLedger, color = MidnightOnSurface)
        }
    }
}

@Composable
fun CashActionButtons(modifier: Modifier = Modifier, onNavigate: (String) -> Unit = {}) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { onNavigate("RecordEntryCredit") },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(9999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CashInGreen)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = CashInOnGreen)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Cash In", style = Typography.labelLarge, color = CashInOnGreen)
        }
        Button(
            onClick = { onNavigate("RecordEntryDebit") },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(9999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CashOutRose)
        ) {
            Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Cash Out", style = Typography.labelLarge, color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItem(
    transaction: Transaction,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    onClick: (Transaction) -> Unit = {},
    onDelete: (Transaction) -> Unit = {},
    onEdit: (Transaction) -> Unit = {}
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete(transaction)
                    false
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    onEdit(transaction)
                    false
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> Color(0xFF10B981)
                SwipeToDismissBoxValue.EndToStart -> Color(0xFFF43F5E)
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                val icon = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Icons.Rounded.Edit else Icons.Rounded.Delete
                Icon(icon, null, tint = Color.White)
            }
        },
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Surface(
            onClick = { onClick(transaction) },
            shape = RoundedCornerShape(16.dp),
            color = MidnightSurfaceContainerLow
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MidnightSurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (transaction.category.id) {
                        "food" -> Icons.Rounded.ShoppingCart
                        "utilities" -> Icons.Rounded.Bolt
                        else -> Icons.Rounded.Receipt
                    }
                    Icon(icon, contentDescription = null, tint = transaction.category.color, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = transaction.payee, style = Typography.bodyLarge, color = MidnightOnSurface)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(transaction.category.color.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = transaction.category.name,
                                style = Typography.labelSmall,
                                color = transaction.category.color
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = transaction.dateTime.format(transactionDateFormatter),
                            style = Typography.bodySmall,
                            color = MidnightOnSurfaceVariant
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format(Locale.US, "%s %s%,.2f", if (transaction.isCredit) "+" else "-", currencySymbol, abs(transaction.amount)),
                        style = CurrencyLedger,
                        color = if (transaction.isCredit) CashInGreen else MidnightOnSurface
                    )
                    Text(text = transaction.paymentMethod, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun BudgetInsightCard(insight: ExpenseInsight, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MidnightSurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = MidnightPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = insight.title, style = Typography.labelLarge, color = MidnightOnSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = insight.description, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
            }
        }
    }
}

data class NavItem(val id: String, val label: String, val icon: ImageVector)

@Composable
fun DashboardBottomNavigation(
    currentScreen: String = "home",
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MidnightSurfaceContainerLow,
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            NavItem("home", "Home", Icons.Rounded.ReceiptLong),
            NavItem("analytics", "Analytics", Icons.Rounded.QueryStats),
            NavItem("savings", "Savings", Icons.Rounded.Savings),
            NavItem("settings", "Settings", Icons.Rounded.Settings)
        )
        
        items.forEach { item ->
            val selected = item.id == currentScreen
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.id) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(text = item.label, style = Typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MidnightPrimary,
                    selectedTextColor = MidnightPrimary,
                    unselectedIconColor = MidnightOnSurfaceVariant,
                    unselectedTextColor = MidnightOnSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
