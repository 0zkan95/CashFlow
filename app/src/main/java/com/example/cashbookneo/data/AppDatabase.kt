package com.example.cashbookneo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cashbookneo.data.dao.*
import com.example.cashbookneo.data.entity.*
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [
        ProfileEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        TransactionLineItemEntity::class,
        TransactionAuditEventEntity::class,
        AssetReserveEntity::class,
        FinancialMemoEntity::class,
        ChecklistItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun transactionSearchDao(): TransactionSearchDao
    abstract fun assetReserveDao(): AssetReserveDao
    abstract fun financialMemoDao(): FinancialMemoDao
    abstract fun analyticsDao(): AnalyticsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, passphrase: ByteArray): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Initialize SQLCipher
                SQLiteDatabase.loadLibs(context)
                
                val factory = SupportFactory(passphrase)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cashflow.db"
                )
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration() // For development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
