package com.example.cashbookneo

import android.app.Application
import net.sqlcipher.database.SQLiteDatabase

class CashBookApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize SQLCipher early to avoid blocking the main thread during UI setup
        SQLiteDatabase.loadLibs(this)
    }
}
