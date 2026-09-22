package com.example.cashbookneo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cashbookneo.model.*
import com.example.cashbookneo.ui.theme.*
import java.util.Locale

@Composable
fun CriteriaMatrix(currencySymbol: String = "$", modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                    Icon(Icons.Rounded.Tune, contentDescription = null, tint = MidnightPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Criteria Matrix", color = Color.White, style = Typography.labelLarge)
                }
                Text("Collapse ▴", color = MidnightOnSurfaceVariant, style = Typography.labelSmall)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            Text("DATE HORIZON", color = MidnightOnSurfaceVariant, style = Typography.labelSmall)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("This Month", "30 Days", "Custom").forEach { label ->
                    FilterChip(
                        selected = label == "30 Days",
                        onClick = {},
                        label = { Text(label, style = Typography.labelSmall) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("AMOUNT SPECTRUM", color = MidnightOnSurfaceVariant, style = Typography.labelSmall)
                Text("$currencySymbol 20.00 – $currencySymbol 250.00", color = MidnightOnSurface, style = Typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(12.dp))
            RangeSlider(
                value = 0.2f..0.6f,
                onValueChange = {},
                colors = SliderDefaults.colors(activeTrackColor = MidnightPrimary)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$currencySymbol 0 Min", color = MidnightOnSurfaceVariant, style = Typography.labelSmall)
                Text("$currencySymbol 500 Mid", color = MidnightOnSurfaceVariant, style = Typography.labelSmall)
                Text("$currencySymbol 1,000+ Max", color = MidnightOnSurfaceVariant, style = Typography.labelSmall)
            }
        }
    }
}

@Composable
fun SearchResultCard(
    transaction: Transaction,
    currencySymbol: String = "$",
    onClick: (Transaction) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick(transaction) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MidnightSurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (transaction.isCredit) Icons.AutoMirrored.Rounded.TrendingUp else Icons.AutoMirrored.Rounded.TrendingDown,
                    null,
                    tint = if (transaction.isCredit) CashInGreen else CashOutRose
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = transaction.payee, style = Typography.labelLarge, color = MidnightOnSurface)
                Text(
                    text = "${transaction.category.name} • ${transaction.paymentMethod}", 
                    style = Typography.bodySmall, 
                    color = MidnightOnSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (transaction.isCredit) "+" else "-"}$currencySymbol${String.format(Locale.US, "%,.2f", Math.abs(transaction.amount))}",
                    style = Typography.labelLarge,
                    color = if (transaction.isCredit) CashInGreen else MidnightOnSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (transaction.attachments > 0) {
                        Icon(Icons.Rounded.AttachFile, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${transaction.attachments} Receipts", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                    } else if (transaction.status != null) {
                        Text(transaction.status.name, style = Typography.labelSmall, color = Color(0xFF10B981))
                    }
                }
            }
        }
    }
}
