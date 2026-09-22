package com.example.cashbookneo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashbookneo.model.SavingsVault
import com.example.cashbookneo.ui.theme.*
import java.util.Locale

enum class VaultTransactionType { DEPOSIT, WITHDRAW }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultTransactionBottomSheet(
    vaults: List<SavingsVault>,
    initialVaultId: String? = null,
    initialType: VaultTransactionType = VaultTransactionType.DEPOSIT,
    onDismissRequest: () -> Unit,
    onConfirmTransaction: (vaultId: String, type: VaultTransactionType, amount: Double, note: String?) -> Unit,
    parseAmount: (String) -> Double
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var transactionType by remember { mutableStateOf(initialType) }
    var selectedVault by remember { mutableStateOf(vaults.find { it.id == initialVaultId } ?: vaults.firstOrNull()) }
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var showVaultMenu by remember { mutableStateOf(false) }

    val surfaceColor = MidnightBackground
    val cardBackground = MidnightSurfaceContainerLow
    val cardBorder = Color.White.copy(alpha = 0.08f)
    val emeraldAccent = CashInGreen
    val coralAccent = CashOutRose
    val primaryColor = if (transactionType == VaultTransactionType.DEPOSIT) emeraldAccent else coralAccent

    val enteredAmount = amountText.toDoubleOrNull() ?: 0.0
    val currentBalance = selectedVault?.let { parseAmount(it.amount) } ?: 0.0
    val isWithdrawExceeding = transactionType == VaultTransactionType.WITHDRAW && enteredAmount > currentBalance
    val isValid = enteredAmount > 0.0 && !isWithdrawExceeding && selectedVault != null

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = surfaceColor,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (transactionType == VaultTransactionType.DEPOSIT) "Deposit into Vault" else "Withdraw from Vault",
                    style = Typography.headlineSmall,
                    color = Color.White
                )
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(cardBackground)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MidnightOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Segmented Type Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(cardBackground)
                    .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
                    .padding(4.dp)
            ) {
                // Deposit Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (transactionType == VaultTransactionType.DEPOSIT) emeraldAccent.copy(alpha = 0.2f) else Color.Transparent
                        )
                        .clickable { transactionType = VaultTransactionType.DEPOSIT },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ Deposit",
                        style = Typography.labelLarge,
                        color = if (transactionType == VaultTransactionType.DEPOSIT) emeraldAccent else MidnightOnSurfaceVariant
                    )
                }

                // Withdraw Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (transactionType == VaultTransactionType.WITHDRAW) coralAccent.copy(alpha = 0.2f) else Color.Transparent
                        )
                        .clickable { transactionType = VaultTransactionType.WITHDRAW },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "− Withdraw",
                        style = Typography.labelLarge,
                        color = if (transactionType == VaultTransactionType.WITHDRAW) coralAccent else MidnightOnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Vault Picker Dropdown
            Text(
                text = "SELECT ASSET VAULT",
                style = Typography.labelSmall,
                color = MidnightOnSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(cardBackground)
                        .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
                        .clickable { showVaultMenu = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = selectedVault?.title ?: "Select a Vault",
                            color = Color.White,
                            style = Typography.labelLarge
                        )
                        Text(
                            text = "Current: ${selectedVault?.amount ?: "0"}",
                            color = MidnightOnSurfaceVariant,
                            style = Typography.bodySmall
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = MidnightOnSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = showVaultMenu,
                    onDismissRequest = { showVaultMenu = false },
                    modifier = Modifier
                        .background(MidnightSurfaceContainerHigh)
                        .border(1.dp, cardBorder, RoundedCornerShape(8.dp))
                ) {
                    vaults.forEach { vault ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(vault.title, color = Color.White, style = Typography.bodyLarge)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(vault.amount, color = emeraldAccent, style = Typography.bodyLarge)
                                }
                            },
                            onClick = {
                                selectedVault = vault
                                showVaultMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Amount Input Card
            val unit = selectedVault?.asset?.uppercase(Locale.US) ?: ""
            val isCurrency = unit !in listOf("GOLD", "GRAMS")
            Text(
                text = "AMOUNT (${unit.ifBlank { "VALUE" }})",
                style = Typography.labelSmall,
                color = MidnightOnSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    if (input.all { it.isDigit() || it == '.' } && input.count { it == '.' } <= 1) {
                        amountText = input
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = Typography.displayLarge.copy(fontSize = 24.sp, color = Color.White),
                leadingIcon = if (isCurrency) {
                    { Text(text = selectedVault?.asset ?: "$", color = primaryColor, style = Typography.headlineMedium) }
                } else null,
                trailingIcon = if (!isCurrency) {
                    {
                        Text(
                            text = selectedVault?.asset ?: "",
                            color = primaryColor,
                            style = Typography.labelLarge,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                } else null,
                placeholder = { 
                    Text("0.00", color = MidnightOutline, style = Typography.displayLarge.copy(fontSize = 24.sp)) 
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = isWithdrawExceeding,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = cardBackground,
                    unfocusedContainerColor = cardBackground,
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = cardBorder,
                    errorBorderColor = coralAccent
                ),
                shape = RoundedCornerShape(14.dp)
            )
            if (isWithdrawExceeding) {
                Text(
                    text = "Cannot withdraw more than current balance (${selectedVault?.amount})",
                    color = coralAccent,
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))

            // Smart Preset Increment Pills
            val presets = if (unit in listOf("GOLD", "GRAMS")) {
                listOf(1.0, 2.5, 5.0, 10.0)
            } else {
                listOf(500.0, 1000.0, 2500.0, 5000.0)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    val label = if (preset % 1.0 == 0.0) "+${preset.toInt()}" else "+$preset"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(cardBackground)
                            .border(1.dp, cardBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                val current = amountText.toDoubleOrNull() ?: 0.0
                                amountText = (current + preset).toString()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = MidnightOnSurface,
                            style = Typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Optional Audit Note
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Add memo or note (e.g. Monthly bullion stash)", color = MidnightOutline, style = Typography.bodySmall) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = cardBackground,
                    unfocusedContainerColor = cardBackground,
                    focusedBorderColor = primaryColor.copy(alpha = 0.5f),
                    unfocusedBorderColor = cardBorder
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Confirm Action Button
            Button(
                onClick = {
                    selectedVault?.let { vault ->
                        onConfirmTransaction(
                            vault.id,
                            transactionType,
                            enteredAmount,
                            noteText.ifBlank { null }
                        )
                        onDismissRequest()
                    }
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    disabledContainerColor = MidnightSurfaceContainerHigh
                )
            ) {
                Text(
                    text = if (transactionType == VaultTransactionType.DEPOSIT) "Confirm Deposit" else "Confirm Withdrawal",
                    color = if (isValid) MidnightBackground else MidnightOnSurfaceVariant,
                    style = Typography.labelLarge
                )
            }
        }
    }
}
