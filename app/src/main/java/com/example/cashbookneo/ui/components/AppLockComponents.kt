package com.example.cashbookneo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashbookneo.ui.theme.*

@Composable
fun AppLockHeader(userName: String, ledgerName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(9999.dp),
            color = MidnightSurfaceContainerLow,
            modifier = Modifier.height(48.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = MidnightSurfaceContainerHigh
                ) {
                    Icon(
                        Icons.Rounded.Person,
                        null,
                        tint = MidnightPrimary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = userName, style = Typography.labelLarge, color = MidnightOnSurface)
                    Text(text = ledgerName, style = Typography.labelSmall, color = MidnightOnSurfaceVariant.copy(alpha = 0.6f))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    Icons.Rounded.Lock,
                    null,
                    tint = CashInGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CashInGreen))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Vault Active", style = Typography.labelSmall, color = CashInGreen)
        }
    }
}

@Composable
fun PinDotIndicator(pinLength: Int, maxDigits: Int = 6) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxDigits) { index ->
            val isActive = index < pinLength
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isActive) MidnightPrimary.copy(alpha = 0.6f) else MidnightSurfaceContainerHigh)
            )
        }
    }
}

@Composable
fun BiometricUnlockCard(onBiometricClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onBiometricClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = CashInGreen.copy(alpha = 0.1f)
            ) {
                Icon(
                    Icons.Rounded.Fingerprint,
                    null,
                    tint = CashInGreen,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Touch Fingerprint Sensor", style = Typography.labelLarge, color = MidnightOnSurface)
                Text(text = "Biometric match ready for Hidayet", style = Typography.bodySmall, color = CashInGreen)
            }
            IconButton(
                onClick = onBiometricClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MidnightSurfaceContainerHigh)
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowForward,
                    null,
                    tint = MidnightOnSurfaceVariant
                )
            }
        }
    }
}

data class KeypadKey(val label: String, val subLabel: String? = null, val icon: ImageVector? = null)

@Composable
fun LockKeypad(
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onBiometricClick: () -> Unit
) {
    val keys = listOf(
        KeypadKey("1"), KeypadKey("2", "A B C"), KeypadKey("3", "D E F"),
        KeypadKey("4", "G H I"), KeypadKey("5", "J K L"), KeypadKey("6", "M N O"),
        KeypadKey("7", "P Q R S"), KeypadKey("8", "T U V"), KeypadKey("9", "W X Y Z"),
        KeypadKey("", icon = Icons.Rounded.Fingerprint), KeypadKey("0", "+"), KeypadKey("", icon = Icons.AutoMirrored.Rounded.Backspace)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        keys.chunked(3).forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowKeys.forEach { key ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MidnightSurfaceContainerLow,
                        onClick = {
                            when {
                                key.icon == Icons.Rounded.Fingerprint -> onBiometricClick()
                                key.icon == Icons.AutoMirrored.Rounded.Backspace -> onDeleteClick()
                                key.label.isNotEmpty() -> onDigitClick(key.label)
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (key.icon != null) {
                                Icon(
                                    key.icon,
                                    null,
                                    tint = if (key.icon == Icons.Rounded.Fingerprint) CashInGreen else MidnightOnSurfaceVariant,
                                    modifier = Modifier.size(28.dp)
                                )
                            } else {
                                Text(
                                    text = key.label,
                                    style = Typography.headlineMedium,
                                    color = MidnightOnSurface
                                )
                                if (key.subLabel != null) {
                                    Text(
                                        text = key.subLabel,
                                        style = Typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 2.sp),
                                        color = MidnightOnSurfaceVariant.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityNoticeCard() {
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
                Icon(Icons.Rounded.Shield, null, tint = CashInGreen, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Zero-Knowledge Ledger Architecture", style = Typography.labelLarge, color = MidnightOnSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Local SQLite storage secured with SQLCipher AES-256 GCM. Encryption keys reside strictly inside the hardware Keystore enclave.",
                    style = Typography.bodySmall,
                    color = MidnightOnSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
