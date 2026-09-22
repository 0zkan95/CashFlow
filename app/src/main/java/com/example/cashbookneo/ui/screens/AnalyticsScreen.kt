package com.example.cashbookneo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cashbookneo.model.*
import com.example.cashbookneo.ui.components.DonutChart
import com.example.cashbookneo.ui.components.WeeklyVelocityBarChart
import com.example.cashbookneo.ui.theme.*
import com.example.cashbookneo.viewmodel.*
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onSeeAllRecordsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val cardBg = MidnightSurfaceContainerLow
    val cardBorder = Color.White.copy(alpha = 0.08f)
    val emeraldAccent = CashInGreen
    val coralAccent = CashOutRose
    val indigoAccent = MidnightPrimary

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Month Picker
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardBg)
                        .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.onPreviousMonth() }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = MidnightOnSurfaceVariant)
                    }
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = indigoAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiState.currentMonth.format(DateTimeFormatter.ofPattern("MMM yyyy")),
                        color = Color.White,
                        style = Typography.labelLarge
                    )
                    IconButton(onClick = { viewModel.onNextMonth() }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = MidnightOnSurfaceVariant)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { /* Filter */ },
                        modifier = Modifier
                            .size(38.dp)
                            .background(cardBg, RoundedCornerShape(10.dp))
                            .border(1.dp, cardBorder, RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = "Filter", tint = MidnightOnSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = { /* Export */ },
                        modifier = Modifier
                            .size(38.dp)
                            .background(cardBg, RoundedCornerShape(10.dp))
                            .border(1.dp, cardBorder, RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = "Export", tint = MidnightOnSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // 2. Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Net Cash Flow", color = MidnightOnSurfaceVariant, style = Typography.bodySmall)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.size(6.dp).background(emeraldAccent, CircleShape))
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(emeraldAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("↑ ${String.format(Locale.US, "%.1f", uiState.savingsRatePct)}% saved", color = emeraldAccent, style = Typography.labelSmall)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${if (uiState.netCashFlow >= 0) "+" else "-"}${uiState.currencySymbol}${String.format(Locale.US, "%,.2f", abs(uiState.netCashFlow))}",
                        style = Typography.displayLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val totalVol = (uiState.totalInflow + uiState.totalOutflow).coerceAtLeast(1.0)
                    val inflowFraction = (uiState.totalInflow / totalVol).toFloat()
                    val outflowFraction = (uiState.totalOutflow / totalVol).toFloat()
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("● Income ${String.format(Locale.US, "%.1f", inflowFraction * 100)}%", color = emeraldAccent, style = Typography.labelSmall)
                        Text("Outflow ${String.format(Locale.US, "%.1f", outflowFraction * 100)}% ●", color = coralAccent, style = Typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    ) {
                        Box(modifier = Modifier.weight(inflowFraction.coerceAtLeast(0.01f)).fillMaxHeight().background(emeraldAccent))
                        Spacer(modifier = Modifier.width(2.dp))
                        Box(modifier = Modifier.weight(outflowFraction.coerceAtLeast(0.01f)).fillMaxHeight().background(coralAccent))
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MidnightBackground)
                                .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("TOTAL INFLOW", color = MidnightOnSurfaceVariant, style = Typography.labelSmall)
                                    Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = emeraldAccent, modifier = Modifier.size(14.dp))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("+${uiState.currencySymbol}${String.format(Locale.US, "%,.0f", uiState.totalInflow)}", color = emeraldAccent, style = Typography.headlineSmall)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MidnightBackground)
                                .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("TOTAL OUTFLOW", color = MidnightOnSurfaceVariant, style = Typography.labelSmall)
                                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = coralAccent, modifier = Modifier.size(14.dp))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("-${uiState.currencySymbol}${String.format(Locale.US, "%,.0f", uiState.totalOutflow)}", color = coralAccent, style = Typography.headlineSmall)
                            }
                        }
                    }
                }
            }
        }

        // 3. Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardBg)
                    .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                AnalyticsTab.entries.forEach { tab ->
                    val selected = uiState.activeTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) cardBorder else Color.Transparent)
                            .clickable { viewModel.onTabSelected(tab) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.name.replace("_", " ").lowercase(Locale.US).replaceFirstChar { it.uppercase() },
                            color = if (selected) Color.White else MidnightOnSurfaceVariant,
                            style = Typography.labelLarge
                        )
                    }
                }
            }
        }

        // 4. Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Category Distribution", color = Color.White, style = Typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DonutChart(
                            slices = uiState.categoryBreakdown,
                            totalLabel = "${uiState.currencySymbol}${String.format(Locale.US, "%.1fk", uiState.totalOutflow / 1000)}"
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            uiState.categoryBreakdown.take(4).forEach { slice ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).background(slice.color, CircleShape))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Text(slice.categoryName, color = Color.White, style = Typography.labelMedium)
                                            Text("${String.format(Locale.US, "%.1f", slice.percentage * 100)}%", color = MidnightOnSurfaceVariant, style = Typography.bodySmall)
                                        }
                                        Text("${uiState.currencySymbol}${String.format(Locale.US, "%,.2f", slice.totalAmount)}", color = Color.White, style = Typography.labelLarge)
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = cardBorder)
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    WeeklyVelocityBarChart(buckets = uiState.weeklyVelocity)
                }
            }
        }

        // 5. Alert
        uiState.spendingAlert?.let { alert ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF151C30)),
                    border = BorderStroke(1.dp, indigoAccent.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = indigoAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(alert.title, color = Color.White, style = Typography.labelLarge)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(alert.message, color = MidnightOnSurfaceVariant, style = Typography.bodySmall)
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Set ${uiState.currencySymbol}${alert.suggestedCap.toInt()} Cap", color = MidnightBackground, style = Typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        // 6. Ledger Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Outflow Ledger", color = Color.White, style = Typography.headlineSmall)
                Text(
                    text = "See All Records >",
                    color = MidnightOnSurfaceVariant,
                    style = Typography.labelSmall,
                    modifier = Modifier.clickable { onSeeAllRecordsClick() }
                )
            }
        }

        items(uiState.categoryBreakdown) { category ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(category.color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = category.color, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(category.categoryName, color = Color.White, style = Typography.labelLarge)
                            Text("${category.transactionCount} entries", color = MidnightOnSurfaceVariant, style = Typography.bodySmall)
                        }
                    }
                    Text("-${uiState.currencySymbol}${String.format(Locale.US, "%,.2f", category.totalAmount)}", color = Color.White, style = Typography.labelLarge)
                }
            }
        }
    }
}
