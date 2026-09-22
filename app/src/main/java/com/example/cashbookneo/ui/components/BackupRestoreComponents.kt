package com.example.cashbookneo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cashbookneo.ui.theme.*

@Composable
fun GoogleDriveStatusCard(
    userName: String,
    userEmail: String,
    lastSnapshot: String,
    backupSize: String,
    driveUsage: String,
    onSwitchAccount: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MidnightSurfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.CloudQueue,
                            contentDescription = null,
                            tint = MidnightPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Google Drive", style = Typography.labelLarge, color = MidnightOnSurface)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CashInGreen))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Connected & Synced", style = Typography.bodySmall, color = CashInGreen)
                        }
                    }
                }
                SettingsBadge(text = "AES-256", color = CashInGreen)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // User Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MidnightSurfaceContainerHigh)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = MidnightPrimary.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = userName.take(1), style = Typography.labelLarge, color = MidnightPrimary)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = userName, style = Typography.labelMedium, color = MidnightOnSurface)
                    Text(text = userEmail, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                }
                Text(
                    text = "Switch",
                    style = Typography.labelSmall,
                    color = MidnightPrimary,
                    modifier = Modifier.clickable(onClick = onSwitchAccount)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Last Snapshot", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                    Text(text = lastSnapshot, style = Typography.labelLarge, color = MidnightOnSurface)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Backup Size", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = backupSize, style = Typography.labelLarge, color = CashInGreen)
                        Text(text = " ($driveUsage)", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 0.05f },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                color = CashInGreen,
                trackColor = MidnightSurfaceContainerHigh
            )
        }
    }
}

@Composable
fun AutomaticBackupCard(
    isAutoBackupEnabled: Boolean,
    onAutoBackupToggle: (Boolean) -> Unit,
    selectedFrequency: String,
    onFrequencySelected: (String) -> Unit,
    isWifiOnlyEnabled: Boolean,
    onWifiOnlyToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
                    Icon(Icons.Rounded.Sync, null, tint = MidnightOnSurfaceVariant)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Automatic Backup", style = Typography.labelLarge, color = MidnightOnSurface)
                        Text(text = "Syncs snapshot automatically", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                    }
                }
                SettingsToggle(checked = isAutoBackupEnabled, onCheckedChange = onAutoBackupToggle)
            }

            Spacer(modifier = Modifier.height(20.dp))
            
            Text(text = "Frequency", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MidnightSurfaceContainerHigh)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Daily", "Weekly", "Monthly").forEach { freq ->
                    val isSelected = freq == selectedFrequency
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MidnightPrimary.copy(alpha = 0.8f) else Color.Transparent)
                            .clickable { onFrequencySelected(freq) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = freq,
                            style = Typography.labelSmall,
                            color = if (isSelected) MidnightOnPrimary else MidnightOnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Wifi, null, tint = MidnightOnSurfaceVariant)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Backup on Wi-Fi Only", style = Typography.labelMedium, color = MidnightOnSurface)
                }
                SettingsToggle(checked = isWifiOnlyEnabled, onCheckedChange = onWifiOnlyToggle)
            }
        }
    }
}

@Composable
fun EncryptionSettingsCard(
    isEncryptionEnabled: Boolean,
    onEncryptionToggle: (Boolean) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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
                    Icon(Icons.Rounded.Shield, null, tint = MidnightOnSurfaceVariant)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "End-to-End Encryption", style = Typography.labelLarge, color = MidnightOnSurface)
                        Text(text = "Client-side AES-256-GCM cipher", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                    }
                }
                SettingsToggle(checked = isEncryptionEnabled, onCheckedChange = onEncryptionToggle)
            }

            if (isEncryptionEnabled) {
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Backup Password", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                    Text(text = "Strong • 14 chars", style = Typography.labelSmall, color = CashInGreen)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MidnightSurfaceContainerHigh
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Key, null, tint = MidnightOutline, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "•".repeat(20),
                            style = Typography.bodyLarge,
                            color = MidnightOnSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Rounded.Visibility, null, tint = MidnightOutline, modifier = Modifier.size(20.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                PasswordStrengthIndicator(strength = 0.8f)
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Rounded.Info,
                            null,
                            tint = MidnightPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Your key never leaves this device. Without it, encrypted backups cannot be recovered by anyone, including the CashFlow team.",
                            style = Typography.bodySmall,
                            color = MidnightOnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordStrengthIndicator(strength: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(4) { index ->
            val color = when {
                strength >= (index + 1) * 0.25f -> CashInGreen
                strength >= index * 0.25f -> CashInGreen.copy(alpha = 0.3f)
                else -> MidnightSurfaceContainerHigh
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun BackupRestoreActions(
    onBackupNow: () -> Unit,
    onRestoreData: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onBackupNow,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CashInGreen)
        ) {
            Icon(Icons.Rounded.CloudUpload, null, tint = CashInOnGreen)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Backup Now", style = Typography.labelLarge, color = CashInOnGreen)
        }
        
        Button(
            onClick = onRestoreData,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MidnightSurfaceContainerLow)
        ) {
            Icon(Icons.Rounded.History, null, tint = MidnightOnSurface)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Restore Data", style = Typography.labelLarge, color = MidnightOnSurface)
        }
    }
}

@Composable
fun OfflineRawExportsSection() {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = "Offline & Raw Exports",
            style = Typography.labelMedium,
            color = MidnightOnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExportImportCard(
                icon = Icons.Rounded.FileDownload,
                title = "Export .CBK",
                subtitle = "Encrypted JSON",
                modifier = Modifier.weight(1f)
            )
            ExportImportCard(
                icon = Icons.Rounded.FileUpload,
                title = "Import File",
                subtitle = "Restore local file",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ExportImportCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(20.dp),
        color = MidnightSurfaceContainerLow,
        onClick = {}
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = MidnightSurfaceContainerHigh
            ) {
                Icon(icon, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, style = Typography.labelLarge, color = MidnightOnSurface)
            Text(text = subtitle, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
        }
    }
}
