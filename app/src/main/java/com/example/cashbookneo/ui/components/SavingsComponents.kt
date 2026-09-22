package com.example.cashbookneo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.cashbookneo.model.AssetDistribution
import com.example.cashbookneo.model.SavingsVault
import com.example.cashbookneo.ui.theme.*
import java.util.Locale

@Composable
fun ValueHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "ESTIMATED TOTAL VALUE", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Rounded.Visibility, contentDescription = null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(16.dp))
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(CashInGreen.copy(alpha = 0.12f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CashInGreen))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Live Rates", style = Typography.labelSmall, color = CashInGreen)
            }
        }
    }
}

@Composable
fun BigValueDisplay(amount: String, growth: String, currencySymbol: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = amount, style = Typography.displayLarge, color = MidnightOnSurface)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "$currencySymbol eq.", style = Typography.bodySmall, color = MidnightOnSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(CashInGreen.copy(alpha = 0.12f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .padding(bottom = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.ArrowOutward, contentDescription = null, tint = CashInGreen, modifier = Modifier.size(14.dp))
                Text(text = growth, style = Typography.labelSmall, color = CashInGreen)
            }
        }
    }
}

@Composable
fun SavingsAnalyticsCard(
    distributions: List<AssetDistribution>,
    selectedAsset: String,
    onAssetChange: (String) -> Unit,
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit,
    trendData: List<Float>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(MidnightSurfaceContainerHigh)
                        .padding(4.dp)
                ) {
                    val tabs = listOf("All Assets", "Fiat", "Gold")
                    tabs.forEach { tab ->
                        val isSelected = tab == selectedAsset
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(if (isSelected) MidnightPrimary.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { onAssetChange(tab) }
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(text = tab, style = Typography.labelSmall, color = if (isSelected) MidnightPrimary else MidnightOnSurfaceVariant)
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(MidnightSurfaceContainerHigh)
                        .padding(4.dp)
                ) {
                    val periods = listOf("1M", "6M", "1Y")
                    periods.forEach { period ->
                        val isSelected = period == selectedPeriod
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(if (isSelected) MidnightOnSurface.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable { onPeriodChange(period) }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = period, style = Typography.labelSmall, color = if (isSelected) MidnightOnSurface else MidnightOnSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Segmented Asset Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                distributions.forEach { dist ->
                    Box(modifier = Modifier.weight(dist.percentage).fillMaxHeight().background(dist.color))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                distributions.forEach { dist ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(dist.color))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "${dist.label} ${dist.percentage.toInt()}%", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            BezierCurveChart(
                data = trendData,
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )
        }
    }
}

@Composable
fun BezierCurveChart(data: List<Float>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val path = Path()
        
        if (data.isEmpty()) return@Canvas

        val xStep = width / (data.size - 1)
        
        path.moveTo(0f, height * (1f - data[0]))
        
        for (i in 1 until data.size) {
            val prevX = (i - 1) * xStep
            val prevY = height * (1f - data[i - 1])
            val currX = i * xStep
            val currY = height * (1f - data[i])
            
            path.cubicTo(
                prevX + xStep / 2, prevY,
                currX - xStep / 2, currY,
                currX, currY
            )
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(MidnightPrimary, Color.Transparent),
                startY = 0f,
                endY = height
            ),
            style = Stroke(width = 3.dp.toPx())
        )
        
        // Data points
        data.forEachIndexed { i, value ->
            val x = i * xStep
            val y = height * (1f - value)
            drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 4.dp.toPx(), center = Offset(x, y))
        }
    }
}

@Composable
fun VaultItem(
    vault: SavingsVault,
    onAddClick: () -> Unit,
    onSubtractClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MidnightSurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (vault.iconName) {
                        "savings" -> Icons.Rounded.Savings
                        "lock" -> Icons.Rounded.Lock
                        "home" -> Icons.Rounded.Home
                        "flight" -> Icons.Rounded.Flight
                        "diamond" -> Icons.Rounded.Diamond
                        "directions_car" -> Icons.Rounded.DirectionsCar
                        "smartphone" -> Icons.Rounded.Smartphone
                        "restaurant" -> Icons.Rounded.Restaurant
                        else -> Icons.Rounded.AccountBalanceWallet
                    }
                    Icon(icon, contentDescription = null, tint = vault.categoryColor, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = vault.title, style = Typography.bodyLarge, color = MidnightOnSurface)
                        if (vault.isCompleted) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = CashInGreen, modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(
                        text = "${vault.type} · ${vault.asset}   ${vault.date}",
                        style = Typography.bodySmall,
                        color = MidnightOnSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = vault.amount, style = CurrencyLedger, color = MidnightOnSurface)
                    if (vault.amountUsd != null) {
                        Text(text = vault.amountUsd, style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                    } else if (vault.growth != null) {
                        Text(text = vault.growth, style = Typography.labelSmall, color = CashInGreen)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = vault.goal, style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                Text(text = vault.statusText, style = Typography.labelSmall, color = if (vault.isCompleted) CashInGreen else Color(0xFFF59E0B))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(MidnightSurfaceContainerLow)) {
                Box(modifier = Modifier.fillMaxWidth(vault.progress).fillMaxHeight().background(if (vault.isCompleted) CashInGreen else Color(0xFFF59E0B)))
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAddClick,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CashInGreen.copy(alpha = 0.1f))
                ) {
                    Icon(Icons.Rounded.Add, null, tint = CashInGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add", style = Typography.labelSmall, color = CashInGreen)
                }
                Button(
                    onClick = onSubtractClick,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CashOutRose.copy(alpha = 0.1f))
                ) {
                    Icon(Icons.Rounded.Remove, null, tint = CashOutRose, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Subtract", style = Typography.labelSmall, color = CashOutRose)
                }
            }
        }
    }
}

@Composable
fun SavingsActionButtons(
    onDepositClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onDepositClick,
            modifier = Modifier.weight(1f).height(56.dp),
            shape = RoundedCornerShape(9999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CashInGreen)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = CashInOnGreen)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Deposit", style = Typography.labelLarge, color = CashInOnGreen)
        }
        Button(
            onClick = onWithdrawClick,
            modifier = Modifier.weight(1f).height(56.dp),
            shape = RoundedCornerShape(9999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MidnightSurfaceContainerLow)
        ) {
            Icon(Icons.Default.Remove, contentDescription = null, tint = MidnightOnSurfaceVariant)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Withdraw", style = Typography.labelLarge, color = MidnightOnSurface)
        }
    }
}

@Composable
fun VaultHeroPreview(
    title: String,
    type: String,
    amount: String,
    valuation: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(18.dp),
                color = MidnightSurfaceContainerHigh
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = accentColor, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = type, style = Typography.labelSmall, color = accentColor)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CashInGreen))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Active Vault", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = title.ifBlank { "Untitled Vault" }, style = Typography.headlineLarge, color = MidnightOnSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = amount, style = Typography.labelLarge, color = CashInGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "•", color = MidnightOnSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = valuation, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun AssetClassificationToggle(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val types = listOf(
            Triple("Gold & Metal", Icons.Rounded.MonetizationOn, "Gold"),
            Triple("Fiat Currency", Icons.Rounded.Payments, "Fiat"),
            Triple("Real Asset", Icons.Rounded.HomeWork, "Commodity")
        )
        types.forEach { (label, icon, type) ->
            val isSelected = type == selectedType
            Surface(
                modifier = Modifier.weight(1f).height(100.dp).clickable { onTypeSelected(type) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) MidnightPrimary.copy(alpha = 0.1f) else MidnightSurfaceContainerLow,
                border = if (isSelected) BorderStroke(1.dp, MidnightPrimary.copy(alpha = 0.5f)) else null
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(icon, null, tint = if (isSelected) MidnightPrimary else MidnightOnSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = label, style = Typography.labelSmall, color = if (isSelected) MidnightOnSurface else MidnightOnSurfaceVariant)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DenominationUnitChips(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit
) {
    val units = listOf("Grams (g)", "Troy Oz (oz)", "EUR (€)", "RSD (дин)", "USD ($)")
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        units.forEach { unit ->
            val isSelected = unit == selectedUnit
            Surface(
                onClick = { onUnitSelected(unit) },
                shape = RoundedCornerShape(9999.dp),
                color = if (isSelected) MidnightPrimary.copy(alpha = 0.2f) else MidnightSurfaceContainerLow,
                border = if (isSelected) BorderStroke(1.dp, MidnightPrimary) else null
            ) {
                Text(
                    text = unit,
                    style = Typography.labelSmall,
                    color = if (isSelected) MidnightPrimary else MidnightOnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun VaultIconGrid(
    selectedIconName: String,
    onIconSelected: (String) -> Unit
) {
    val icons = listOf(
        "savings" to Icons.Rounded.Savings,
        "lock" to Icons.Rounded.Lock,
        "home" to Icons.Rounded.Home,
        "flight" to Icons.Rounded.Flight,
        "diamond" to Icons.Rounded.Diamond,
        "directions_car" to Icons.Rounded.DirectionsCar
    )
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        icons.forEach { (name, icon) ->
            val isSelected = name == selectedIconName
            Surface(
                modifier = Modifier.size(48.dp).clickable { onIconSelected(name) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MidnightPrimary.copy(alpha = 0.1f) else MidnightSurfaceContainerLow,
                border = if (isSelected) BorderStroke(1.dp, MidnightPrimary) else null
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = if (isSelected) MidnightPrimary else MidnightOnSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun GlowAccentPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val colors = listOf(
        Color(0xFF10B981), // Emerald
        Color(0xFF818CF8), // Indigo
        Color(0xFFF87171), // Coral
        Color(0xFF6366F1), // Periwinkle
        Color(0xFF34D399)  // Mint
    )
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        colors.forEach { color ->
            val isSelected = color == selectedColor
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onColorSelected(color) }
                    .border(if (isSelected) BorderStroke(2.dp, Color.White) else BorderStroke(0.dp, Color.Transparent), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
