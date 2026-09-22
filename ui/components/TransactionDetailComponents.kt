package com.example.cashbookneo.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashbookneo.model.*
import com.example.cashbookneo.ui.theme.*
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DetailHeader(transaction: Transaction, currencySymbol: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(MidnightSurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.ShoppingCart, null, tint = transaction.category.color, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(transaction.payee, style = Typography.headlineSmall, color = MidnightOnSurface)
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(shape = RoundedCornerShape(99.dp), color = CashOutRose.copy(alpha = 0.12f)) {
                            Text("● Outflow", style = Typography.labelSmall, color = CashOutRose, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                        }
                    }
                    Text(transaction.detail?.subtitle ?: "", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(verticalAlignment = Alignment.Bottom) {
                Text(if (transaction.isCredit) "+" else "-", style = Typography.displayMedium, color = MidnightOnSurface, fontWeight = FontWeight.Light)
                Text(currencySymbol, style = Typography.headlineMedium, color = MidnightOnSurface, modifier = Modifier.padding(bottom = 8.dp, end = 4.dp))
                Text(String.format(Locale.US, "%,.2f", Math.abs(transaction.amount)), style = Typography.displayMedium, color = MidnightOnSurface, fontWeight = FontWeight.Bold)
            }
            
            Text(
                transaction.dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy • HH:mm a (EEEE)", Locale.US)),
                style = Typography.bodySmall,
                color = MidnightOnSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF10B981).copy(alpha = 0.05f),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.VerifiedUser, null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Audited & Cryptographically Signed", style = Typography.labelSmall, color = Color(0xFF10B981), modifier = Modifier.weight(1f))
                    Icon(Icons.Rounded.Lock, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(14.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("ASSIGNED LEDGER", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                    Text(transaction.detail?.assignedLedger ?: "", style = Typography.bodySmall, color = MidnightOnSurface)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("SETTLEMENT", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                    Text(transaction.detail?.settlementStatus ?: "", style = Typography.bodySmall, color = Color(0xFF10B981))
                }
            }
        }
    }
}

@Composable
fun DetailQuickActions(
    onEdit: () -> Unit = {},
    onSplitBill: () -> Unit = {},
    onDuplicate: () -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DetailActionButton(Icons.Rounded.Edit, "Edit", Modifier.weight(1f), onClick = onEdit)
        DetailActionButton(Icons.Rounded.CallSplit, "Split Bill", Modifier.weight(1f), onClick = onSplitBill)
        DetailActionButton(Icons.Rounded.FileCopy, "Duplicate", Modifier.weight(1f), onClick = onDuplicate)
        DetailActionButton(Icons.Rounded.DeleteOutline, "Void / Trash", Modifier.weight(1f), color = CashOutRose, onClick = onDelete)
    }
}

@Composable
fun DetailActionButton(icon: ImageVector, label: String, modifier: Modifier = Modifier, color: Color = MidnightOnSurface, onClick: () -> Unit = {}) {
    Surface(
        modifier = modifier.height(84.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MidnightSurfaceContainerLow
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = Typography.labelSmall.copy(fontSize = 10.sp), color = color, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ReceiptExtractionCard(detail: TransactionDetail, currencySymbol: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.QrCodeScanner, null, tint = MidnightOnSurfaceVariant)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Receipt Extraction", style = Typography.headlineSmall, color = MidnightOnSurface)
                Spacer(modifier = Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF10B981).copy(alpha = 0.12f)) {
                    Text("${detail.items.size} items parsed", style = Typography.labelSmall, color = Color(0xFF10B981), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            detail.items.forEach { item ->
                Row(modifier = Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.label, style = Typography.bodyLarge, color = MidnightOnSurface)
                        Text(item.category, style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
                    }
                    Text(String.format(Locale.US, "%s%,.2f", currencySymbol, item.amount), style = Typography.bodyLarge, color = MidnightOnSurface, fontWeight = FontWeight.Bold)
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.White.copy(alpha = 0.05f))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                Text(String.format(Locale.US, "%s%,.2f", currencySymbol, detail.subtotal), style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
            }
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tax (VAT 20% incl.)", style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                Text(String.format(Locale.US, "%s%,.2f", currencySymbol, detail.tax), style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Discounts Applied", style = Typography.bodySmall, color = Color(0xFF10B981))
                Text(String.format(Locale.US, "-%s%,.2f", currencySymbol, detail.discounts), style = Typography.bodySmall, color = Color(0xFF10B981))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Total Settled", style = Typography.headlineSmall, color = MidnightOnSurface)
                Text(String.format(Locale.US, "-%s%,.2f", currencySymbol, detail.subtotal), style = Typography.headlineSmall, color = CashOutRose, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EncryptedProofCard(detail: TransactionDetail, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Shield, null, tint = MidnightOnSurfaceVariant)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Encrypted Proof of Payment", style = Typography.headlineSmall, color = MidnightOnSurface, modifier = Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(99.dp), color = MidnightSurfaceContainerHigh) {
                    Text("AES-256 GCM", style = Typography.labelSmall.copy(fontSize = 10.sp), color = MidnightOnSurfaceVariant, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
            ) {
                // Placeholder for receipt image
                Icon(
                    Icons.Rounded.ReceiptLong,
                    null,
                    tint = MidnightOnSurfaceVariant.copy(alpha = 0.2f),
                    modifier = Modifier.size(100.dp).align(Alignment.Center)
                )
                
                Column(
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.ZoomIn, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tap to view original high-res", style = Typography.labelSmall, color = Color.White)
                    }
                }
                Text("1.4 MB", style = Typography.labelSmall, color = Color.White.copy(alpha = 0.6f), modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Fingerprint, null, tint = MidnightOnSurfaceVariant, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("SHA-256: ${detail.sha256}", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Icon(Icons.Rounded.CheckCircle, null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Stored locally in SQLite vault • Zero cloud leak", style = Typography.labelSmall, color = Color(0xFF10B981))
            }
        }
    }
}

@Composable
fun AuditTrailTimeline(events: List<AuditEvent>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.History, null, tint = MidnightOnSurfaceVariant)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Audit Trail & Event Log", style = Typography.headlineSmall, color = MidnightOnSurface)
                Spacer(modifier = Modifier.weight(1f))
                Text("${events.size} Events", style = Typography.labelSmall, color = MidnightOnSurfaceVariant)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            events.forEachIndexed { index, event ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(event.color)
                        )
                        if (index < events.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(60.dp)
                                    .background(Color.White.copy(alpha = 0.05f))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.padding(bottom = 24.dp)) {
                        Text(
                            event.dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy • HH:mm", Locale.US)),
                            style = Typography.labelSmall,
                            color = MidnightOnSurfaceVariant
                        )
                        Text(event.title, style = Typography.labelLarge, color = MidnightOnSurface)
                        Text(event.description, style = Typography.bodySmall, color = MidnightOnSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionDetailFooter(
    onExport: () -> Unit = {},
    onAttachNote: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Button(
            onClick = onExport,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MidnightPrimaryContainer)
        ) {
            Icon(Icons.Rounded.PictureAsPdf, null, tint = MidnightOnPrimaryContainer)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Export Tax Receipt (PDF / JSON)", color = MidnightOnPrimaryContainer)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextButton(
            onClick = onAttachNote,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Rounded.NoteAdd, null, tint = MidnightOnSurfaceVariant)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Attach Internal Note or Audit Memo", color = MidnightOnSurfaceVariant)
        }
    }
}
