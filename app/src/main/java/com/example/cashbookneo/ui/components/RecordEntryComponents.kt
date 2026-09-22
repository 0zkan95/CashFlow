package com.example.cashbookneo.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.sp
import com.example.cashbookneo.ui.theme.*
import java.util.Locale

@Composable
fun EntryTypeToggle(
    isCredit: Boolean,
    onTypeChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MidnightSurfaceContainerLow)
            .padding(4.dp)
    ) {
        // Cash Out
        Surface(
            onClick = { onTypeChange(false) },
            modifier = Modifier.weight(1f).fillMaxHeight(),
            shape = RoundedCornerShape(12.dp),
            color = if (!isCredit) CashOutRose else Color.Transparent
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Rounded.NorthEast,
                    contentDescription = null,
                    tint = if (!isCredit) Color.White else MidnightOnSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Cash Out",
                        style = Typography.labelLarge,
                        color = if (!isCredit) Color.White else MidnightOnSurface
                    )
                    Text(
                        text = "Debit",
                        style = Typography.labelSmall,
                        color = if (!isCredit) Color.White.copy(alpha = 0.7f) else MidnightOnSurfaceVariant
                    )
                }
            }
        }

        // Cash In
        Surface(
            onClick = { onTypeChange(true) },
            modifier = Modifier.weight(1f).fillMaxHeight(),
            shape = RoundedCornerShape(12.dp),
            color = if (isCredit) CashInGreen else Color.Transparent
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Rounded.SouthWest,
                    contentDescription = null,
                    tint = if (isCredit) CashInOnGreen else MidnightOnSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Cash In",
                        style = Typography.labelLarge,
                        color = if (isCredit) CashInOnGreen else MidnightOnSurface
                    )
                    Text(
                        text = "Credit",
                        style = Typography.labelSmall,
                        color = if (isCredit) CashInOnGreen.copy(alpha = 0.7f) else MidnightOnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AmountInputHeader(
    amount: String,
    isCredit: Boolean,
    currencySymbol: String,
    currencyCode: String,
    estimatedBalance: String,
    onCurrencyClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                onClick = onCurrencyClick,
                shape = RoundedCornerShape(9999.dp),
                color = MidnightSurfaceContainerHigh
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CashInGreen))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "$currencyCode ($currencySymbol)", style = Typography.labelSmall, color = MidnightOnSurface)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Rounded.UnfoldMore, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isCredit) "+" else "-",
                    style = CurrencyDisplay.copy(fontSize = 36.sp),
                    color = if (isCredit) CashInGreen else CashOutRose
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$currencySymbol $amount",
                    style = CurrencyDisplay.copy(fontSize = 48.sp),
                    color = MidnightOnSurface
                )
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .width(2.dp)
                        .height(40.dp)
                        .background(MidnightPrimary)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Estimated Balance after: $estimatedBalance",
                style = Typography.bodySmall,
                color = MidnightOnSurfaceVariant
            )
        }
    }
}

data class EntryCategory(val id: String, val name: String, val icon: ImageVector, val color: Color)

@Composable
fun CategoryGridPicker(
    categories: List<EntryCategory>,
    selectedCategoryId: String?,
    onCategorySelected: (String) -> Unit
) {
    val allCategories = categories + EntryCategory("custom", "Custom", Icons.Rounded.Add, MidnightOutline)
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(allCategories) { category ->
            val isSelected = category.id == selectedCategoryId
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onCategorySelected(category.id) }
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) category.color.copy(alpha = 0.15f) else MidnightSurfaceContainerLow,
                    border = if (isSelected) BorderStroke(1.dp, category.color) else null
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            category.icon,
                            contentDescription = null,
                            tint = if (isSelected) category.color else MidnightOnSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.name,
                    style = Typography.labelSmall,
                    color = if (isSelected) MidnightOnSurface else MidnightOnSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun NumericKeypad(
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    val buttons = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        ".", "0", "DEL"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        buttons.chunked(3).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { button ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(2f)
                            .clickable {
                                if (button == "DEL") onDeleteClick() else onDigitClick(button)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (button == "DEL") {
                            Icon(Icons.Rounded.Backspace, null, tint = MidnightOnSurfaceVariant)
                        } else {
                            Text(
                                text = button,
                                style = Typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = MidnightOnSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodSelector(
    selectedMethod: String,
    onMethodSelected: (String) -> Unit
) {
    val methods = listOf("Cash", "Debit card", "Bank transfer")
    val icons = listOf(Icons.Rounded.Payments, Icons.Rounded.CreditCard, Icons.Rounded.AccountBalance)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        methods.zip(icons).forEach { (method, icon) ->
            val isSelected = method == selectedMethod
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clickable { onMethodSelected(method) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MidnightPrimary.copy(alpha = 0.1f) else MidnightSurfaceContainerLow,
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MidnightPrimary) else null
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(
                        icon,
                        null,
                        tint = if (isSelected) MidnightPrimary else MidnightOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = method,
                        style = Typography.labelSmall,
                        color = if (isSelected) MidnightPrimary else MidnightOnSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun AttachmentSection(
    files: List<String>,
    onAttach: () -> Unit,
    onCamera: () -> Unit,
    onRemove: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("ATTACHMENTS", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(
                onClick = onCamera,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = MidnightSurfaceContainerLow
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.CameraAlt, null, tint = MidnightOnSurfaceVariant)
                }
            }
            Surface(
                onClick = onAttach,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = MidnightSurfaceContainerLow
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.AttachFile, null, tint = MidnightOnSurfaceVariant)
                }
            }
            files.forEachIndexed { index, file ->
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MidnightSurfaceContainerHigh
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Icon(Icons.Rounded.Description, null, modifier = Modifier.align(Alignment.Center), tint = MidnightPrimary)
                        IconButton(onClick = { onRemove(index) }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Rounded.Cancel, null, tint = CashOutRose, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RepeatTransactionSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Repeat, null, tint = MidnightOnSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Repeat Transaction", style = Typography.labelLarge, color = MidnightOnSurface)
            Text("Schedule this entry regularly", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
        }
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
}
