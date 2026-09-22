package com.example.cashbookneo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashbookneo.ui.components.*
import com.example.cashbookneo.ui.theme.*
import com.example.cashbookneo.viewmodel.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureSecurityScreen(
    viewModel: SecurityViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val cardBg = MidnightSurfaceContainerLow
    val cardBorder = Color.White.copy(alpha = 0.08f)
    val emeraldAccent = CashInGreen
    val indigoAccent = MidnightPrimary
    val coralAccent = CashOutRose

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "APP LOCK & SECURITY", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                        Text(text = "Security Gateway", style = Typography.headlineSmall, color = MidnightOnSurface)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = MidnightOnSurface)
                    }
                },
                actions = {
                    if (uiState.hasChanges) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(Color(0xFFFBBF24).copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFFBBF24)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Unsaved Changes", color = Color(0xFFFBBF24), style = Typography.labelSmall)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightBackground)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MidnightBackground)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Rounded.Close, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel", style = Typography.labelLarge)
                }
                Button(
                    onClick = { viewModel.saveSettings(onBack) },
                    modifier = Modifier.weight(1.5f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent)
                ) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = CashInOnGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Settings", color = CashInOnGreen, style = Typography.labelLarge)
                }
            }
        },
        containerColor = MidnightBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Header Info
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = cardBg
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = MidnightSurfaceContainerHigh) {
                            Icon(Icons.Rounded.Shield, null, tint = indigoAccent, modifier = Modifier.padding(10.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = uiState.activeAccount.name, style = Typography.labelLarge, color = Color.White)
                            Text(text = "Primary Ledger • Master Vault", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                        }
                        SettingsBadge("Keystore Active", CashInGreen)
                    }
                }
            }

            // Master Toggle
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(12.dp), color = MidnightSurfaceContainerHigh) {
                                Icon(Icons.Rounded.Security, null, tint = indigoAccent, modifier = Modifier.padding(10.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("App Lock Protection", style = Typography.labelLarge, color = Color.White)
                                Text("Require cryptographic authentication when launching", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                            }
                            Switch(
                                checked = uiState.isAppLockEnabled,
                                onCheckedChange = { viewModel.onAppLockToggle(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = emeraldAccent)
                            )
                        }
                        
                        if (uiState.isAppLockEnabled) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("AUTO-LOCK FREQUENCY", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val options = listOf(0L to "Immediately", 30000L to "30s", 60000L to "1 min", 300000L to "5 mins")
                                options.forEach { (ms, label) ->
                                    val isSelected = uiState.lockTimeoutMs == ms
                                    Surface(
                                        modifier = Modifier.weight(1f).height(40.dp).clickable { viewModel.onTimeoutChange(ms) },
                                        shape = RoundedCornerShape(99.dp),
                                        color = if (isSelected) indigoAccent.copy(alpha = 0.2f) else MidnightSurfaceContainerHigh,
                                        border = if (isSelected) BorderStroke(1.dp, indigoAccent) else null
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = label, style = Typography.labelSmall, color = if (isSelected) Color.White else MidnightOnSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Authentication Methods
            item {
                Text(
                    text = "AUTHENTICATION METHODS",
                    style = Typography.labelSmall,
                    color = MidnightOnSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        SettingsItem(
                            icon = Icons.Rounded.Pin,
                            title = "6-Digit Master PIN",
                            subtitle = if (uiState.pinConfigured) "•••••• · Configured" else "Not Configured",
                            trailingContent = {
                                TextButton(onClick = { /* Change PIN */ }) {
                                    Text("Change", color = indigoAccent, style = Typography.labelSmall)
                                }
                            }
                        )
                        HorizontalDivider(color = cardBorder, modifier = Modifier.padding(horizontal = 20.dp))
                        SettingsItem(
                            icon = Icons.Rounded.Fingerprint,
                            title = "Biometrics & Fingerprint",
                            subtitle = "Sensor Active & Calibrated",
                            trailingContent = {
                                Switch(
                                    checked = uiState.isBiometricsEnabled,
                                    onCheckedChange = { viewModel.onBiometricsToggle(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = emeraldAccent)
                                )
                            }
                        )
                    }
                }
            }

            // PIN Editor Section (Design shows it in-screen)
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(shape = RoundedCornerShape(99.dp), color = Color.White.copy(alpha = 0.05f)) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.History, null, tint = indigoAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Re-Keying Master Vault", style = Typography.labelSmall, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Enter New 6-Digit PIN", style = Typography.headlineSmall, color = Color.White)
                    Text("Step 1 of 2: Create personal sequence", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    PinDotIndicator(pinLength = uiState.currentPin.length, maxDigits = 6)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = cardBg, border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Rounded.Info, null, tint = indigoAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Avoid sequential numbers (123456) or repeating patterns. Protected by StrongBox TEE.",
                                style = Typography.bodySmall,
                                color = MidnightOnSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    LockKeypad(
                        onDigitClick = { /* No-op for now */ },
                        onDeleteClick = { /* No-op */ },
                        onBiometricClick = { /* Test Biometrics */ }
                    )
                }
            }

            // Enrolled Biometrics
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Enrolled Biometrics", style = Typography.headlineSmall, color = Color.White)
                        Text("Hardware-backed fingerprint credentials", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                    }
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(99.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = indigoAccent.copy(alpha = 0.15f)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Rounded.Add, null, tint = indigoAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enroll Finger", color = indigoAccent, style = Typography.labelSmall)
                    }
                }
            }

            items(2) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = MidnightSurfaceContainerHigh) {
                            Icon(Icons.Rounded.Fingerprint, null, tint = emeraldAccent, modifier = Modifier.padding(8.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Fingerprint ${index + 1}", style = Typography.labelLarge, color = Color.White)
                            Text(if (index == 0) "Primary (Right Index) • Added 14 days ago" else "Secondary (Left Thumb) • Added 4 days ago", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Rounded.Delete, null, tint = MidnightOnSurfaceVariant)
                        }
                    }
                }
            }
            
            item {
                Row(modifier = Modifier.padding(vertical = 16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Rounded.VerifiedUser, null, tint = emeraldAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Fallback policy: 6-digit PIN will be mandated after 3 consecutive failed biometric attempts or upon device reboot.",
                        style = Typography.bodySmall,
                        color = MidnightOnSurfaceVariant
                    )
                }
            }

            // Advanced Protection
            item {
                Text(
                    text = "Advanced Protection",
                    style = Typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        SecurityItemSwitch("Scramble Keypad Layout", "Randomizes PIN numbers on lock to prevent smudge attacks.", uiState.scrambleKeypad) { viewModel.onScrambleToggle(it) }
                        HorizontalDivider(color = cardBorder, modifier = Modifier.padding(horizontal = 20.dp))
                        SecurityItemSwitch("Lock on Screen Off", "Instantly terminate active session whenever device display shuts down.", uiState.lockOnScreenOff) { viewModel.onLockOnScreenOffToggle(it) }
                        HorizontalDivider(color = cardBorder, modifier = Modifier.padding(horizontal = 20.dp))
                        SecurityItemSwitch("FLAG_SECURE Mode", "Prevent screenshots and blank out CashFlow in Android app switcher.", uiState.screenMasking) { viewModel.onScreenMaskingToggle(it) }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1010))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Warning, null, tint = coralAccent)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Emergency Panic Lockout", style = Typography.labelLarge, color = Color.White)
                                    Text("Requires manual Keystore master recovery file", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                                }
                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("Configure", color = Color.White, style = Typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
            
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.VerifiedUser, null, tint = indigoAccent, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("HARDWARE SECURITY GUARANTEE", style = Typography.labelSmall, color = Color.White)
                    Text(
                        text = "Encrypted with SQLiteCipher AES-256 GCM using Android Keystore StrongBox keys. Zero-Knowledge architectural verification active.",
                        style = Typography.bodySmall,
                        color = MidnightOnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun SecurityItemSwitch(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = Typography.labelLarge, color = Color.White)
            Text(subtitle, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CashInGreen)
        )
    }
}
