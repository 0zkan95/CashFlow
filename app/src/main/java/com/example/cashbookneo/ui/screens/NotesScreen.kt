package com.example.cashbookneo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashbookneo.data.entity.*
import com.example.cashbookneo.navigation.Screen
import com.example.cashbookneo.ui.components.*
import com.example.cashbookneo.ui.theme.*
import com.example.cashbookneo.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val bgSurface = MidnightBackground
    val cardBg = MidnightSurfaceContainerLow
    val emeraldAccent = CashInGreen
    val indigoAccent = MidnightPrimary

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Notes & Memos", style = Typography.headlineSmall, color = Color.White)
                    Text("${uiState.totalCount} Saved Records", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(emeraldAccent.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, null, tint = emeraldAccent, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AES-256", color = emeraldAccent, style = Typography.labelSmall)
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search memos or items...", color = MidnightOutline) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = MidnightOnSurfaceVariant) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = cardBg,
                    unfocusedContainerColor = cardBg,
                    focusedBorderColor = indigoAccent,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.08f)
                ),
                singleLine = true
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NotesFilterTab.entries.forEach { tab ->
                    item {
                        FilterChip(
                            selected = uiState.activeTab == tab,
                            onClick = { viewModel.onTabChanged(tab) },
                            label = { Text(tab.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = indigoAccent,
                                selectedLabelColor = MidnightOnPrimary
                            )
                        )
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionNoteCard(
                    title = "New Memo",
                    icon = Icons.Rounded.EditNote,
                    color = indigoAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Screen.CreateNote.route) }
                )
                QuickActionNoteCard(
                    title = "Checklist",
                    icon = Icons.Default.Checklist,
                    color = emeraldAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Screen.CreateNote.route) } // Placeholder
                )
                QuickActionNoteCard(
                    title = "IOU Tracker",
                    icon = Icons.Default.AccountBalanceWallet,
                    color = Color(0xFFF472B6),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Screen.CreateNote.route) } // Placeholder
                )
            }
        }

        items(uiState.memos) { memo ->
            when (memo.type) {
                MemoType.IOU -> {
                    IouNoteCard(
                        memo = memo,
                        onSettle = { viewModel.settleIOU(memo) },
                        onClick = { onNavigate(Screen.EditNote.createRoute(memo.id)) }
                    )
                }
                MemoType.CHECKLIST -> {
                    ChecklistNoteCard(
                        memo = memo,
                        items = emptyList(), // TODO: Fetch items
                        onToggleItem = { viewModel.toggleChecklistItem(memo.id, it) },
                        onClick = { onNavigate(Screen.EditNote.createRoute(memo.id)) }
                    )
                }
                else -> {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onNavigate(Screen.EditNote.createRoute(memo.id)) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = memo.title, style = Typography.labelLarge, color = Color.White)
                            if (!memo.description.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = memo.description, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
