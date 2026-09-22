package com.example.cashbookneo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cashbookneo.model.*
import com.example.cashbookneo.ui.components.*
import com.example.cashbookneo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    activeAccount: Account,
    transaction: Transaction,
    onBack: () -> Unit,
    onEdit: (Transaction) -> Unit = {},
    onSplitBill: (Transaction) -> Unit = {},
    onDuplicate: (Transaction) -> Unit = {},
    onDelete: (Transaction) -> Unit = {},
    onExport: (Transaction) -> Unit = {},
    onAttachNote: (Transaction) -> Unit = {}
) {
    val currencySymbol = activeAccount.currencySymbol
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            DetailHeader(transaction = transaction, currencySymbol = currencySymbol)
        }
        
        item {
            DetailQuickActions(
                onEdit = { onEdit(transaction) },
                onSplitBill = { onSplitBill(transaction) },
                onDuplicate = { onDuplicate(transaction) },
                onDelete = { onDelete(transaction) }
            )
        }
        
        transaction.detail?.let { detail ->
            item {
                ReceiptExtractionCard(detail = detail, currencySymbol = currencySymbol)
            }
            
            item {
                EncryptedProofCard(detail = detail)
            }
            
            item {
                AuditTrailTimeline(events = detail.auditTrail)
            }
        }
        
        item {
            TransactionDetailFooter(
                onExport = { onExport(transaction) },
                onAttachNote = { onAttachNote(transaction) }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionDetailPreview() {
    CashFlowTheme {
        TransactionDetailScreen(
            activeAccount = DummyUser,
            transaction = DummyTransactions[0],
            onBack = {}
        )
    }
}
