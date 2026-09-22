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
fun ActiveAccountCard(
    name: String,
    email: String,
    info: String,
    entriesCount: Int,
    syncStatus: String,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Profile Image
                Box {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = MidnightSurfaceContainerHigh
                    ) {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = null,
                            tint = MidnightPrimary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    // Checkmark badge
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(CashInGreen)
                            .border(width = 2.dp, color = MidnightSurfaceContainerLow, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Check, null, tint = MidnightOnSurface, modifier = Modifier.size(12.dp))
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = name, style = Typography.labelLarge, color = MidnightOnSurface)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MidnightPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "Primary", style = Typography.labelSmall, color = MidnightPrimary)
                        }
                    }
                    Text(text = email, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                    Text(text = info, style = Typography.bodySmall, color = MidnightOnSurfaceVariant.copy(alpha = 0.7f))
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MidnightSurfaceContainerHigh)
                ) {
                    Icon(
                        Icons.Rounded.Edit,
                        contentDescription = null,
                        tint = MidnightOnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AccountStatCard(
                    icon = Icons.Rounded.Description,
                    label = "Entries Logged",
                    value = "$entriesCount records",
                    modifier = Modifier.weight(1f)
                )
                AccountStatCard(
                    icon = Icons.Rounded.CloudDone,
                    label = "Sync Status",
                    value = syncStatus,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AccountStatCard(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MidnightSurfaceContainerHigh.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MidnightSurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = MidnightPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = label, style = Typography.bodySmall, color = MidnightOnSurfaceVariant.copy(alpha = 0.6f))
                Text(text = value, style = Typography.labelMedium, color = MidnightOnSurface)
            }
        }
    }
}

@Composable
fun OtherAccountItem(
    initials: String,
    avatarColor: Color,
    title: String,
    description: String,
    currencyBadge: String,
    stats: String,
    isBiometric: Boolean = false,
    isLocalActive: Boolean = false,
    onSwitch: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = avatarColor.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = initials, style = Typography.headlineSmall, color = avatarColor)
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = Typography.labelLarge, color = MidnightOnSurface)
                    Text(text = description, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SettingsBadge(text = currencyBadge, color = CashInGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "• $stats", style = Typography.bodySmall, color = MidnightOnSurfaceVariant.copy(alpha = 0.7f))
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        onClick = onSwitch,
                        shape = RoundedCornerShape(9999.dp),
                        color = MidnightSurfaceContainerHigh
                    ) {
                        Text(
                            text = "Switch",
                            style = Typography.labelSmall,
                            color = MidnightOnSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(20.dp))
                }
            }
            
            if (isBiometric || isLocalActive) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    if (isBiometric) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Lock, null, tint = MidnightOutline, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Biometric PIN Protection", style = Typography.bodySmall, color = MidnightOutline)
                        }
                    }
                    if (isLocalActive) {
                        Text(text = "Local Ledger Active", style = Typography.labelSmall, color = CashInGreen)
                    } else {
                        Text(text = "Auto-Backup ON", style = Typography.labelSmall, color = MidnightOnSurfaceVariant.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }
}

@Composable
fun IsolationCallout() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.03f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MidnightSurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.VerifiedUser, null, tint = MidnightPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Strict Ledger Isolation", style = Typography.labelLarge, color = MidnightOnSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Each profile keeps its own independent SQLite database, zero-knowledge offline caching, and isolated cloud backups. Switching accounts will never mix transaction tallies.",
                    style = Typography.bodySmall,
                    color = MidnightOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ManagementToolRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = MidnightSurfaceContainerHigh
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MidnightOnSurfaceVariant,
                modifier = Modifier.padding(10.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = Typography.labelLarge, color = MidnightOnSurface)
            Text(text = subtitle, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
        }

        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = MidnightOnSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}
