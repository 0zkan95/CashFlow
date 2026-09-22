package com.example.cashbookneo.data.repository

import com.example.cashbookneo.data.AppDatabase
import com.example.cashbookneo.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(private val database: AppDatabase) {

    fun searchTransactions(filter: SearchFilterState): Flow<List<TransactionSearchResult>> {
        val query = TransactionQueryBuilder.buildQuery(filter)
        return database.transactionSearchDao().executeDynamicSearch(query).map { entities ->
            entities.map { entity ->
                // Map entity to result
                // Note: We might need to fetch ledger and category names if not already in entity
                // For now, let's keep it simple or fetch via separate query if needed.
                // Since RawQuery here only returns TransactionEntity, we might need a better mapping or join.
                
                TransactionSearchResult(
                    id = entity.id,
                    title = entity.payee,
                    ledgerName = "Primary Vault", // Placeholder
                    amount = entity.amount,
                    flowType = if (entity.isCredit) FlowType.INFLOW_ONLY else FlowType.OUTFLOW_ONLY,
                    currency = "USD", // Placeholder
                    categoryName = "General", // Placeholder
                    channel = PaymentChannel.ALL, // Placeholder
                    timestamp = entity.timestamp,
                    hasReceipt = !entity.receiptImagePath.isNullOrBlank(),
                    isAudited = !entity.sha256Hash.isNullOrBlank(),
                    iconName = "receipt"
                )
            }
        }
    }
}
