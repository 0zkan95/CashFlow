package com.example.cashbookneo.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cashbookneo.security.SecurityUtils
import com.example.cashbookneo.ui.components.*
import com.example.cashbookneo.ui.theme.*

@Composable
fun AppLockScreen(
    userName: String = "Hidayet Aslan",
    vaultName: String = "Primary Ledger",
    pinHash: String?,
    pinSalt: String?,
    onUnlock: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    val maxPinLength = 6
    var errorState by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MidnightBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AppLockHeader(userName = userName, ledgerName = vaultName)

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MidnightSurfaceContainerLow
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.Shield,
                            null,
                            tint = if (errorState) CashOutRose else MidnightPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                        Icon(
                            Icons.Rounded.Key,
                            null,
                            tint = CashInGreen,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 4.dp, y = 4.dp)
                                .background(MidnightBackground, CircleShape)
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (errorState) "Incorrect PIN" else "Verify Identity",
                    style = Typography.displayLarge,
                    color = if (errorState) CashOutRose else MidnightOnSurface
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Enter 6-digit Privacy PIN or authenticate with Biometrics to unlock your ledger.",
                    style = Typography.bodyMedium,
                    color = MidnightOnSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 48.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                PinDotIndicator(pinLength = pin.length, maxDigits = maxPinLength)

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = { }) {
                    Text(text = "Forgot PIN? Recover with Master Key", style = Typography.labelLarge, color = MidnightPrimary)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            BiometricUnlockCard(onBiometricClick = { onUnlock() })

            Spacer(modifier = Modifier.height(24.dp))

            LockKeypad(
                onDigitClick = { digit ->
                    if (pin.length < maxPinLength) {
                        errorState = false
                        pin += digit
                        if (pin.length == maxPinLength) {
                            val salt = pinSalt ?: ""
                            val hash = SecurityUtils.hashPin(pin, salt)
                            if (hash == pinHash || pinHash == null) {
                                onUnlock()
                            } else {
                                pin = ""
                                errorState = true
                            }
                        }
                    }
                },
                onDeleteClick = {
                    if (pin.isNotEmpty()) {
                        pin = pin.dropLast(1)
                    }
                },
                onBiometricClick = { onUnlock() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            SecurityNoticeCard()

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.RecentActors, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Switch Profile", style = Typography.labelLarge, color = MidnightOnSurface)
                    }
                }
                TextButton(onClick = { }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.History, null, tint = CashOutRose, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Emergency Lockout", style = Typography.labelLarge, color = CashOutRose)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
