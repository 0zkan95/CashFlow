package com.example.cashbookneo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashbookneo.ui.components.*
import com.example.cashbookneo.ui.theme.*
import com.example.cashbookneo.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigate: (String) -> Unit,
    onNavigateToSecurity: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Design Tokens (Midnight Ledger)
    val cardBg = MidnightSurfaceContainerLow
    val cardBorder = Color.White.copy(alpha = 0.08f)
    val indigoAccent = Color(android.graphics.Color.parseColor(uiState.accentColorHex))
    val emeraldAccent = CashInGreen
    val coralAccent = CashOutRose

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Search Bar within Settings
        item {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search settings, currencies, security...", color = MidnightOutline, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(18.dp)) },
                trailingIcon = { Icon(Icons.Rounded.Tune, contentDescription = null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(18.dp)) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = cardBg,
                    unfocusedContainerColor = cardBg,
                    focusedBorderColor = indigoAccent,
                    unfocusedBorderColor = cardBorder,
                    focusedTextColor = Color.White
                ),
                singleLine = true
            )
        }

        // User Profile Banner Card
        item {
            SettingsProfileCard(
                name = uiState.activeAccount.name,
                email = uiState.activeAccount.email,
                primaryCurrency = uiState.activeAccount.currency,
                secondaryCurrency = "EUR",
                ledgerCount = 2,
                onManageProfiles = { onNavigate("accounts/manage") }
            )
        }

        // Section 1: General & Currency
        item {
            SettingsSection(title = "General & Currency", icon = Icons.Rounded.AccountBalance, actionText = "Ledger Config") {
                // Primary Currency Selector
                SettingsItem(
                    icon = Icons.Rounded.AccountBalanceWallet,
                    title = "Primary Currency",
                    subtitle = "${uiState.activeAccount.currency} (Serbian Dinar)",
                    trailingContent = {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MidnightSurfaceContainerHigh)
                                .clickable { /* Picker */ }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("${uiState.activeAccount.currencySymbol} ↕", color = Color.White, style = Typography.labelSmall)
                        }
                    }
                )
                HorizontalDivider(color = cardBorder)
                // Multi-Currency Converter Switch
                SettingsItem(
                    icon = Icons.Rounded.CurrencyExchange,
                    title = "Multi-Currency Converter",
                    subtitle = "Instant parity conversion on ledger entries",
                    trailingContent = {
                        SettingsToggle(checked = uiState.multiCurrencyConverter, onCheckedChange = { viewModel.updateMultiCurrency(it) })
                    }
                )
                HorizontalDivider(color = cardBorder)
                // First Day of Week
                SettingsItem(
                    icon = Icons.Rounded.CalendarToday,
                    title = "First Day of Week",
                    subtitle = "Sets analytics cycle timing",
                    trailingContent = {
                        SettingsSegmentedControl(
                            options = listOf("Mon", "Sun"),
                            selectedOption = uiState.firstDayOfWeek,
                            onOptionSelected = { viewModel.updateFirstDayOfWeek(it) }
                        )
                    }
                )
                HorizontalDivider(color = cardBorder)
                // Default Quick-Entry Mode
                SettingsItem(
                    icon = Icons.Rounded.SwapHoriz,
                    title = "Default Quick-Entry",
                    subtitle = "Pre-selected tab in action drawer",
                    trailingContent = {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            SettingsActionButton(
                                text = "↓ Cash Out",
                                onClick = { viewModel.updateDefaultEntryType("Outflow") },
                                color = if (uiState.defaultEntryType == "Outflow") coralAccent else MidnightOnSurfaceVariant
                            )
                            SettingsActionButton(
                                text = "↑ Cash In",
                                onClick = { viewModel.updateDefaultEntryType("Inflow") },
                                color = if (uiState.defaultEntryType == "Inflow") emeraldAccent else MidnightOnSurfaceVariant
                            )
                        }
                    }
                )
            }
        }

        // Section 2: Appearance & Aesthetics
        item {
            SettingsSection(title = "Appearance & Aesthetics", icon = Icons.Rounded.Palette, actionText = "Midnight Glow") {
                SettingsItem(
                    icon = Icons.Rounded.DarkMode,
                    title = "Midnight Dark Theme",
                    subtitle = "OLED low-power deep canvas",
                    trailingContent = {
                        SettingsToggle(checked = uiState.isDarkMode, onCheckedChange = { viewModel.updateDarkMode(it) })
                    }
                )
                HorizontalDivider(color = cardBorder)
                SettingsItem(
                    icon = Icons.Rounded.ColorLens,
                    title = "System Accent Tint",
                    trailingContent = {
                        SettingsAccentTintPicker(
                            colors = listOf(Color(0xFF818CF8), Color(0xFF10B981), Color(0xFFFBBF24), Color(0xFFF87171)),
                            selectedColor = indigoAccent,
                            onColorSelected = { viewModel.updateAccentColor("#" + Integer.toHexString(it.value.toInt()).substring(2).uppercase()) }
                        )
                    }
                )
                HorizontalDivider(color = cardBorder)
                SettingsItem(
                    icon = Icons.Rounded.PieChart,
                    title = "Chart Visualization",
                    subtitle = "Analytics layout rendering",
                    trailingContent = {
                        SettingsSegmentedControl(
                            options = listOf("Donut", "Bars"),
                            selectedOption = uiState.chartType,
                            onOptionSelected = { viewModel.updateChartType(it) }
                        )
                    }
                )
                HorizontalDivider(color = cardBorder)
                SettingsItem(
                    icon = Icons.Rounded.ViewAgenda,
                    title = "Compact Record View",
                    subtitle = "Display 25% more items per screen",
                    trailingContent = {
                        SettingsToggle(checked = uiState.compactView, onCheckedChange = { viewModel.updateCompactView(it) })
                    }
                )
            }
        }

        // Section 3: Security & Cloud Vault
        item {
            SettingsSection(title = "Security & Cloud Vault", icon = Icons.Rounded.Security, actionText = "Shield Verified") {
                SettingsItem(
                    icon = Icons.Rounded.Fingerprint,
                    title = "Biometrics & 6-Digit PIN",
                    subtitle = "Touch ID & hardware keystore enabled",
                    onClick = onNavigateToSecurity,
                    trailingContent = { SettingsBadge("Active", CashInGreen) }
                )
                HorizontalDivider(color = cardBorder)
                SettingsItem(
                    icon = Icons.Rounded.VisibilityOff,
                    title = "App Switcher Privacy",
                    subtitle = "Blurs balances when minimizing app",
                    trailingContent = {
                        SettingsToggle(checked = uiState.screenMasking, onCheckedChange = { viewModel.updateScreenMasking(it) })
                    }
                )
                HorizontalDivider(color = cardBorder)
                SettingsItem(
                    icon = Icons.Rounded.CloudSync,
                    title = "Google Drive Sync",
                    subtitle = "Encrypted snapshot · Today, 01:14",
                    trailingContent = {
                        SettingsActionButton(text = "Sync Now", onClick = { /* Sync */ })
                    }
                )
            }
        }

        // Section 4: Data & Storage
        item {
            SettingsSection(title = "Data & Storage", icon = Icons.Rounded.Storage, actionText = "SQLite v3.44") {
                SettingsItem(
                    icon = Icons.Rounded.FileDownload,
                    title = "Export Statements & Ledger",
                    subtitle = "CSV, encrypted JSON, or printable PDF"
                )
                HorizontalDivider(color = cardBorder)
                SettingsItem(
                    icon = Icons.Rounded.DeleteSweep,
                    title = "Clear Cached Proofs & Receipts",
                    subtitle = "Reclaim ${String.format("%.1f", uiState.cachedReceiptsSizeMb)} MB of local storage",
                    trailingContent = {
                        SettingsActionButton(text = "Clean", onClick = { viewModel.onClearCacheClicked() }, color = coralAccent)
                    }
                )
                HorizontalDivider(color = cardBorder)
                SettingsItem(
                    icon = Icons.Rounded.Engineering,
                    title = "Integrity Check & Vacuum",
                    subtitle = "Optimize indexing & SQLite pages",
                    trailingContent = {
                        if (uiState.isRunningVacuum) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = indigoAccent)
                        } else {
                            IconButton(onClick = { viewModel.onIntegrityCheckAndVacuumClicked() }) {
                                Icon(Icons.Rounded.SettingsSuggest, null, tint = MidnightOnSurfaceVariant)
                            }
                        }
                    }
                )
            }
        }

        // Section 5: App Version & Zero-Telemetry Footer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.AccountBalance, null, tint = indigoAccent, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "CashFlow", style = Typography.headlineSmall, color = Color.White)
                    Text(text = uiState.appVersion, style = Typography.bodySmall, color = emeraldAccent)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SettingsActionButton(text = "Changelog", onClick = {})
                        SettingsActionButton(text = "GitHub", onClick = {})
                        SettingsActionButton(text = "Privacy", onClick = {})
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Crafted with strict offline-first integrity, AES-256 client storage, and absolute zero-telemetry trackers. You own your ledger, your data, and your privacy.",
                        style = Typography.labelSmall.copy(fontSize = 10.sp),
                        color = MidnightOnSurfaceVariant.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Shield, null, tint = emeraldAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Zero-Telemetry Policy Active", style = Typography.labelSmall, color = emeraldAccent)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
    
    // Status Feedback Snackbar
    uiState.statusFeedbackMessage?.let { message ->
        LaunchedEffect(message) {
            // In a real app, use SnackbarHostState
            viewModel.clearFeedback()
        }
    }
}
