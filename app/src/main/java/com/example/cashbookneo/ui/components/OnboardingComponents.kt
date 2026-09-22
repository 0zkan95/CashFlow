package com.example.cashbookneo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashbookneo.ui.theme.*

@Composable
fun OnboardingProgressIndicator(currentStep: Int, totalSteps: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STEP $currentStep OF $totalSteps: ${getStepTitle(currentStep)}",
                style = Typography.labelSmall,
                color = MidnightOnSurfaceVariant,
                letterSpacing = 1.sp
            )
            if (currentStep == totalSteps) {
                Text(
                    text = "Final Step",
                    style = Typography.labelSmall,
                    color = MidnightPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 1..totalSteps) {
                val bgModifier = if (i <= currentStep) {
                    Modifier.background(Brush.horizontalGradient(listOf(MidnightPrimary, Color(0xFF818CF8))))
                } else {
                    Modifier.background(MidnightSurfaceContainerHigh)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .then(bgModifier)
                )
            }
        }
    }
}

private fun getStepTitle(step: Int): String = when (step) {
    1 -> "IDENTITY & ACCOUNT"
    2 -> "SECURITY & PRIVACY"
    3 -> "INITIALIZATION"
    else -> ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, style = Typography.bodyLarge, color = MidnightOutline) },
            leadingIcon = leadingIcon?.let { { Icon(it, null, tint = MidnightPrimary) } },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MidnightSurfaceContainerLow,
                unfocusedContainerColor = MidnightSurfaceContainerLow,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MidnightPrimary
            ),
            singleLine = true
        )
    }
}

@Composable
fun GenderSelector(selected: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    val options = listOf("Male", "Female", "Other")
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "GENDER / TITLE", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MidnightSurfaceContainerLow)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEach { option ->
                val isSelected = option == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MidnightPrimary.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { onSelect(option) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSelected) {
                            Icon(Icons.Rounded.Check, null, tint = MidnightPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = option,
                            style = Typography.labelMedium,
                            color = if (isSelected) MidnightPrimary else MidnightOnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyCard(
    symbol: String,
    code: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(100.dp)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                brush = if (isSelected) Brush.verticalGradient(listOf(MidnightPrimary, Color(0xFF818CF8))) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MidnightPrimary.copy(alpha = 0.1f) else MidnightSurfaceContainerLow
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(text = symbol, style = Typography.headlineSmall, color = if (isSelected) MidnightPrimary else MidnightOnSurface)
                if (isSelected) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = MidnightPrimary, modifier = Modifier.size(16.dp))
                }
            }
            Column {
                Text(text = code, style = Typography.labelLarge, color = MidnightOnSurface, fontWeight = FontWeight.Bold)
                Text(text = label, style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
            }
        }
    }
}

@Composable
fun SecurityOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String? = null
) {
    Card(
        onClick = onSelect,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MidnightPrimary else Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) MidnightPrimary.copy(alpha = 0.05f) else MidnightSurfaceContainerLow)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) MidnightPrimary.copy(alpha = 0.2f) else MidnightSurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = if (isSelected) MidnightPrimary else MidnightOnSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, style = Typography.labelLarge, color = MidnightOnSurface)
                    if (badgeText != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(99.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = badgeText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = Typography.labelSmall,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
            }
            if (isSelected) {
                Icon(Icons.Rounded.CheckCircle, null, tint = MidnightPrimary, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun PrivacyToggle(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        color = MidnightSurfaceContainerLow
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MidnightSurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = MidnightOnSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = Typography.labelLarge, color = MidnightOnSurface)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, style = Typography.bodySmall, color = MidnightOnSurfaceVariant.copy(alpha = 0.7f))
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MidnightPrimary,
                    checkedTrackColor = MidnightPrimary.copy(alpha = 0.2f),
                    uncheckedThumbColor = MidnightOnSurfaceVariant,
                    uncheckedTrackColor = MidnightSurfaceContainerHigh
                )
            )
        }
    }
}

@Composable
fun TemplateCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    tags: List<Pair<String, ImageVector>> = emptyList(),
    badgeText: String? = null
) {
    Card(
        onClick = onSelect,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MidnightPrimary else Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) MidnightPrimary.copy(alpha = 0.05f) else MidnightSurfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) MidnightPrimary else MidnightSurfaceContainerHigh)
                            .border(width = 1.dp, color = if (isSelected) MidnightPrimary else Color.White.copy(alpha = 0.1f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = title, style = Typography.headlineSmall, color = MidnightOnSurface)
                }
                if (badgeText != null) {
                    Surface(
                        shape = RoundedCornerShape(99.dp),
                        color = if (badgeText.contains("Recommended")) Color(0xFF10B981).copy(alpha = 0.12f) else MidnightSurfaceContainerHigh
                    ) {
                        Text(
                            text = badgeText,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = Typography.labelSmall,
                            color = if (badgeText.contains("Recommended")) Color(0xFF10B981) else MidnightOnSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = description, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
            
            if (tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { (label, icon) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MidnightSurfaceContainerHigh.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(icon, null, tint = MidnightPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = label, style = Typography.labelSmall, color = MidnightOnSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}
