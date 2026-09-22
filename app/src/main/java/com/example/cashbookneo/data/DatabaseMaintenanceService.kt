package com.example.cashbookneo.data

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class DatabaseMaintenanceService(
    private val db: SupportSQLiteDatabase,
    private val context: Context
) {
    suspend fun performIntegrityCheckAndVacuum(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val cursor = db.query("PRAGMA integrity_check;")
            var result = "ok"
            if (cursor.moveToFirst()) {
                result = cursor.getString(0)
            }
            cursor.close()
            if (result.equals("ok", ignoreCase = true)) {
                db.execSQL("VACUUM;")
                Pair(true, "Integrity verified. Database pages vacuumed successfully.")
            } else {
                Pair(false, "Integrity check failed: $result")
            }
        } catch (e: Exception) {
            Pair(false, e.localizedMessage ?: "Unknown SQLite maintenance error")
        }
    }

    suspend fun clearReceiptCache(): Long = withContext(Dispatchers.IO) {
        val cacheFolder = File(context.cacheDir, "receipt_temp")
        if (!cacheFolder.exists()) return@withContext 0L
        var bytesDeleted = 0L
        cacheFolder.listFiles()?.forEach { file ->
            bytesDeleted += file.length()
            file.delete()
        }
        bytesDeleted
    }
    
    fun getReceiptCacheSize(): Double {
        val cacheFolder = File(context.cacheDir, "receipt_temp")
        if (!cacheFolder.exists()) return 0.0
        var totalBytes = 0L
        cacheFolder.listFiles()?.forEach { totalBytes += it.length() }
        return totalBytes / (1024.0 * 1024.0)
    }
}
