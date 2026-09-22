package com.example.cashbookneo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashbookneo.ui.components.*
import com.example.cashbookneo.ui.theme.*
import com.example.cashbookneo.viewmodel.EditAccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountScreen(
    viewModel: EditAccountViewModel,
    onBack: () -> Unit,
    onNavigateToSecurity: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val cardBg = MidnightSurfaceContainerLow
    val cardBorder = Color.White.copy(alpha = 0.08f)
    val indigoAccent = MidnightPrimary
    val emeraldAccent = CashInGreen

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = MidnightOnSurface)
                    }
                },
                actions = {
                    if (uiState.hasChanges) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(Color(0xFFFBBF24).copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFFBBF24)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Unsaved Changes", color = Color(0xFFFBBF24), style = Typography.labelSmall)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Button(
                        onClick = { viewModel.saveProfile(onBack) },
                        colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Save", color = CashInOnGreen, style = Typography.labelLarge)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightBackground)
            )
        },
        containerColor = MidnightBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Hero Section
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = CircleShape,
                            color = MidnightSurfaceContainerHigh,
                            border = BorderStroke(2.dp, indigoAccent.copy(alpha = 0.3f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = uiState.name.take(2).uppercase(),
                                    style = Typography.displaySmall.copy(fontSize = 32.sp),
                                    color = Color.White
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.size(32.dp).offset(x = (-4).dp, y = (-4).dp),
                            shape = CircleShape,
                            color = emeraldAccent
                        ) {
                            Icon(Icons.Rounded.CameraAlt, null, tint = CashInOnGreen, modifier = Modifier.padding(6.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = uiState.name, style = Typography.headlineSmall, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Rounded.CheckCircle, null, tint = emeraldAccent, modifier = Modifier.size(18.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(emeraldAccent.copy(alpha = 0.12f))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(emeraldAccent))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Active Primary Account", color = emeraldAccent, style = Typography.labelSmall)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Key, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "AES-256 Ledger ID: CB-8924-X9F", color = MidnightOnSurfaceVariant, style = Typography.labelSmall)
                    }
                }
            }

            // Section: Personal Information
            item {
                EditSection(title = "Personal Information", badge = "Identity", icon = Icons.Rounded.Person) {
                    EditLabel("Full Name")
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = { viewModel.onNameChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Rounded.Badge, null, tint = MidnightOutline) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = cardBg, unfocusedContainerColor = cardBg)
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        EditLabel("Email Address")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.CheckCircle, null, tint = emeraldAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Verified", color = emeraldAccent, style = Typography.labelSmall)
                        }
                    }
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Rounded.AlternateEmail, null, tint = MidnightOutline) },
                        trailingIcon = { Icon(Icons.Rounded.Lock, null, tint = MidnightOutline, modifier = Modifier.size(18.dp)) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = cardBg, unfocusedContainerColor = cardBg)
                    )
                    Text("Linked to master recovery & multi-ledger encryption keys.", style = Typography.bodySmall, color = MidnightOnSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    EditLabel("Honorific / Gender Presentation")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GenderPill("Male", uiState.gender == "Male", Modifier.weight(1f)) { viewModel.onGenderChange("Male") }
                        GenderPill("Female", uiState.gender == "Female", Modifier.weight(1f)) { viewModel.onGenderChange("Female") }
                        GenderPill("Prefer not to say", uiState.gender == "Other", Modifier.weight(1.5f)) { viewModel.onGenderChange("Other") }
                    }
                }
            }

            // Section: Ledger & Currency Settings
            item {
                EditSection(title = "Ledger & Currency Settings", badge = "2 Ledgers Active", icon = Icons.Rounded.AccountBalanceWallet) {
                    CurrencyRow("RSD", "Serbian Dinar", "Primary Operating Currency", "дин")
                    HorizontalDivider(color = cardBorder, modifier = Modifier.padding(vertical = 12.dp))
                    CurrencyRow("EUR", "Euro", "Secondary Analytics Benchmark", "€")
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    EditLabel("Default Ledger Name")
                    OutlinedTextField(
                        value = uiState.vaultLabel,
                        onValueChange = { viewModel.onVaultLabelChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Rounded.Book, null, tint = MidnightOutline) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = cardBg, unfocusedContainerColor = cardBg)
                    )
                }
            }

            // Section: Security & Sync
            item {
                EditSection(title = "Security & Sync", badge = "Protected", icon = Icons.Rounded.Shield) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToSecurity() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(12.dp), color = MidnightSurfaceContainerHigh) {
                            Icon(Icons.Rounded.Cloud, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.padding(10.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Google Drive Sync", style = Typography.labelLarge, color = Color.White)
                            Text("Auto-backed 18 mins ago", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                        }
                        SettingsActionButton("Manage", onClick = {})
                    }
                    
                    HorizontalDivider(color = cardBorder, modifier = Modifier.padding(vertical = 16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(12.dp), color = MidnightSurfaceContainerHigh) {
                            Icon(Icons.Rounded.Fingerprint, null, tint = emeraldAccent, modifier = Modifier.padding(10.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Biometric Profile Lock", style = Typography.labelLarge, color = Color.White)
                            Text("Require fingerprint when switching", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                        }
                        Switch(checked = true, onCheckedChange = {}, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = emeraldAccent))
                    }
                    
                    HorizontalDivider(color = cardBorder, modifier = Modifier.padding(vertical = 16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(12.dp), color = MidnightSurfaceContainerHigh) {
                            Icon(Icons.Rounded.Engineering, null, tint = emeraldAccent, modifier = Modifier.padding(10.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Hardware Backed Keystore", style = Typography.labelLarge, color = Color.White)
                            Text("Android StrongBox TEE • Key Level 3 Certified", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                        }
                    }
                }
            }

            // Section: Data & Lifecycle Operations
            item {
                EditSection(title = "Data & Lifecycle Operations", icon = Icons.Rounded.Warning, color = Color(0xFFF87171).copy(alpha = 0.8f)) {
                    DataOperationRow(Icons.Rounded.FileDownload, "Export Account Data & Keys", "Encrypted JSON & CSV ledger bundle")
                    HorizontalDivider(color = cardBorder, modifier = Modifier.padding(vertical = 12.dp))
                    DataOperationRow(Icons.Rounded.RecentActors, "Switch to Another Profile", "Open profile switcher sheet")
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D1010)),
                        border = BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Rounded.DeleteForever, null, tint = Color(0xFFF87171))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Delete or Reset Account", color = Color(0xFFF87171), style = Typography.labelSmall)
                    }
                    Text(
                        text = "Permanently purges local key storage and clears all synchronized cache indexes for this profile.",
                        style = Typography.bodySmall,
                        color = MidnightOnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }

            // Footer Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("Cancel", style = Typography.labelLarge)
                    }
                    Button(
                        onClick = { viewModel.saveProfile(onBack) },
                        modifier = Modifier.weight(1.5f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent)
                    ) {
                        Icon(Icons.Rounded.Save, null, tint = CashInOnGreen)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Save Account Changes", color = CashInOnGreen, style = Typography.labelLarge)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun EditSection(
    title: String,
    badge: String? = null,
    icon: ImageVector? = null,
    color: Color = CashInGreen,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(top = 32.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = title, style = Typography.headlineSmall, color = MidnightOnSurface)
            }
            if (badge != null) {
                Text(text = badge, style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                content()
            }
        }
    }
}

@Composable
fun EditLabel(text: String) {
    Text(
        text = text,
        style = Typography.labelSmall,
        color = MidnightOnSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun GenderPill(label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(99.dp),
        color = if (isSelected) MidnightPrimary.copy(alpha = 0.2f) else MidnightSurfaceContainerHigh,
        border = if (isSelected) BorderStroke(1.dp, MidnightPrimary) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = label, style = Typography.labelSmall, color = if (isSelected) MidnightPrimary else MidnightOnSurfaceVariant)
        }
    }
}

@Composable
fun CurrencyRow(code: String, name: String, status: String, symbol: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = MidnightSurfaceContainerHigh) {
            Box(contentAlignment = Alignment.Center) {
                Text(code, style = Typography.labelSmall, color = CashInGreen, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = Typography.labelLarge, color = Color.White)
            Text(status, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(symbol, style = Typography.labelLarge, color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = MidnightOnSurfaceVariant)
        }
    }
}

@Composable
fun DataOperationRow(icon: ImageVector, title: String, subtitle: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(12.dp), color = MidnightSurfaceContainerHigh) {
            Icon(icon, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.padding(10.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = Typography.labelLarge, color = Color.White)
            Text(subtitle, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
        }
        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = MidnightOnSurfaceVariant)
    }
}
