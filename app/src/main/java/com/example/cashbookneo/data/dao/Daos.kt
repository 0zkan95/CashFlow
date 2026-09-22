package com.example.cashbookneo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.cashbookneo.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Update
    suspend fun updateProfile(profile: ProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: ProfileEntity)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE profile_id = :profileId ORDER BY timestamp DESC")
    fun getTransactionsForProfile(profileId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItems(lineItems: List<TransactionLineItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditEvents(events: List<TransactionAuditEventEntity>)

    @Transaction
    suspend fun insertFullTransaction(
        transaction: TransactionEntity,
        lineItems: List<TransactionLineItemEntity>,
        auditEvents: List<TransactionAuditEventEntity>
    ) {
        insertTransaction(transaction)
        insertLineItems(lineItems)
        insertAuditEvents(auditEvents)
    }

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transaction_line_items WHERE transaction_id = :transactionId")
    fun getLineItemsForTransaction(transactionId: String): Flow<List<TransactionLineItemEntity>>

    @Query("SELECT * FROM transaction_audit_events WHERE transaction_id = :transactionId ORDER BY timestamp ASC")
    fun getAuditEventsForTransaction(transactionId: String): Flow<List<TransactionAuditEventEntity>>

    @Query("SELECT DISTINCT payee FROM transactions WHERE profile_id = :profileId")
    fun getUniquePayeesForProfile(profileId: String): Flow<List<String>>
}

@Dao
interface TransactionSearchDao {
    @RawQuery(observedEntities = [TransactionEntity::class])
    fun executeDynamicSearch(query: SupportSQLiteQuery): Flow<List<TransactionEntity>>
}

@Dao
interface AssetReserveDao {
    @Query("SELECT * FROM asset_reserves WHERE profile_id = :profileId ORDER BY updated_at DESC")
    fun getAssetReservesForProfile(profileId: String): Flow<List<AssetReserveEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssetReserve(assetReserve: AssetReserveEntity)

    @Update
    suspend fun updateAssetReserve(assetReserve: AssetReserveEntity)

    @Delete
    suspend fun deleteAssetReserve(assetReserve: AssetReserveEntity)
}

@Dao
interface FinancialMemoDao {
    @Query("SELECT * FROM financial_memos WHERE profile_id = :profileId AND is_archived = 0 ORDER BY is_pinned DESC, updated_at DESC")
    fun getMemosForProfile(profileId: String): Flow<List<FinancialMemoEntity>>

    @Query("SELECT * FROM financial_memos WHERE id = :id LIMIT 1")
    suspend fun getMemoById(id: String): FinancialMemoEntity?

    @Query("SELECT * FROM financial_memos WHERE id = :id LIMIT 1")
    fun observeMemoById(id: String): Flow<FinancialMemoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMemo(memo: FinancialMemoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItems(items: List<ChecklistItemEntity>)

    @Transaction
    suspend fun insertFullMemo(
        memo: FinancialMemoEntity,
        items: List<ChecklistItemEntity>
    ) {
        upsertMemo(memo)
        insertChecklistItems(items)
    }

    @Query("UPDATE financial_memos SET is_archived = :archived, updated_at = :timestamp WHERE id = :memoId")
    suspend fun setArchivedStatus(memoId: String, archived: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE financial_memos SET is_settled = 1, updated_at = :timestamp WHERE id = :memoId")
    suspend fun markSettled(memoId: String, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteMemo(memo: FinancialMemoEntity)

    @Query("DELETE FROM financial_memos WHERE id = :memoId")
    suspend fun deleteMemoById(memoId: String)

    @Query("SELECT * FROM checklist_items WHERE memo_id = :memoId")
    fun getChecklistItemsForMemo(memoId: String): Flow<List<ChecklistItemEntity>>

    @Update
    suspend fun updateChecklistItem(item: ChecklistItemEntity)
}

data class CategoryAggResult(
    val categoryId: String,
    val categoryName: String,
    val colorHex: String?,
    val iconName: String?,
    val totalSpend: Double,
    val count: Int
)

data class MonthTotalResult(
    val totalInflow: Double?,
    val totalOutflow: Double?
)

data class WeeklyOutflowDto(
    val weekNumber: Int,
    val weekTotal: Double
)

@Dao
interface AnalyticsDao {
    @Query("""
        SELECT 
            COALESCE(SUM(CASE WHEN is_credit = 1 THEN amount ELSE 0 END), 0.0) AS totalInflow,
            COALESCE(SUM(CASE WHEN is_credit = 0 THEN ABS(amount) ELSE 0 END), 0.0) AS totalOutflow
        FROM transactions
        WHERE profile_id = :profileId AND timestamp >= :startTimestamp AND timestamp <= :endTimestamp
    """)
    fun getMonthlyTotals(profileId: String, startTimestamp: Long, endTimestamp: Long): Flow<MonthTotalResult>

    @Query("""
        SELECT 
            t.category_id AS categoryId, 
            c.name AS categoryName, 
            c.color_hex AS colorHex, 
            c.icon_name AS iconName, 
            SUM(ABS(t.amount)) AS totalSpend, 
            COUNT(t.id) AS count
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE t.profile_id = :profileId AND t.is_credit = 0 AND t.timestamp >= :startTimestamp AND t.timestamp <= :endTimestamp
        GROUP BY t.category_id
        ORDER BY totalSpend DESC
    """)
    fun getCategoryOutflows(profileId: String, startTimestamp: Long, endTimestamp: Long): Flow<List<CategoryAggResult>>

    @Query("""
        SELECT 
            ((strftime('%d', timestamp / 1000, 'unixepoch') - 1) / 7 + 1) AS weekNumber, 
            SUM(ABS(amount)) AS weekTotal
        FROM transactions
        WHERE profile_id = :profileId AND is_credit = 0 AND timestamp >= :startTimestamp AND timestamp <= :endTimestamp
        GROUP BY weekNumber
        ORDER BY weekNumber ASC
    """)
    suspend fun getWeeklyOutflows(profileId: String, startTimestamp: Long, endTimestamp: Long): List<WeeklyOutflowDto>
}
