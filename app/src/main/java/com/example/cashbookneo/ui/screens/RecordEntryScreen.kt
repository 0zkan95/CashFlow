package com.example.cashbookneo.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.cashbookneo.model.*
import com.example.cashbookneo.ui.components.*
import com.example.cashbookneo.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordEntryScreen(
    activeAccount: Account,
    transactionToEdit: Transaction? = null,
    initialIsCredit: Boolean = false,
    payeeSuggestions: List<String> = emptyList(),
    onBack: () -> Unit,
    onSave: (Transaction) -> Unit,
    onSaveAndAddAnother: (Transaction) -> Unit = {}
) {
    var isCredit by remember { mutableStateOf(transactionToEdit?.isCredit ?: initialIsCredit) }
    var amount by remember { mutableStateOf(transactionToEdit?.let { String.format(Locale.US, "%.2f", Math.abs(it.amount)) } ?: "0") }
    var selectedCategoryId by remember { mutableStateOf<String?>(transactionToEdit?.category?.id) }
    var payee by remember { mutableStateOf(transactionToEdit?.payee ?: "") }
    var selectedMethod by remember { mutableStateOf(transactionToEdit?.paymentMethod ?: "Cash") }
    var isRepeat by remember { mutableStateOf(false) }
    var dateTime by remember { mutableStateOf(transactionToEdit?.dateTime ?: LocalDateTime.now()) }
    var attachmentUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val context = LocalContext.current
    val currencySymbol = activeAccount.currencySymbol
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            dateTime = dateTime.withHour(hourOfDay).withMinute(minute)
        },
        dateTime.hour,
        dateTime.minute,
        true
    )

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dateTime = dateTime.withYear(year).withMonth(month + 1).withDayOfMonth(dayOfMonth)
            timePickerDialog.show()
        },
        dateTime.year,
        dateTime.monthValue - 1,
        dateTime.dayOfMonth
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { _ -> }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { attachmentUris = attachmentUris + it }
        }
    )

    val debitCategories = listOf(
        EntryCategory("food", "Food &...", Icons.Rounded.Restaurant, Color(0xFF8B5CF6)),
        EntryCategory("utilities", "Utilities", Icons.Rounded.Bolt, Color(0xFF10B981)),
        EntryCategory("transport", "Transport", Icons.Rounded.DirectionsCar, Color(0xFF3B82F6)),
        EntryCategory("shopping", "Shopping", Icons.Rounded.ShoppingBag, Color(0xFFEC4899)),
        EntryCategory("housing", "Housing", Icons.Rounded.Home, Color(0xFFF59E0B)),
        EntryCategory("health", "Health", Icons.Rounded.Favorite, Color(0xFFEF4444)),
        EntryCategory("leisure", "Leisure", Icons.Rounded.Gamepad, Color(0xFF8B5CF6))
    )

    val creditCategories = listOf(
        EntryCategory("salary", "Salary", Icons.Rounded.Payments, Color(0xFF10B981)),
        EntryCategory("investment", "Investment", Icons.AutoMirrored.Rounded.TrendingUp, Color(0xFF8B5CF6)),
        EntryCategory("gift", "Gift", Icons.Rounded.CardGiftcard, Color(0xFFEC4899)),
        EntryCategory("refund", "Refund", Icons.Rounded.SettingsBackupRestore, Color(0xFF3B82F6)),
        EntryCategory("business", "Business", Icons.Rounded.Work, Color(0xFFF59E0B)),
        EntryCategory("other", "Other", Icons.Rounded.MoreHoriz, MidnightOutline)
    )

    val categories = if (isCredit) creditCategories else debitCategories

    fun createTransaction(): Transaction {
        val category = categories.find { it.id == selectedCategoryId } 
            ?: categories.last()
        return Transaction(
            id = transactionToEdit?.id ?: UUID.randomUUID().toString(),
            accountId = activeAccount.id,
            payee = if (payee.isBlank()) "Unspecified" else payee,
            category = Category(category.id, category.name, category.color, 0),
            dateTime = dateTime,
            amount = (amount.toDoubleOrNull() ?: 0.0) * (if (isCredit) 1 else -1),
            paymentMethod = selectedMethod,
            isCredit = isCredit,
            attachments = attachmentUris.size,
            detail = transactionToEdit?.detail
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "LEDGER ENTRY", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                        Text(text = "Record Entry", style = Typography.headlineSmall, color = MidnightOnSurface)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.Close, null, tint = MidnightOnSurface)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Rounded.QrCodeScanner, null, tint = MidnightOnSurface)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Rounded.BookmarkBorder, null, tint = MidnightOnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightBackground)
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(MidnightSurfaceContainerLowest)) {
                NumericKeypad(
                    onDigitClick = { digit ->
                        if (digit == ".") {
                            if (!amount.contains(".")) {
                                amount += "."
                            }
                        } else {
                            if (amount == "0") {
                                amount = digit
                            } else {
                                if (amount.contains(".")) {
                                    val parts = amount.split(".")
                                    if (parts.size > 1 && parts[1].length >= 2) {
                                        // Max 2 decimal places
                                    } else {
                                        amount += digit
                                    }
                                } else {
                                    amount += digit
                                }
                            }
                        }
                    },
                    onDeleteClick = {
                        if (amount.length > 1) {
                            amount = amount.dropLast(1)
                        } else {
                            amount = "0"
                        }
                    }
                )
                
                Surface(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCredit) CashInGreen else CashOutRose
                ) {
                    Button(
                        onClick = {
                            onSave(createTransaction())
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Icon(Icons.Rounded.CheckCircle, null, tint = if (isCredit) CashInOnGreen else Color.White)
                        Spacer(modifier = Modifier.width(12.dp))
                        val cleanAmount = if (amount.endsWith(".")) amount + "00" else amount
                        Text(
                            text = if (isCredit) "Record Inflow +$currencySymbol$cleanAmount" else "Record Outflow -$currencySymbol$cleanAmount",
                            style = Typography.labelLarge,
                            color = if (isCredit) CashInOnGreen else Color.White
                        )
                    }
                }
                
                TextButton(
                    onClick = {
                        onSaveAndAddAnother(createTransaction())
                        // Reset state for next entry
                        amount = "0"
                        payee = ""
                        attachmentUris = emptyList()
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Rounded.Add, null, tint = MidnightOnSurface, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Save & Add Another", style = Typography.labelLarge, color = MidnightOnSurface)
                }
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
                EntryTypeToggle(isCredit = isCredit, onTypeChange = { isCredit = it })
            }
            
            item {
                AmountInputHeader(
                    amount = amount,
                    isCredit = isCredit,
                    currencySymbol = currencySymbol,
                    currencyCode = activeAccount.currency,
                    estimatedBalance = "$currencySymbol ${String.format(Locale.US, "%,.2f", activeAccount.balance + (amount.toDoubleOrNull() ?: 0.0) * (if (isCredit) 1 else -1))}",
                    onCurrencyClick = {}
                )
            }
            
            item {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = MidnightSurfaceContainerLow
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.AccountBalanceWallet, null, tint = MidnightPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Account", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = activeAccount.name.take(8) + "...", style = Typography.labelMedium, color = MidnightOnSurface)
                                    Icon(Icons.Rounded.KeyboardArrowDown, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        modifier = Modifier.weight(1f).clickable { datePickerDialog.show() },
                        shape = RoundedCornerShape(16.dp),
                        color = MidnightSurfaceContainerLow
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.CalendarToday, null, tint = CashInGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Date", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = dateTime.format(dateFormatter), style = Typography.labelMedium, color = MidnightOnSurface)
                                    Icon(Icons.Rounded.CalendarMonth, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Surface(
                        modifier = Modifier.weight(1f).clickable { timePickerDialog.show() },
                        shape = RoundedCornerShape(16.dp),
                        color = MidnightSurfaceContainerLow
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Schedule, null, tint = MidnightPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Time", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = dateTime.format(timeFormatter), style = Typography.labelMedium, color = MidnightOnSurface)
                                    Icon(Icons.Rounded.KeyboardArrowDown, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            
            item {
                CategoryGridPicker(
                    categories = categories,
                    selectedCategoryId = selectedCategoryId,
                    onCategorySelected = { selectedCategoryId = it }
                )
            }
            
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(text = "Payee or Description", style = Typography.labelMedium, color = MidnightOnSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    var expanded by remember { mutableStateOf(false) }
                    val filteredSuggestions = remember(payee) {
                        payeeSuggestions.filter { it.contains(payee, ignoreCase = true) && it != payee }.take(5)
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = payee,
                            onValueChange = { 
                                payee = it
                                expanded = it.isNotEmpty() && filteredSuggestions.isNotEmpty()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("e.g. Lidl Supermarket", color = MidnightOutline) },
                            leadingIcon = { Icon(Icons.Rounded.Store, null, tint = MidnightOutline) },
                            trailingIcon = { if (payee.isNotEmpty()) IconButton(onClick = { payee = ""; expanded = false }) { Icon(Icons.Rounded.Cancel, null, tint = MidnightOutline) } },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MidnightSurfaceContainerLow,
                                focusedContainerColor = MidnightSurfaceContainerLow,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedTextColor = MidnightOnSurface,
                                focusedTextColor = MidnightOnSurface
                            )
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(MidnightSurfaceContainerHigh),
                            properties = PopupProperties(focusable = false)
                        ) {
                            filteredSuggestions.forEach { suggestion ->
                                DropdownMenuItem(
                                    text = { Text(text = suggestion, color = Color.White) },
                                    onClick = {
                                        payee = suggestion
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
                PaymentMethodSelector(
                    selectedMethod = selectedMethod,
                    onMethodSelected = { selectedMethod = it }
                )
            }
            
            item {
                AttachmentSection(
                    files = attachmentUris.map { it.lastPathSegment ?: "image.jpg" },
                    onAttach = { galleryLauncher.launch("image/*") },
                    onCamera = { cameraLauncher.launch(null) },
                    onRemove = { index -> 
                        attachmentUris = attachmentUris.filterIndexed { i, _ -> i != index }
                    }
                )
            }
            
            item {
                RepeatTransactionSwitch(checked = isRepeat, onCheckedChange = { isRepeat = it })
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
