package com.example.cashbookneo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.cashbookneo.data.entity.*
import com.example.cashbookneo.ui.theme.*
import java.util.Locale

@Composable
fun QuickActionNoteCard(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = Typography.labelSmall, color = Color.White)
        }
    }
}

@Composable
fun IouNoteCard(
    memo: FinancialMemoEntity,
    onSettle: () -> Unit,
    onClick: () -> Unit
) {
    val emeraldAccent = CashInGreen
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(emeraldAccent.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("IOU · Receivable", color = emeraldAccent, style = Typography.labelSmall)
                }
                Icon(Icons.Default.MoreVert, null, tint = MidnightOnSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = memo.title, style = Typography.labelLarge, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Principal Outstanding", color = MidnightOnSurfaceVariant, style = Typography.bodySmall)
                    Text(text = memo.amountStr ?: "€0.00", color = emeraldAccent, style = Typography.headlineMedium)
                }
                Button(
                    onClick = onSettle,
                    colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Check, null, tint = MidnightBackground, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Settle", color = MidnightBackground, style = Typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun ChecklistNoteCard(
    memo: FinancialMemoEntity,
    items: List<ChecklistItemEntity>,
    onToggleItem: (ChecklistItemEntity) -> Unit,
    onClick: () -> Unit
) {
    val periwinkle = MidnightPrimary
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(periwinkle.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Checklist", color = periwinkle, style = Typography.labelSmall)
                }
                Icon(Icons.Default.MoreVert, null, tint = MidnightOnSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = memo.title, style = Typography.labelLarge, color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            
            items.take(3).forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = { onToggleItem(item) },
                        colors = CheckboxDefaults.colors(checkedColor = periwinkle)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.label,
                        style = Typography.bodyMedium,
                        color = if (item.isChecked) MidnightOnSurfaceVariant else Color.White
                    )
                }
            }
            if (items.size > 3) {
                Text(text = "+ ${items.size - 3} more items", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
            }
        }
    }
}
