package com.example.cashbookneo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.cashbookneo.model.Account
import com.example.cashbookneo.model.SideNavItem
import com.example.cashbookneo.ui.theme.*
import com.example.cashbookneo.viewmodel.MainViewModel
import java.util.Locale

@Composable
fun AppDrawerContent(
    mainViewModel: MainViewModel,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit
) {
    val scrollState = rememberScrollState()
    val accounts by mainViewModel.accounts.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(MidnightSurface)
            .border(width = 1.dp, color = Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(0.dp))
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Header Section
        DrawerHeader(onClose = onClose)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // User Section / Multi-Account Switcher
        DrawerUserProfile(
            activeAccount = mainViewModel.activeAccount,
            accounts = accounts,
            onAccountSwitch = { mainViewModel.switchAccount(it) },
            onNavigate = onNavigate,
            onClose = onClose
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Groups
        DrawerGroup("CORE BANKING") {
            DrawerNavItem(
                item = SideNavItem("home", "Home Dashboard", Icons.Rounded.Home, isGlow = true),
                isSelected = currentRoute == "home",
                onClick = { onNavigate("home"); onClose() }
            )
            DrawerNavItem(
                item = SideNavItem("search", "Custom Search", Icons.Rounded.Search, trailingText = "Filters"),
                isSelected = currentRoute == "search",
                onClick = { onNavigate("search"); onClose() }
            )
            DrawerNavItem(
                item = SideNavItem("analytics", "Monthly Summary", Icons.Rounded.BarChart, trailingText = "+14.2%", statusColor = CashInGreen),
                isSelected = currentRoute == "analytics",
                onClick = { onNavigate("analytics"); onClose() }
            )
            DrawerNavItem(
                item = SideNavItem("savings", "Savings Goals", Icons.Rounded.Savings, trailingText = "4 Active", statusColor = Color(0xFF10B981)),
                isSelected = currentRoute == "savings",
                onClick = { onNavigate("savings"); onClose() }
            )
        }
        
        DrawerGroup("PRODUCTIVITY") {
            DrawerNavItem(
                item = SideNavItem("notes", "Financial Notes", Icons.Rounded.Description, trailingText = "8 Memos"),
                isSelected = currentRoute == "notes",
                onClick = { onNavigate("notes"); onClose() }
            )
            DrawerNavItem(
                item = SideNavItem("Scanner", "Receipt Scanner", Icons.Rounded.CenterFocusWeak, trailingText = "AI OCR", statusColor = MidnightPrimary),
                isSelected = false,
                onClick = { onNavigate("transaction/add"); onClose() }
            )
            DrawerNavItem(
                item = SideNavItem("Export", "Export Statements", Icons.Rounded.FileDownload, trailingText = ">"),
                isSelected = false,
                onClick = { onNavigate("backup"); onClose() }
            )
        }
        
        DrawerGroup("SYSTEM & SYNC") {
            DrawerNavItem(
                item = SideNavItem("backup", "Backup & Restore", Icons.Rounded.Sync, trailingText = "✓ 2m ago", statusColor = CashInGreen),
                isSelected = currentRoute == "backup",
                onClick = { onNavigate("backup"); onClose() }
            )
            DrawerNavItem(
                item = SideNavItem("Privacy", "App Lock & Privacy", Icons.Rounded.Fingerprint, trailingText = "Biometric ON"),
                isSelected = false,
                onClick = { onNavigate("settings"); onClose() }
            )
            DrawerNavItem(
                item = SideNavItem("settings", "Settings", Icons.Rounded.Settings, trailingText = ">"),
                isSelected = currentRoute == "settings",
                onClick = { onNavigate("settings"); onClose() }
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Footer
        DrawerFooter()
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DrawerHeader(onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MidnightSurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = MidnightPrimary, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "CashFlow", style = Typography.headlineSmall, color = MidnightOnSurface)
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "AES-256", style = Typography.labelSmall, color = Color(0xFF10B981))
                }
            }
        }
        
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        ) {
            Icon(Icons.Rounded.Close, contentDescription = null, tint = MidnightOnSurface, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun DrawerUserProfile(
    activeAccount: Account,
    accounts: List<Account>,
    onAccountSwitch: (Account) -> Unit,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MidnightSurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Person, contentDescription = null, tint = MidnightPrimary, modifier = Modifier.size(28.dp))
                    // Status dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                            .border(width = 2.dp, color = MidnightSurface, shape = CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = activeAccount.name, style = Typography.labelLarge, color = MidnightOnSurface)
                        Icon(
                            if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MidnightOnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(text = activeAccount.email, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                }
                IconButton(onClick = { onNavigate("accounts/manage"); onClose() }) {
                    Icon(Icons.Rounded.SyncAlt, contentDescription = "Switch Profile", tint = MidnightOnSurfaceVariant, modifier = Modifier.size(20.dp))
                }
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                accounts.filter { it.id != activeAccount.id }.forEach { account ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAccountSwitch(account); expanded = false }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = MidnightSurfaceContainerHigh) {
                            Icon(Icons.Rounded.Person, null, modifier = Modifier.padding(4.dp), tint = MidnightOnSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = account.name, style = Typography.labelMedium, color = MidnightOnSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            DrawerLedgerBadge(activeAccount)
        }
    }
}

@Composable
fun DrawerLedgerBadge(account: Account) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(Color.Black.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF8B5CF6)))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "${account.ledgerType} (${account.currency})", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${account.currencySymbol}${String.format(Locale.US, "%.2f", account.balance)}", 
            style = Typography.labelSmall, 
            color = Color(0xFF10B981)
        )
    }
}

@Composable
fun DrawerGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = Typography.labelSmall,
            color = MidnightOnSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
fun DrawerNavItem(item: SideNavItem, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) MidnightPrimary.copy(alpha = 0.15f) else Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                item.icon,
                contentDescription = null,
                tint = if (isSelected) MidnightPrimary else MidnightOnSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.label,
                style = Typography.labelLarge,
                color = if (isSelected) MidnightOnSurface else MidnightOnSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            
            if (item.trailingText != null) {
                Text(
                    text = item.trailingText,
                    style = Typography.labelSmall,
                    color = item.statusColor ?: MidnightOnSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            if (item.isGlow && isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MidnightPrimary))
            }
        }
    }
}

@Composable
fun DrawerFooter() {
    val themeState = LocalTheme.current
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.DarkMode, contentDescription = null, tint = MidnightOnSurfaceVariant)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Dark Mode", style = Typography.labelLarge, color = MidnightOnSurface)
                    Text(
                        text = if (themeState.isDarkMode) "Midnight OLED Active" else "Light Mode Active", 
                        style = Typography.bodySmall, 
                        color = MidnightOnSurfaceVariant
                    )
                }
            }
            Switch(
                checked = themeState.isDarkMode,
                onCheckedChange = { themeState.isDarkMode = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MidnightPrimary,
                    uncheckedThumbColor = MidnightOnSurfaceVariant,
                    uncheckedTrackColor = MidnightSurfaceContainerHigh
                )
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "V2.4.1 (Build 842)", style = Typography.bodySmall, color = MidnightOnSurfaceVariant.copy(alpha = 0.5f))
            Text(text = "Changelog", style = Typography.bodySmall, color = MidnightOnSurfaceVariant.copy(alpha = 0.5f))
        }
    }
}
