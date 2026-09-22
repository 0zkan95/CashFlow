package com.example.cashbookneo.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.cashbookneo.model.TransactionSearchResult
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object CsvExportHelper {
    fun exportSearchResultsToCsv(context: Context, results: List<TransactionSearchResult>) {
        val fileName = "cashbook_search_export_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault())
        
        file.printWriter().use { writer ->
            // Header Row
            writer.println("ID,Title,Ledger,Amount,Currency,Category,Channel,Timestamp,HasReceipt,Audited")
            // Data Rows
            results.forEach { item ->
                val formattedDate = dateFormatter.format(Instant.ofEpochMilli(item.timestamp))
                writer.println(
                    "\"${item.id}\",\"${item.title}\",\"${item.ledgerName}\",${item.amount}," +
                    "\"${item.currency}\",\"${item.categoryName}\",\"${item.channel}\"," +
                    "\"$formattedDate\",${item.hasReceipt},${item.isAudited}"
                )
            }
        }

        // Launch Share Intent
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Filtered Transactions"))
    }
}
