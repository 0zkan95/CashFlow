package com.example.cashbookneo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import com.example.cashbookneo.viewmodel.MainViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cashbookneo.ui.components.*
import com.example.cashbookneo.ui.theme.*

@Composable
fun ManageAccountsScreen(
    mainViewModel: MainViewModel,
    onNavigate: (String) -> Unit,
    onEditAccount: (String) -> Unit = {}
) {
    val accounts by mainViewModel.accounts.collectAsState()
    val activeAccount = mainViewModel.activeAccount

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "ACTIVE ACCOUNT", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CashInGreen))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Current Ledger", style = Typography.labelSmall, color = CashInGreen)
                }
            }
        }

        item {
            ActiveAccountCard(
                name = activeAccount.name,
                email = activeAccount.email,
                info = "${activeAccount.ledgerType} · ${activeAccount.currency}",
                entriesCount = 24,
                syncStatus = "Drive Encrypted",
                onEdit = { onEditAccount(activeAccount.id) }
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Other Accounts", style = Typography.headlineSmall, color = MidnightOnSurface)
                    Text(text = "Isolated workspaces & offline budgets", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MidnightPrimary.copy(alpha = 0.15f)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Rounded.Add, null, tint = MidnightPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New Profile", style = Typography.labelSmall, color = MidnightPrimary)
                }
            }
        }

        item {
            Column {
                accounts.filter { it.id != activeAccount.id }.forEach { account ->
                    OtherAccountItem(
                        initials = account.name.take(2).uppercase(),
                        avatarColor = if (account.id == "acc2") CashInGreen else CashOutRose,
                        title = account.name,
                        description = account.ledgerType,
                        currencyBadge = "${account.currency} (${account.currencySymbol})",
                        stats = "Active Ledger",
                        isBiometric = true,
                        isLocalActive = true,
                        onSwitch = { mainViewModel.switchAccount(account) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        item {
            IsolationCallout()
        }

        item {
            Text(
                text = "MANAGEMENT TOOLS",
                style = Typography.labelSmall,
                color = MidnightOnSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
            ) {
                Column {
                    ManagementToolRow(
                        icon = Icons.Rounded.ToggleOn,
                        title = "Default Startup Account",
                        subtitle = "Currently: ${activeAccount.name}",
                        onClick = {}
                    )
                    ManagementToolRow(
                        icon = Icons.Rounded.FileDownload,
                        title = "Export Consolidated Ledgers",
                        subtitle = "CSV, JSON, or Encrypted .cashbook",
                        onClick = {}
                    )
                    ManagementToolRow(
                        icon = Icons.Rounded.Lock,
                        title = "Profile Lock & Privacy Pins",
                        subtitle = "Require fingerprint to switch ledgers",
                        onClick = {}
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
