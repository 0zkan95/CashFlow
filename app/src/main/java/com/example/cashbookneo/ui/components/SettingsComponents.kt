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
import androidx.compose.ui.unit.dp
import com.example.cashbookneo.ui.theme.*

@Composable
fun SettingsProfileCard(
    name: String,
    email: String,
    primaryCurrency: String,
    secondaryCurrency: String,
    ledgerCount: Int,
    onManageProfiles: () -> Unit
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
                // Profile Image with Status
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
                    // Status Dot
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(CashInGreen)
                            .border(width = 2.dp, color = MidnightSurfaceContainerLow, shape = CircleShape)
                    )
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CashInGreen.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "$primaryCurrency / $secondaryCurrency",
                                style = Typography.labelSmall,
                                color = CashInGreen
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• $ledgerCount Ledgers",
                            style = Typography.labelSmall,
                            color = MidnightOnSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MidnightSurfaceContainerHigh)
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MidnightOnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable(onClick = onManageProfiles),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.RecentActors,
                        contentDescription = null,
                        tint = MidnightPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Manage Profiles & Ledgers", style = Typography.labelSmall, color = MidnightPrimary)
                }
                Text(text = "Encrypted sync", style = Typography.labelSmall, color = MidnightOnSurfaceVariant.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {},
    icon: ImageVector? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = CashInGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = title, style = Typography.headlineSmall, color = MidnightOnSurface)
            }
            if (actionText != null) {
                Text(
                    text = actionText,
                    style = Typography.labelSmall,
                    color = MidnightPrimary,
                    modifier = Modifier.clickable(onClick = onActionClick)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit = {},
    trailingContent: @Composable () -> Unit = {
        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = MidnightOnSurfaceVariant.copy(alpha = 0.5f)
        )
    }
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
            if (subtitle != null) {
                Text(text = subtitle, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
            }
        }

        trailingContent()
    }
}

@Composable
fun SettingsToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = MidnightPrimary,
            uncheckedThumbColor = MidnightOnSurfaceVariant,
            uncheckedTrackColor = MidnightSurfaceContainerHigh
        )
    )
}

@Composable
fun SettingsSegmentedControl(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(MidnightSurfaceContainerHigh)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(if (isSelected) MidnightPrimary.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { onOptionSelected(option) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    style = Typography.labelSmall,
                    color = if (isSelected) MidnightPrimary else MidnightOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingsAccentTintPicker(
    colors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        colors.forEach { color ->
            val isSelected = color == selectedColor
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onColorSelected(color) }
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = Color.White.copy(alpha = 0.5f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsBadge(text: String, color: Color = MidnightPrimary) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, style = Typography.labelSmall, color = color)
    }
}

@Composable
fun SettingsActionButton(
    text: String,
    onClick: () -> Unit,
    color: Color = MidnightPrimary
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(9999.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.1f)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        modifier = Modifier.height(36.dp)
    ) {
        Text(text = text, style = Typography.labelSmall, color = color)
    }
}
