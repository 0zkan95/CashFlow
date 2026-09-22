package com.example.cashbookneo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cashbookneo.model.SavingsVault
import com.example.cashbookneo.ui.components.*
import com.example.cashbookneo.ui.theme.*
import com.example.cashbookneo.viewmodel.SavingsViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVaultScreen(
    vaultId: String? = null,
    viewModel: SavingsViewModel,
    onBack: () -> Unit
) {
    val existingVault = remember(vaultId) { viewModel.getVault(vaultId) }
    
    var isNewMode by remember { mutableStateOf(vaultId == null) }
    var title by remember { mutableStateOf(existingVault?.title ?: "") }
    var type by remember { mutableStateOf(existingVault?.type ?: "Gold") }
    var unit by remember { mutableStateOf(existingVault?.asset ?: "Grams (g)") }
    var balance by remember { mutableStateOf(existingVault?.let { viewModel.parseAmount(it.amount).toString() } ?: "0") }
    var enableGoal by remember { mutableStateOf(existingVault?.targetAmount != null) }
    var targetAmount by remember { mutableStateOf(existingVault?.targetAmount?.toString() ?: "") }
    var iconName by remember { mutableStateOf(existingVault?.iconName ?: "savings") }
    var accentColor by remember { mutableStateOf(existingVault?.categoryColor ?: Color(0xFF10B981)) }
    var isArchived by remember { mutableStateOf(existingVault?.isArchived ?: false) }

    val icon = when (iconName) {
        "savings" -> Icons.Rounded.Savings
        "lock" -> Icons.Rounded.Lock
        "home" -> Icons.Rounded.Home
        "flight" -> Icons.Rounded.Flight
        "diamond" -> Icons.Rounded.Diamond
        "directions_car" -> Icons.Rounded.DirectionsCar
        else -> Icons.Rounded.AccountBalanceWallet
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = if (isNewMode) "NEW VAULT" else "EDIT VAULT", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                        Text(text = "Asset Vault", style = Typography.headlineSmall, color = MidnightOnSurface)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.Close, null, tint = MidnightOnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightBackground)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MidnightBackground)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val currentBalVal = balance.toDoubleOrNull() ?: 0.0
                        val targetBalVal = if (enableGoal) targetAmount.toDoubleOrNull() else null
                        val newVault = SavingsVault(
                            id = existingVault?.id ?: UUID.randomUUID().toString(),
                            title = title,
                            type = type,
                            asset = unit.split(" ").first(),
                            date = existingVault?.date ?: "Just Now",
                            amount = balance,
                            goal = if (enableGoal) "Goal: $targetAmount ${unit.split(" ").first()}" else "No Goal",
                            targetAmount = targetBalVal,
                            progress = if (enableGoal && targetBalVal != null) (currentBalVal / targetBalVal).toFloat() else 0f,
                            statusText = if (enableGoal && targetBalVal != null) "${((currentBalVal / targetBalVal) * 100).toInt()}% Reached" else "Active Fund",
                            isCompleted = enableGoal && targetBalVal != null && currentBalVal >= targetBalVal,
                            isArchived = isArchived,
                            iconName = iconName,
                            categoryColor = accentColor
                        )
                        viewModel.saveVault(newVault)
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CashInGreen)
                ) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = CashInOnGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Save Changes", style = Typography.labelLarge, color = CashInOnGreen)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Discard & Return",
                    style = Typography.labelSmall,
                    color = MidnightOnSurfaceVariant,
                    modifier = Modifier.clickable { onBack() }
                )
            }
        },
        containerColor = MidnightBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .background(MidnightSurfaceContainerLow, RoundedCornerShape(9999.dp))
                            .padding(4.dp)
                    ) {
                        Surface(
                            onClick = { isNewMode = false },
                            shape = RoundedCornerShape(9999.dp),
                            color = if (!isNewMode) MidnightPrimary.copy(alpha = 0.15f) else Color.Transparent
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Edit, null, modifier = Modifier.size(14.dp), tint = if (!isNewMode) MidnightPrimary else MidnightOnSurfaceVariant)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Edit Vault", style = Typography.labelSmall, color = if (!isNewMode) MidnightPrimary else MidnightOnSurfaceVariant)
                            }
                        }
                        Surface(
                            onClick = { isNewMode = true },
                            shape = RoundedCornerShape(9999.dp),
                            color = if (isNewMode) MidnightPrimary.copy(alpha = 0.15f) else Color.Transparent
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Add, null, modifier = Modifier.size(14.dp), tint = if (isNewMode) MidnightPrimary else MidnightOnSurfaceVariant)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "New Vault", style = Typography.labelSmall, color = if (isNewMode) MidnightPrimary else MidnightOnSurfaceVariant)
                            }
                        }
                    }
                }
            }

            item {
                VaultHeroPreview(
                    title = title,
                    type = type,
                    amount = "$balance ${unit.split(" ").first()}",
                    valuation = "≈ $435.50 USD",
                    icon = icon,
                    accentColor = accentColor
                )
            }

            item {
                SectionHeader(title = "Asset Classification", action = "Select Type")
                AssetClassificationToggle(selectedType = type, onTypeSelected = { type = it })
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(text = "Vault Name", style = Typography.labelMedium, color = MidnightOnSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Emergency Gold Bullion", color = MidnightOutline) },
                        leadingIcon = { Icon(Icons.Rounded.OutlinedFlag, null, tint = MidnightOutline) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MidnightSurfaceContainerLow,
                            unfocusedContainerColor = MidnightSurfaceContainerLow,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            item {
                SectionHeader(title = "Denomination / Unit")
                DenominationUnitChips(selectedUnit = unit, onUnitSelected = { unit = it })
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Current Balance", style = Typography.labelMedium, color = MidnightOnSurfaceVariant)
                        Text(text = "Verified Balance", style = Typography.labelSmall, color = CashInGreen)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = balance,
                        onValueChange = { balance = it },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Text(text = unit.split(" ").first(), color = MidnightOnSurfaceVariant, modifier = Modifier.padding(end = 16.dp)) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MidnightSurfaceContainerLow,
                            unfocusedContainerColor = MidnightSurfaceContainerLow,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Flag, null, tint = CashInGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Enable Savings Goal", style = Typography.labelLarge, color = MidnightOnSurface)
                                Text(text = "Track pacing and milestone celebration", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                            }
                            Switch(
                                checked = enableGoal,
                                onCheckedChange = { enableGoal = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = CashInGreen,
                                    uncheckedThumbColor = MidnightOnSurfaceVariant,
                                    uncheckedTrackColor = MidnightSurfaceContainerHigh
                                )
                            )
                        }

                        if (enableGoal) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(text = "Target Amount", style = Typography.labelMedium, color = MidnightOnSurfaceVariant)
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = targetAmount,
                                onValueChange = { targetAmount = it },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = { Text(text = unit.split(" ").first(), color = MidnightOnSurfaceVariant, modifier = Modifier.padding(end = 16.dp)) },
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MidnightSurfaceContainerHigh,
                                    unfocusedContainerColor = MidnightSurfaceContainerHigh,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            val current = balance.toDoubleOrNull() ?: 0.0
                            val target = targetAmount.toDoubleOrNull() ?: 1.0
                            val progress = (current / target).toFloat().coerceIn(0f, 1f)
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Progress to Milestone", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                                Text(text = "${(progress * 100).toInt()}% Reached", style = Typography.labelSmall, color = CashInGreen)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(MidnightSurfaceContainerHigh)) {
                                Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().background(CashInGreen))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "$current ${unit.split(" ").first()}", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                                Text(text = "Goal: $target ${unit.split(" ").first()}", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MidnightSurfaceContainerHigh
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.AutoAwesome, null, tint = MidnightPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Recommended cadence: +2.5 Grams / month to conclude in 6 months",
                                        style = Typography.bodySmall,
                                        color = MidnightOnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                SectionHeader(title = "Vault Identity & Aesthetic", icon = Icons.Rounded.Public)
                Text(text = "Vault Symbol", style = Typography.labelMedium, color = MidnightOnSurfaceVariant, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                VaultIconGrid(selectedIconName = iconName, onIconSelected = { iconName = it })
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Theme Glow Accent", style = Typography.labelMedium, color = MidnightOnSurfaceVariant, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                GlowAccentPicker(selectedColor = accentColor, onColorSelected = { accentColor = it })
            }

            if (!isNewMode) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Archive Vault", style = Typography.labelLarge, color = MidnightOnSurface)
                                    Text(text = "Hide from ledger views but preserve past balance history", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                                }
                                Switch(
                                    checked = isArchived,
                                    onCheckedChange = { isArchived = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = MidnightPrimary,
                                        uncheckedThumbColor = MidnightOnSurfaceVariant,
                                        uncheckedTrackColor = MidnightSurfaceContainerHigh
                                    )
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { 
                                    viewModel.deleteVault(vaultId!!)
                                    onBack()
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E))
                            ) {
                                Icon(Icons.Rounded.DeleteSweep, null, tint = CashOutRose)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = "Delete Asset Vault", style = Typography.labelSmall, color = CashOutRose)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Permanently delinks transactions & removes holding metrics from all valuation charts.",
                                style = Typography.bodySmall,
                                color = MidnightOnSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null, icon: ImageVector? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = title, style = Typography.headlineSmall, color = MidnightOnSurface)
        }
        if (action != null) {
            Text(text = action, style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
        }
    }
}
