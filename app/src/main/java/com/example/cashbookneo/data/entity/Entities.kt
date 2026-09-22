package com.example.cashbookneo.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String?,
    val gender: String?,
    @ColumnInfo(name = "base_currency", defaultValue = "RSD") val baseCurrency: String = "RSD",
    @ColumnInfo(name = "avatar_path") val avatarPath: String?,
    @ColumnInfo(name = "vault_label", defaultValue = "Personal Treasury") val vaultLabel: String = "Personal Treasury",
    @ColumnInfo(name = "is_onboarding_completed", defaultValue = "0") val isOnboardingCompleted: Boolean = false
)

enum class CategoryType {
    INCOME, EXPENSE
}

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: CategoryType,
    @ColumnInfo(name = "color_hex") val colorHex: String?,
    @ColumnInfo(name = "icon_name") val iconName: String?
)

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"]
        )
    ],
    indices = [Index("profile_id"), Index("category_id")]
)
data class TransactionEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "profile_id") val profileId: String,
    @ColumnInfo(name = "category_id") val categoryId: String,
    val payee: String,
    val amount: Double,
    @ColumnInfo(name = "is_credit") val isCredit: Boolean,
    val timestamp: Long,
    @ColumnInfo(name = "payment_method") val paymentMethod: String?,
    val memo: String?,
    @ColumnInfo(name = "receipt_image_path") val receiptImagePath: String?,
    @ColumnInfo(name = "sha256_hash") val sha256Hash: String?
)

@Entity(
    tableName = "transaction_line_items",
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transaction_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("transaction_id")]
)
data class TransactionLineItemEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "transaction_id") val transactionId: String,
    val label: String,
    val category: String?,
    val amount: Double
)

@Entity(
    tableName = "transaction_audit_events",
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transaction_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("transaction_id")]
)
data class TransactionAuditEventEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "transaction_id") val transactionId: String,
    val timestamp: Long,
    val title: String,
    val description: String?,
    @ColumnInfo(name = "color_hex") val colorHex: String?
)

enum class AssetType {
    CURRENCY, GOLD, COMMODITY, REAL_ESTATE
}

@Entity(
    tableName = "asset_reserves",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("profile_id")]
)
data class AssetReserveEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "profile_id") val profileId: String,
    val title: String,
    @ColumnInfo(name = "asset_type") val assetType: AssetType?,
    val quantity: Double,
    val unit: String?,
    @ColumnInfo(name = "goal_text") val goalText: String?,
    @ColumnInfo(name = "status_text") val statusText: String?,
    @ColumnInfo(defaultValue = "0.0") val progress: Double = 0.0,
    @ColumnInfo(name = "is_completed", defaultValue = "0") val isCompleted: Boolean = false,
    @ColumnInfo(name = "is_archived", defaultValue = "0") val isArchived: Boolean = false,
    @ColumnInfo(name = "icon_name") val iconName: String = "vault",
    @ColumnInfo(name = "category_color_hex") val categoryColorHex: String?,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)

enum class MemoType {
    IOU, CHECKLIST, RECEIPT, SAVINGS_WATCH
}

@Entity(
    tableName = "financial_memos",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("profile_id")]
)
data class FinancialMemoEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "profile_id") val profileId: String,
    val title: String,
    @ColumnInfo(name = "date_str") val dateStr: String?,
    val type: MemoType?,
    @ColumnInfo(name = "is_pinned", defaultValue = "0") val isPinned: Boolean = false,
    @ColumnInfo(name = "is_archived", defaultValue = "0") val isArchived: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis(),

    // Financial / IOU Fields
    @ColumnInfo(name = "principal_amount") val principalAmount: Double? = null,
    val currency: String? = null,
    @ColumnInfo(name = "contact_name") val contactName: String? = null,
    @ColumnInfo(name = "due_date") val dueDate: Long? = null,
    @ColumnInfo(name = "is_settled", defaultValue = "0") val isSettled: Boolean = false,
    @ColumnInfo(name = "reminder_enabled", defaultValue = "0") val reminderEnabled: Boolean = false,

    val category: String?,
    val description: String?,

    // Metadata / Display
    @ColumnInfo(name = "amount_str") val amountStr: String? = null,
    @ColumnInfo(name = "status_text") val statusText: String? = null,
    @ColumnInfo(name = "auto_deduct") val autoDeduct: Boolean? = null,
    @ColumnInfo(name = "total_est_str") val totalEstStr: String? = null,
    @ColumnInfo(name = "target_str") val targetStr: String? = null
)

@Entity(
    tableName = "checklist_items",
    foreignKeys = [
        ForeignKey(
            entity = FinancialMemoEntity::class,
            parentColumns = ["id"],
            childColumns = ["memo_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("memo_id")]
)
data class ChecklistItemEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "memo_id") val memoId: String,
    val label: String,
    @ColumnInfo(name = "amount_str") val amountStr: String?,
    @ColumnInfo(name = "is_checked", defaultValue = "0") val isChecked: Boolean = false
)
