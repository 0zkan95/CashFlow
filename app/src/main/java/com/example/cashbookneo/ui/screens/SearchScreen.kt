package com.example.cashbookneo.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashbookneo.model.*
import com.example.cashbookneo.ui.theme.*
import com.example.cashbookneo.viewmodel.*
import com.example.cashbookneo.util.CsvExportHelper
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    activeAccount: Account,
    viewModel: CustomSearchViewModel,
    onTransactionClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val filter = uiState.filter
    val context = LocalContext.current
    val currencySymbol = activeAccount.currencySymbol

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = filter.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search transactions, notes...", color = MidnightOutline) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MidnightOnSurfaceVariant) },
                trailingIcon = {
                    if (filter.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = MidnightOnSurfaceVariant)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MidnightSurfaceContainerLow,
                    unfocusedContainerColor = MidnightSurfaceContainerLow,
                    focusedBorderColor = MidnightPrimary,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
        }

        item {
            CriteriaMatrixCard(
                filter = filter,
                currencySymbol = currencySymbol,
                onToggleCollapse = { viewModel.toggleMatrixCollapse() },
                onAmountChange = { min, max -> viewModel.onAmountRangeChanged(min, max) },
                onChannelSelect = { viewModel.onPaymentChannelSelected(it) },
                onReceiptToggle = { viewModel.onReceiptToggleChanged(it) },
                onPresetSelect = { viewModel.onDatePresetSelected(it) }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${uiState.totalMatchedCount} matched entries",
                        color = Color.White,
                        style = Typography.labelLarge
                    )
                    Text(
                        text = "Net: $currencySymbol${String.format(Locale.US, "%.2f", uiState.netSumAmount)}",
                        color = if (uiState.netSumAmount >= 0) CashInGreen else CashOutRose,
                        style = Typography.labelSmall
                    )
                }
                Button(
                    onClick = { CsvExportHelper.exportSearchResultsToCsv(context, uiState.results) },
                    colors = ButtonDefaults.buttonColors(containerColor = MidnightSurfaceContainerLow),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = "Export", tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("CSV", color = Color.White, style = Typography.labelSmall)
                }
            }
        }

        items(uiState.results, key = { it.id }) { item ->
            TransactionResultItem(
                item = item, 
                currencySymbol = currencySymbol,
                onClick = { onTransactionClick(item.id) }
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.resetFilters() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MidnightOnSurfaceVariant)
                ) {
                    Text("Reset Criteria", style = Typography.labelLarge)
                }
                Button(
                    onClick = { /* Apply */ },
                    modifier = Modifier.weight(1.5f),
                    colors = ButtonDefaults.buttonColors(containerColor = MidnightPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply (${uiState.totalMatchedCount} Results)", color = MidnightOnPrimary, style = Typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun CriteriaMatrixCard(
    filter: SearchFilterState,
    currencySymbol: String,
    onToggleCollapse: () -> Unit,
    onAmountChange: (Double, Double) -> Unit,
    onChannelSelect: (PaymentChannel) -> Unit,
    onReceiptToggle: (Boolean) -> Unit,
    onPresetSelect: (DateHorizonPreset) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleCollapse() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = MidnightPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Criteria Matrix", color = Color.White, style = Typography.labelLarge)
                }
                Text(
                    text = if (filter.isMatrixCollapsed) "Expand ▾" else "Collapse ▴",
                    color = MidnightOnSurfaceVariant,
                    style = Typography.labelSmall
                )
            }

            AnimatedVisibility(visible = !filter.isMatrixCollapsed) {
                Column(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("DATE HORIZON", color = MidnightOnSurfaceVariant, style = Typography.labelSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            DateHorizonPreset.THIS_MONTH to "This Month",
                            DateHorizonPreset.LAST_30_DAYS to "30 Days",
                            DateHorizonPreset.CUSTOM_SPAN to "Custom"
                        ).forEach { (preset, label) ->
                            FilterChip(
                                selected = filter.datePreset == preset,
                                onClick = { onPresetSelect(preset) },
                                label = { Text(label, style = Typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MidnightPrimary,
                                    selectedLabelColor = MidnightOnPrimary
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("AMOUNT SPECTRUM", color = MidnightOnSurfaceVariant, style = Typography.labelSmall)
                        Text("$currencySymbol${filter.minAmount.toInt()} - $currencySymbol${filter.maxAmount.toInt()}", color = Color.White, style = Typography.labelSmall)
                    }
                    var sliderRange by remember(filter.minAmount, filter.maxAmount) {
                        mutableStateOf(filter.minAmount.toFloat()..filter.maxAmount.toFloat())
                    }
                    RangeSlider(
                        value = sliderRange,
                        onValueChange = { range ->
                            sliderRange = range
                            onAmountChange(range.start.toDouble(), range.endInclusive.toDouble())
                        },
                        valueRange = 0f..10000f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = MidnightPrimary,
                            inactiveTrackColor = MidnightSurfaceContainerHigh
                        )
                    )

                    Text("PAYMENT CHANNEL", color = MidnightOnSurfaceVariant, style = Typography.labelSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PaymentChannel.entries.forEach { channel ->
                            FilterChip(
                                selected = filter.paymentChannel == channel,
                                onClick = { onChannelSelect(channel) },
                                label = { Text(channel.name.replace("_", " "), style = Typography.labelSmall) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Only with receipt / tax invoice", color = Color.White, style = Typography.bodyMedium)
                        }
                        Switch(
                            checked = filter.onlyWithReceipt,
                            onCheckedChange = { onReceiptToggle(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MidnightPrimary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionResultItem(item: TransactionSearchResult, currencySymbol: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = MidnightSurfaceContainerHigh
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (item.flowType == FlowType.INFLOW_ONLY) Icons.AutoMirrored.Rounded.TrendingUp else Icons.AutoMirrored.Rounded.TrendingDown,
                        contentDescription = null,
                        tint = if (item.flowType == FlowType.INFLOW_ONLY) CashInGreen else CashOutRose,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, style = Typography.labelLarge, color = MidnightOnSurface)
                Text(text = "${item.categoryName} • ${item.ledgerName}", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (item.flowType == FlowType.INFLOW_ONLY) "+" else "-"}$currencySymbol${String.format(Locale.US, "%.2f", item.amount)}",
                    style = Typography.labelLarge,
                    color = if (item.flowType == FlowType.INFLOW_ONLY) CashInGreen else Color.White
                )
                if (item.hasReceipt) {
                    Icon(Icons.Rounded.Receipt, contentDescription = null, tint = MidnightPrimary, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
