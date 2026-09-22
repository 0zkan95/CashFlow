package com.example.cashbookneo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.cashbookneo.navigation.Screen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cashbookneo.model.*
import com.example.cashbookneo.ui.components.*
import com.example.cashbookneo.ui.theme.*
import com.example.cashbookneo.viewmodel.SavingsViewModel
import java.util.Locale
import kotlin.math.abs

@Composable
fun SavingsScreen(
    activeAccount: Account,
    transactions: List<Transaction>,
    onNavigate: (String) -> Unit,
    viewModel: SavingsViewModel = viewModel()
) {
    val currencySymbol = activeAccount.currencySymbol
    val totalSavings = remember(transactions, viewModel.vaults) {
        transactions.filter { it.isCredit }.sumOf { it.amount } - transactions.filter { !it.isCredit }.sumOf { abs(it.amount) }
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedActionType by remember { mutableStateOf(VaultTransactionType.DEPOSIT) }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            ValueHeader()
        }
        item {
            BigValueDisplay(
                amount = String.format(Locale.US, "%s%,.2f", currencySymbol, totalSavings),
                growth = "12.4%",
                currencySymbol = currencySymbol
            )
        }
        item {
            SavingsAnalyticsCard(
                distributions = DummyAssetDistributions,
                selectedAsset = viewModel.selectedAssetFilter,
                onAssetChange = { viewModel.selectedAssetFilter = it },
                selectedPeriod = viewModel.selectedPeriodFilter,
                onPeriodChange = { viewModel.selectedPeriodFilter = it },
                trendData = viewModel.getTrendData()
            )
        }
        item {
            BudgetInsightCard(insight = ExpenseInsight("Predictive Goal Completion", "At current pace, Europe Travel hits 100% in Nov 2026"))
        }
        item {
            VaultHeader(
                count = viewModel.vaults.size,
                onAddClick = { onNavigate(Screen.CreateVault.route) }
            )
        }
        items(viewModel.vaults) { vault ->
            VaultItem(
                vault = vault,
                modifier = Modifier.clickable { onNavigate(Screen.EditVault.createRoute(vault.id)) },
                onAddClick = {
                    viewModel.selectedVaultId = vault.id
                    selectedActionType = VaultTransactionType.DEPOSIT
                    showBottomSheet = true
                },
                onSubtractClick = {
                    viewModel.selectedVaultId = vault.id
                    selectedActionType = VaultTransactionType.WITHDRAW
                    showBottomSheet = true
                }
            )
        }
        item {
            SavingsActionButtons(
                onDepositClick = {
                    viewModel.selectedVaultId = null
                    selectedActionType = VaultTransactionType.DEPOSIT
                    showBottomSheet = true
                },
                onWithdrawClick = {
                    viewModel.selectedVaultId = null
                    selectedActionType = VaultTransactionType.WITHDRAW
                    showBottomSheet = true
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showBottomSheet) {
        VaultTransactionBottomSheet(
            vaults = viewModel.vaults,
            initialVaultId = viewModel.selectedVaultId,
            initialType = selectedActionType,
            onDismissRequest = { showBottomSheet = false },
            onConfirmTransaction = { vaultId, type, amount, note ->
                if (type == VaultTransactionType.DEPOSIT) {
                    viewModel.onDeposit(vaultId, amount, note)
                } else {
                    viewModel.onWithdraw(vaultId, amount, note)
                }
            },
            parseAmount = { viewModel.parseAmount(it) }
        )
    }
}

@Composable
fun VaultHeader(count: Int, onAddClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Active Vaults", style = Typography.headlineSmall, color = MidnightOnSurface)
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = onAddClick,
                modifier = Modifier.size(24.dp).clip(CircleShape).background(MidnightPrimary.copy(alpha = 0.1f))
            ) {
                Icon(Icons.Rounded.Add, null, tint = MidnightPrimary, modifier = Modifier.size(16.dp))
            }
        }
        Text(text = "$count Accounts", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
    }
}
