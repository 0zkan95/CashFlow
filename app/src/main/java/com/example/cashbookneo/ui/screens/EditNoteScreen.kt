package com.example.cashbookneo.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashbookneo.data.entity.MemoType
import com.example.cashbookneo.ui.components.*
import com.example.cashbookneo.ui.theme.*
import com.example.cashbookneo.viewmodel.EditNoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    viewModel: EditNoteViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val bgSurface = MidnightBackground
    val cardBg = MidnightSurfaceContainerLow
    val emeraldAccent = CashInGreen
    val indigoAccent = MidnightPrimary

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "FINANCIAL NOTE", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                        Text(text = if (uiState.isEditMode) "Edit Memo #${uiState.noteId?.takeLast(4)}" else "New Note", style = Typography.headlineSmall, color = MidnightOnSurface)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.Close, null, tint = MidnightOnSurface)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onPinToggle(!uiState.isPinned) }) {
                        Icon(
                            if (uiState.isPinned) Icons.Rounded.PushPin else Icons.Rounded.PushPin, // Use filled/outlined if available
                            null,
                            tint = if (uiState.isPinned) indigoAccent else MidnightOnSurfaceVariant
                        )
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
                    onClick = { viewModel.saveNote(onBack) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent)
                ) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = CashInOnGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Save Note Record", style = Typography.labelLarge, color = CashInOnGreen)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Discard",
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
                            onClick = { /* New Note Pill - logic in VM if needed */ },
                            shape = RoundedCornerShape(9999.dp),
                            color = if (!uiState.isEditMode) indigoAccent.copy(alpha = 0.15f) else Color.Transparent
                        ) {
                            Text(text = "New Note", modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), style = Typography.labelSmall, color = if (!uiState.isEditMode) indigoAccent else MidnightOnSurfaceVariant)
                        }
                        Surface(
                            onClick = { /* Edit Memo Pill */ },
                            shape = RoundedCornerShape(9999.dp),
                            color = if (uiState.isEditMode) indigoAccent.copy(alpha = 0.15f) else Color.Transparent
                        ) {
                            Text(text = "Edit Memo", modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), style = Typography.labelSmall, color = if (uiState.isEditMode) indigoAccent else MidnightOnSurfaceVariant)
                        }
                    }
                }
            }

            item {
                Text(text = "ARCHETYPE FORMAT", style = Typography.labelSmall, color = MidnightOnSurfaceVariant, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ArchetypeCard("IOU / Debt", Icons.Rounded.Handshake, uiState.noteType == MemoType.IOU, Modifier.weight(1f)) { viewModel.onTypeChange(MemoType.IOU) }
                    ArchetypeCard("Checklist", Icons.Rounded.Checklist, uiState.noteType == MemoType.CHECKLIST, Modifier.weight(1f)) { viewModel.onTypeChange(MemoType.CHECKLIST) }
                    ArchetypeCard("Tax/Receipt", Icons.Rounded.Description, uiState.noteType == MemoType.RECEIPT, Modifier.weight(1f)) { viewModel.onTypeChange(MemoType.RECEIPT) }
                    ArchetypeCard("Memo", Icons.Rounded.Notes, uiState.noteType == null, Modifier.weight(1f)) { viewModel.onTypeChange(MemoType.IOU) } // fallback
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(text = "RECORD TITLE", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = { viewModel.onTitleChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Lent to Marko", color = MidnightOutline) },
                        leadingIcon = { Icon(Icons.Rounded.EditNote, null, tint = MidnightOutline) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardBg,
                            unfocusedContainerColor = cardBg,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            if (uiState.noteType == MemoType.IOU) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f).height(48.dp).clickable { },
                            shape = RoundedCornerShape(12.dp),
                            color = emeraldAccent
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Icon(Icons.Rounded.ArrowOutward, null, tint = CashInOnGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("I Lent (Receivable)", style = Typography.labelSmall, color = CashInOnGreen)
                            }
                        }
                        Surface(
                            modifier = Modifier.weight(1f).height(48.dp).clickable { },
                            shape = RoundedCornerShape(12.dp),
                            color = cardBg
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Icon(Icons.Rounded.SouthWest, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("I Borrowed (Payable)", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(text = "Borrower / Contact Name", style = Typography.labelMedium, color = MidnightOnSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = uiState.contactName,
                            onValueChange = { viewModel.onContactNameChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Rounded.Person, null, tint = MidnightOutline) },
                            trailingIcon = { Icon(Icons.Rounded.ContactPage, null, tint = MidnightOutline) },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = cardBg, unfocusedContainerColor = cardBg)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(text = "Principal Amount", style = Typography.labelMedium, color = MidnightOnSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = uiState.currency, style = Typography.displayLarge, color = emeraldAccent, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            OutlinedTextField(
                                value = uiState.principalAmount,
                                onValueChange = { viewModel.onAmountChange(it) },
                                modifier = Modifier.weight(1f),
                                textStyle = Typography.displayLarge.copy(fontSize = 32.sp, color = Color.White),
                                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("EUR", style = Typography.labelSmall, color = indigoAccent, modifier = Modifier.background(indigoAccent.copy(alpha = 0.15f), RoundedCornerShape(4.dp)).padding(4.dp))
                                Text("USD", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                                Text("RSD", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                            }
                        }
                    }
                }
            }

            if (uiState.noteType == MemoType.CHECKLIST) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(text = "CHECKLIST ITEMS", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        uiState.checklistItems.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = item.isChecked,
                                    onCheckedChange = { viewModel.toggleChecklistItem(item.id) },
                                    colors = CheckboxDefaults.colors(checkedColor = indigoAccent)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = item.label, style = Typography.bodyMedium, color = if (item.isChecked) MidnightOnSurfaceVariant else Color.White, modifier = Modifier.weight(1f))
                                IconButton(onClick = { viewModel.removeChecklistItem(item.id) }) {
                                    Icon(Icons.Rounded.Delete, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        
                        var newItemLabel by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = newItemLabel,
                            onValueChange = { newItemLabel = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Add new item...", color = MidnightOutline) },
                            trailingIcon = {
                                IconButton(onClick = {
                                    if (newItemLabel.isNotBlank()) {
                                        viewModel.addChecklistItem(newItemLabel)
                                        newItemLabel = ""
                                    }
                                }) {
                                    Icon(Icons.Rounded.Add, null, tint = indigoAccent)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = cardBg, unfocusedContainerColor = cardBg)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(text = "FOLDERS & LEDGER CLASSIFICATION", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("#CarRepair", "#Pantry", "#Tax2026", "#PersonalLoan", "+ New Tag").forEach { tag ->
                            Text(
                                text = tag,
                                style = Typography.labelSmall,
                                color = if (tag.startsWith("+")) indigoAccent else MidnightOnSurfaceVariant,
                                modifier = Modifier.background(cardBg, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.PushPin, null, tint = indigoAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Pin to Priority Top", style = Typography.labelLarge, color = MidnightOnSurface, modifier = Modifier.weight(1f))
                        Switch(checked = uiState.isPinned, onCheckedChange = { viewModel.onPinToggle(it) })
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = cardBg
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Lock, null, tint = emeraldAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Local Hardware AES-256", style = Typography.labelSmall, color = Color.White, modifier = Modifier.weight(1f))
                            Text(text = "ENCRYPTED", style = Typography.labelSmall, color = emeraldAccent)
                        }
                    }
                }
            }

            if (uiState.isEditMode) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Archive, null, tint = MidnightOnSurfaceVariant)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Archive to Inactive Ledger", style = Typography.labelLarge, color = MidnightOnSurface)
                            }
                            Switch(checked = uiState.isArchived, onCheckedChange = { viewModel.onArchiveToggle(it) })
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.deleteNote(onBack) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D1010))
                        ) {
                            Icon(Icons.Rounded.DeleteForever, null, tint = CashOutRose)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Delete Financial Note", style = Typography.labelSmall, color = CashOutRose)
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
fun ArchetypeCard(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(80.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MidnightPrimary.copy(alpha = 0.15f) else MidnightSurfaceContainerLow,
        border = if (isSelected) BorderStroke(1.dp, MidnightPrimary) else null
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = if (isSelected) MidnightPrimary else MidnightOnSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, style = Typography.labelSmall, color = if (isSelected) Color.White else MidnightOnSurfaceVariant)
        }
    }
}
