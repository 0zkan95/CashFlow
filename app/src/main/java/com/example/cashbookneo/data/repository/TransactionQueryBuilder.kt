package com.example.cashbookneo.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.cashbookneo.model.*
import java.time.ZoneOffset

object TransactionQueryBuilder {
    fun buildQuery(filter: SearchFilterState): SimpleSQLiteQuery {
        val conditions = mutableListOf<String>()
        val bindArgs = mutableListOf<Any>()

        // 1. Text Search
        if (filter.searchQuery.isNotBlank()) {
            conditions.add("(t.payee LIKE ? OR t.memo LIKE ? OR c.name LIKE ?)")
            val wildcard = "%${filter.searchQuery.trim()}%"
            bindArgs.add(wildcard)
            bindArgs.add(wildcard)
            bindArgs.add(wildcard)
        }

        // 2. Specific Ledger
        if (filter.selectedLedgerId != null) {
            conditions.add("t.profile_id = ?")
            bindArgs.add(filter.selectedLedgerId)
        }

        // 3. Inflow vs Outflow
        when (filter.flowType) {
            FlowType.INFLOW_ONLY -> {
                conditions.add("t.is_credit = 1")
            }
            FlowType.OUTFLOW_ONLY -> {
                conditions.add("t.is_credit = 0")
            }
            FlowType.ALL -> {}
        }

        // 4. Date Horizon
        val startEpoch = filter.customStartDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        val endEpoch = filter.customEndDate?.atTime(23, 59, 59)?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
        if (startEpoch != null) {
            conditions.add("t.timestamp >= ?")
            bindArgs.add(startEpoch)
        }
        if (endEpoch != null) {
            conditions.add("t.timestamp <= ?")
            bindArgs.add(endEpoch)
        }

        // 5. Amount Spectrum
        conditions.add("t.amount >= ?")
        bindArgs.add(filter.minAmount)
        conditions.add("t.amount <= ?")
        bindArgs.add(filter.maxAmount)

        // 6. Payment Channel
        if (filter.paymentChannel != PaymentChannel.ALL) {
            conditions.add("t.payment_method = ?")
            bindArgs.add(filter.paymentChannel.name)
        }

        // 7. Receipt Toggle
        if (filter.onlyWithReceipt) {
            conditions.add("(t.receipt_image_path IS NOT NULL AND t.receipt_image_path != '')")
        }

        val whereClause = if (conditions.isNotEmpty()) {
            "WHERE " + conditions.joinToString(" AND ")
        } else {
            ""
        }

        val sql = """
            SELECT t.* FROM transactions t
            LEFT JOIN profiles p ON t.profile_id = p.id
            LEFT JOIN categories c ON t.category_id = c.id
            $whereClause
            ORDER BY t.timestamp DESC
        """.trimIndent()

        return SimpleSQLiteQuery(sql, bindArgs.toTypedArray())
    }
}
