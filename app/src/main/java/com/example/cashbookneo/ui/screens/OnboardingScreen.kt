package com.example.cashbookneo.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cashbookneo.model.Category
import com.example.cashbookneo.model.OnboardingData
import com.example.cashbookneo.ui.components.*
import com.example.cashbookneo.ui.theme.*
import com.example.cashbookneo.viewmodel.OnboardingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.appcompat.app.AppCompatActivity
import com.example.cashbookneo.security.BiometricPromptManager
import com.example.cashbookneo.security.CryptoManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingStep1Screen(
    onNavigateToStep2: () -> Unit,
    onRestoreBackup: (Uri) -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val profile = viewModel.profileDraft
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.updateAvatar(uri) }
    )
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> uri?.let { onRestoreBackup(it) } }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Column {
                        Text("CashFlow", style = Typography.labelLarge, color = MidnightOnSurface)
                        Text("Identity & Account", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                    }
                },
                actions = {
                    TextButton(onClick = { 
                        filePickerLauncher.launch(arrayOf("*/*"))
                    }) {
                        Icon(Icons.Rounded.SettingsBackupRestore, null, tint = MidnightPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restore Backup", style = Typography.labelSmall, color = MidnightPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightBackground)
            )
        },
        containerColor = MidnightBackground,
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = {
                        if (profile.fullName.isNotBlank()) {
                            onNavigateToStep2()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MidnightPrimary.copy(alpha = 0.8f)),
                    enabled = profile.fullName.isNotBlank()
                ) {
                    Text("Continue to Security", style = Typography.labelLarge, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            OnboardingProgressIndicator(currentStep = 1, totalSteps = 3, modifier = Modifier.padding(vertical = 16.dp))
            
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.clickable {
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }) {
                            Surface(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                color = MidnightSurfaceContainerHigh
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (profile.avatarUri != null) {
                                        AsyncImage(
                                            model = profile.avatarUri,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text(profile.fullName.take(2).uppercase(), style = Typography.headlineMedium, color = MidnightPrimary)
                                    }
                                }
                            }
                            Surface(
                                modifier = Modifier.align(Alignment.BottomEnd).size(28.dp),
                                shape = CircleShape,
                                color = MidnightSurfaceContainerLow,
                                border = BorderStroke(1.dp, MidnightOutline)
                            ) {
                                Icon(Icons.Rounded.CameraAlt, null, modifier = Modifier.padding(6.dp), tint = MidnightOnSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Primary Account Profile", style = Typography.headlineSmall, color = MidnightOnSurface)
                    }
                }
                
                item {
                    OnboardingTextField("FULL NAME", profile.fullName, { viewModel.updateFullName(it) }, "Enter your name")
                }
                
                item {
                    OnboardingTextField("EMAIL / ACCOUNT IDENTIFIER", profile.email, { viewModel.updateEmail(it) }, "Enter your email", leadingIcon = Icons.Rounded.AlternateEmail)
                }
                
                item {
                    GenderSelector(profile.gender, { viewModel.updateGender(it) })
                }
                
                item {
                    OnboardingTextField("VAULT PROFILE LABEL", profile.vaultLabel, { viewModel.updateVaultLabel(it) }, "Treasury Name", leadingIcon = Icons.Rounded.AccountBalanceWallet)
                }
                
                item {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("BASE OPERATING CURRENCY", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        val currencies = listOf(
                            Triple("$", "USD", "US Dollar"),
                            Triple("€", "EUR", "Euro"),
                            Triple("ДИН", "RSD", "Dinar")
                        )
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            currencies.forEach { (sym, code, label) ->
                                CurrencyCard(sym, code, label, viewModel.baseCurrency == code, { viewModel.baseCurrency = code }, Modifier.weight(1f))
                            }
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingStep2Screen(
    onNavigateToStep3: () -> Unit,
    onBack: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    var isSettingPin by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? AppCompatActivity
    val isPreview = LocalInspectionMode.current
    
    val biometricPromptManager = remember { BiometricPromptManager(activity) }
    val cryptoManager = remember { if (isPreview) null else CryptoManager() }

    LaunchedEffect(biometricPromptManager.promptResults) {
        biometricPromptManager.promptResults.collectLatest { result ->
            if (result is BiometricPromptManager.BiometricResult.AuthenticationSuccess) {
                isSettingPin = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Column {
                        Text("CashFlow", style = Typography.labelLarge, color = MidnightOnSurface)
                        Text("Security & Privacy", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (isSettingPin) isSettingPin = false else onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = MidnightOnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightBackground)
            )
        },
        containerColor = MidnightBackground,
        bottomBar = {
            if (!isSettingPin) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = {
                            if (viewModel.securityMode == "Biometrics" && viewModel.userPin.isEmpty()) {
                                biometricPromptManager.showBiometricPrompt(
                                    "Setup Biometrics",
                                    "Confirm your identity to enable biometric unlock."
                                )
                            } else {
                                onNavigateToStep3()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MidnightPrimary.copy(alpha = 0.8f))
                    ) {
                        val buttonText = if (viewModel.securityMode == "Biometrics" && viewModel.userPin.isEmpty()) "Confirm & Set PIN" else "Continue to Templates"
                        Text(buttonText, style = Typography.labelLarge, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (!isSettingPin) {
                OnboardingProgressIndicator(currentStep = 2, totalSteps = 3, modifier = Modifier.padding(vertical = 16.dp))
                
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(MidnightSurfaceContainerHigh), contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.Shield, null, tint = MidnightPrimary, modifier = Modifier.size(32.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Secure Your Financial Privacy", style = Typography.headlineSmall, color = MidnightOnSurface)
                        }
                    }
                    
                    item {
                        Column {
                            Text("AUTHENTICATION GATE", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                            Spacer(modifier = Modifier.height(12.dp))
                            SecurityOptionCard(
                                "Biometrics + Master PIN",
                                "Instant fingerprint/face unlock on launch. Fallback to 6-digit cryptographic PIN.",
                                Icons.Rounded.Fingerprint,
                                viewModel.securityMode == "Biometrics",
                                { viewModel.securityMode = "Biometrics" },
                                badgeText = "Recommended"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SecurityOptionCard(
                                "Open Ledger (Instant Launch)",
                                "No authentication gate at startup.",
                                Icons.Rounded.Launch,
                                viewModel.securityMode == "Open",
                                { viewModel.securityMode = "Open" }
                            )
                        }
                    }

                    item {
                        Column {
                            Text("PRIVACY SETTINGS", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                            Spacer(modifier = Modifier.height(12.dp))
                            PrivacyToggle(
                                "Screen Masking",
                                "Hide app content in recent apps and prevent screenshots.",
                                Icons.Rounded.VisibilityOff,
                                viewModel.screenMasking,
                                { viewModel.toggleScreenMasking(it) }
                            )
                        }
                    }
                }
            } else {
                Step2PinSetup(
                    pin = viewModel.userPin,
                    onPinChange = { viewModel.userPin = it },
                    onComplete = { 
                        // Simulate encryption of a master key
                        val masterKey = "master-key-seed".toByteArray()
                        cryptoManager?.encrypt(masterKey)
                        isSettingPin = false
                        onNavigateToStep3()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingStep3Screen(
    onComplete: (OnboardingData) -> Unit,
    onBack: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val customCategories = remember { mutableStateListOf<Category>() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Column {
                        Text("CashFlow", style = Typography.labelLarge, color = MidnightOnSurface)
                        Text("Initialization", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !viewModel.isInitializing) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = MidnightOnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightBackground)
            )
        },
        containerColor = MidnightBackground,
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.performVaultInitialization {
                                onComplete(
                                    OnboardingData(
                                        fullName = viewModel.profileDraft.fullName,
                                        email = viewModel.profileDraft.email,
                                        vaultLabel = viewModel.profileDraft.vaultLabel,
                                        securityMode = viewModel.securityMode,
                                        pin = viewModel.userPin,
                                        autoLockTimeout = viewModel.autoLockTimeout,
                                        baseCurrency = viewModel.baseCurrency,
                                        customCategories = customCategories.toList(),
                                        template = viewModel.ledgerTemplate,
                                        startingBalance = viewModel.startingBalance.replace(",", "").toDoubleOrNull() ?: 0.0
                                    )
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MidnightPrimary),
                    enabled = !viewModel.isInitializing
                ) {
                    if (viewModel.isInitializing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Seeding Ledger...", style = Typography.labelLarge, color = Color.White)
                    } else {
                        Text("Initialize Encrypted Vault \uD83D\uDE80", style = Typography.labelLarge, color = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            OnboardingProgressIndicator(currentStep = 3, totalSteps = 3, modifier = Modifier.padding(vertical = 16.dp))
            
            Step3Initialization(
                template = viewModel.ledgerTemplate,
                onTemplateChange = { viewModel.ledgerTemplate = it },
                startingBalance = viewModel.startingBalance,
                onStartingBalanceChange = { viewModel.startingBalance = it },
                recordOpeningBalance = viewModel.recordOpeningBalance,
                onRecordOpeningBalanceChange = { viewModel.recordOpeningBalance = it },
                customCategories = customCategories
            )
        }
    }
}

@Composable
fun Step3Initialization(
    template: String, onTemplateChange: (String) -> Unit,
    startingBalance: String, onStartingBalanceChange: (String) -> Unit,
    recordOpeningBalance: Boolean, onRecordOpeningBalanceChange: (Boolean) -> Unit,
    customCategories: SnapshotStateList<Category>
) {
    var showAddCategory by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color(0xFF8B5CF6)) }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(MidnightSurfaceContainerHigh), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.RocketLaunch, null, tint = MidnightPrimary, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Choose Your Starter Ledger", style = Typography.headlineSmall, color = MidnightOnSurface)
            }
        }
        
        item {
            Column {
                Text("LEDGER TEMPLATE", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                TemplateCard(
                    "Essential Ledger",
                    "Injects everyday categories.",
                    template == "ESSENTIAL_PRESETS",
                    { onTemplateChange("ESSENTIAL_PRESETS") },
                    badgeText = "Recommended",
                    tags = listOf("Food & Dining" to Icons.Rounded.Restaurant)
                )
                Spacer(modifier = Modifier.height(12.dp))
                TemplateCard(
                    "Blank Ledger",
                    "Start with zero categories and data.",
                    template == "BLANK",
                    { onTemplateChange("BLANK") },
                    tags = listOf("Clean Slate" to Icons.Rounded.LayersClear)
                )
                Spacer(modifier = Modifier.height(12.dp))
                TemplateCard(
                    "Demo Sandbox",
                    "Populates ledger with dummy data for testing.",
                    template == "DEMO_SANDBOX",
                    { onTemplateChange("DEMO_SANDBOX") },
                    tags = listOf("Sample Data" to Icons.Rounded.Science)
                )
            }
        }

        item {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("CUSTOM CATEGORIES", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                    TextButton(onClick = { showAddCategory = true }) {
                        Text("Add Custom", style = Typography.labelSmall, color = MidnightPrimary)
                    }
                }
                
                customCategories.forEach { category ->
                    Text(category.name, color = category.color)
                }
            }
        }
        
        item {
            Surface(shape = RoundedCornerShape(24.dp), color = MidnightSurfaceContainerLow) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("INITIAL BALANCE", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(startingBalance, style = Typography.displaySmall, color = MidnightOnSurface)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Record opening balance entry", style = Typography.labelLarge, color = MidnightOnSurface)
                        }
                        Switch(checked = recordOpeningBalance, onCheckedChange = onRecordOpeningBalanceChange)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("0", "5,000", "10,000", "50,000").forEach { valAmount ->
                            val isSelected = startingBalance == valAmount
                            Surface(
                                onClick = { onStartingBalanceChange(valAmount) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MidnightPrimary.copy(alpha = 0.8f) else MidnightSurfaceContainerHigh
                            ) {
                                Text(
                                    text = valAmount,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = Typography.labelSmall,
                                    color = if (isSelected) Color.White else MidnightOnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddCategory) {
        AlertDialog(
            onDismissRequest = { showAddCategory = false },
            confirmButton = {
                Button(onClick = {
                    if (newCategoryName.isNotBlank()) {
                        customCategories.add(Category(newCategoryName.lowercase(), newCategoryName, selectedColor, 0))
                        newCategoryName = ""
                        showAddCategory = false
                    }
                }) { Text("Add") }
            },
            title = { Text("Add Category") },
            text = {
                TextField(value = newCategoryName, onValueChange = { newCategoryName = it })
            }
        )
    }
}

@Composable
fun Step2PinSetup(
    pin: String,
    onPinChange: (String) -> Unit,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        PinDotIndicator(pinLength = pin.length, maxDigits = 6)
        Spacer(modifier = Modifier.weight(1f))
        LockKeypad(
            onDigitClick = { digit ->
                if (pin.length < 6) {
                    val newPin = pin + digit
                    onPinChange(newPin)
                    if (newPin.length == 6) onComplete()
                }
            },
            onDeleteClick = { if (pin.isNotEmpty()) onPinChange(pin.dropLast(1)) },
            onBiometricClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingStep2Preview() {
    CashFlowTheme {
        OnboardingStep2Screen(onNavigateToStep3 = {}, onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingStep3Preview() {
    CashFlowTheme {
        OnboardingStep3Screen(onComplete = {}, onBack = {})
    }
}
