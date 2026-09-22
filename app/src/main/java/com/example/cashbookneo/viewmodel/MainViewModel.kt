package com.example.cashbookneo.viewmodel

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashbookneo.data.PreferenceManager
import com.example.cashbookneo.data.AppDatabase
import com.example.cashbookneo.data.entity.*
import com.example.cashbookneo.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.Instant
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val preferenceManager: PreferenceManager,
    private val database: AppDatabase
) : ViewModel() {
    var hasCompletedOnboarding by mutableStateOf(false)
    var isAppLocked by mutableStateOf(false)
    var userData by mutableStateOf<OnboardingData?>(null)

    private val _activeAccountId = MutableStateFlow<String?>(null)
    val activeAccountId: StateFlow<String?> = _activeAccountId.asStateFlow()

    val accounts: StateFlow<List<Account>> = database.profileDao().getAllProfiles()
        .map { entities ->
            entities.map { entity ->
                Account(
                    id = entity.id,
                    name = entity.name,
                    ledgerType = entity.vaultLabel,
                    currency = entity.baseCurrency,
                    balance = 0.0,
                    email = entity.email ?: ""
                )
            }.ifEmpty { 
                listOf(DummyUser, DummyUser2) 
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf(DummyUser))

    private val _activeAccountFlow = combine(_activeAccountId, accounts) { id, accs ->
        accs.find { it.id == id } ?: accs.firstOrNull() ?: DummyUser
    }
    
    val activeAccountState: StateFlow<Account> = _activeAccountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DummyUser)

    var activeAccount by mutableStateOf(DummyUser)
        private set

    val transactions: StateFlow<List<Transaction>> = activeAccountId
        .flatMapLatest { profileId ->
            val id = profileId ?: "acc1"
            combine(
                database.transactionDao().getTransactionsForProfile(id),
                database.categoryDao().getAllCategories()
            ) { entities, categories ->
                val categoryMap = categories.associateBy { it.id }
                entities.map { entity ->
                    val catEntity = categoryMap[entity.categoryId]
                    Transaction(
                        id = entity.id,
                        accountId = entity.profileId,
                        payee = entity.payee,
                        category = Category(
                            id = entity.categoryId,
                            name = catEntity?.name ?: entity.categoryId.replaceFirstChar { it.uppercase() },
                            color = catEntity?.colorHex?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color.Gray,
                            iconRes = 0
                        ),
                        dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(entity.timestamp), ZoneOffset.UTC),
                        amount = entity.amount,
                        paymentMethod = entity.paymentMethod ?: "Cash",
                        isCredit = entity.isCredit,
                        status = null,
                        attachments = 0,
                        memo = entity.memo,
                        detail = null
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memos: StateFlow<List<FinancialMemo>> = activeAccountId
        .flatMapLatest { profileId ->
            val id = profileId ?: "acc1"
            database.financialMemoDao().getMemosForProfile(id)
                .map { entities ->
                    entities.map { entity ->
                        IOUData(
                            id = entity.id,
                            accountId = entity.profileId,
                            title = entity.title,
                            date = entity.dateStr ?: "",
                            amount = entity.amountStr ?: "0",
                            type = if (entity.principalAmount != null) "Receivable" else "Note",
                            status = entity.statusText ?: "Active",
                            autoDeduct = entity.autoDeduct ?: false
                        )
                    }
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payeeSuggestions: StateFlow<List<String>> = activeAccountId
        .flatMapLatest { profileId ->
            database.transactionDao().getUniquePayeesForProfile(profileId ?: "acc1")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vaults = mutableStateListOf<SavingsVault>().apply { addAll(DummyVaults) }

    var lockTimeoutMs: Long = 0 // 0 means Immediately
    var lastBackgroundTime: Long = 0

    init {
        viewModelScope.launch {
            preferenceManager.isOnboardingCompleted.collectLatest {
                hasCompletedOnboarding = it
            }
        }
        viewModelScope.launch {
            preferenceManager.lockTimeout.collectLatest {
                lockTimeoutMs = it
            }
        }
        viewModelScope.launch {
            preferenceManager.lastBackgroundTime.collectLatest {
                lastBackgroundTime = it
            }
        }
        viewModelScope.launch {
            preferenceManager.activeProfileId.collectLatest { id ->
                _activeAccountId.value = id ?: "acc1"
            }
        }
        viewModelScope.launch {
            activeAccountState.collectLatest { 
                activeAccount = it 
            }
        }
        seedDatabaseIfNeeded()
    }

    private fun seedDatabaseIfNeeded() {
        viewModelScope.launch {
            val profileDao = database.profileDao()
            val categoryDao = database.categoryDao()
            val transactionDao = database.transactionDao()
            val memoDao = database.financialMemoDao()

            if (profileDao.getProfileById("acc1") == null) {
                profileDao.insertProfile(ProfileEntity("acc1", "Hidayet Aslan", "hidayet.a2315@vault.io", "Male", "RSD", null, "Primary Ledger", true))
            }
            if (profileDao.getProfileById("acc2") == null) {
                profileDao.insertProfile(ProfileEntity("acc2", "Business Account", "business@vault.io", "Other", "EUR", null, "Corporate Ledger", true))
            }
            
            val existingCats = categoryDao.getAllCategories().first()
            if (existingCats.isEmpty()) {
                val cats = listOf(
                    CategoryEntity("food", "Food", CategoryType.EXPENSE, "#8B5CF6", "restaurant"),
                    CategoryEntity("utilities", "Utilities", CategoryType.EXPENSE, "#10B981", "bolt"),
                    CategoryEntity("transport", "Transport", CategoryType.EXPENSE, "#3B82F6", "directions_car"),
                    CategoryEntity("shopping", "Shopping", CategoryType.EXPENSE, "#EC4899", "shopping_bag"),
                    CategoryEntity("housing", "Housing", CategoryType.EXPENSE, "#F59E0B", "home"),
                    CategoryEntity("health", "Health", CategoryType.EXPENSE, "#EF4444", "favorite"),
                    CategoryEntity("leisure", "Leisure", CategoryType.EXPENSE, "#8B5CF6", "gamepad"),
                    CategoryEntity("salary", "Salary", CategoryType.INCOME, "#10B981", "payments"),
                    CategoryEntity("investment", "Investment", CategoryType.INCOME, "#8B5CF6", "trending_up"),
                    CategoryEntity("opening_balance", "Opening Balance", CategoryType.INCOME, "#10B981", "account_balance_wallet")
                )
                cats.forEach { categoryDao.insertCategory(it) }
            }

            val txs = transactionDao.getTransactionsForProfile("acc1").first()
            if (txs.isEmpty()) {
                DummyTransactions.forEach { tx ->
                    transactionDao.insertTransaction(
                        TransactionEntity(
                            id = tx.id,
                            profileId = tx.accountId,
                            categoryId = tx.category.id,
                            payee = tx.payee,
                            amount = tx.amount,
                            isCredit = tx.isCredit,
                            timestamp = tx.dateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli(),
                            paymentMethod = tx.paymentMethod,
                            memo = tx.memo,
                            receiptImagePath = null,
                            sha256Hash = tx.detail?.sha256
                        )
                    )
                }
            }

            val existingMemos = memoDao.getMemosForProfile("acc1").first()
            if (existingMemos.isEmpty()) {
                DummyMemos.forEach { memo ->
                    val type = when (memo) {
                        is IOUData -> MemoType.IOU
                        is ChecklistMemo -> MemoType.CHECKLIST
                        is ReceiptMemo -> MemoType.RECEIPT
                        is SavingsWatchMemo -> MemoType.SAVINGS_WATCH
                    }
                    memoDao.upsertMemo(
                        FinancialMemoEntity(
                            id = memo.id,
                            profileId = memo.accountId,
                            title = memo.title,
                            dateStr = memo.date,
                            type = type,
                            isPinned = false,
                            isArchived = false,
                            principalAmount = if (memo is IOUData) parseAmount(memo.amount) else null,
                            currency = if (memo is IOUData) "EUR" else null,
                            contactName = if (memo is IOUData) "Marko" else null,
                            isSettled = false,
                            reminderEnabled = false,
                            category = if (memo is ChecklistMemo) memo.category else null,
                            description = if (memo is SavingsWatchMemo) memo.description else null,
                            amountStr = if (memo is IOUData) memo.amount else null,
                            statusText = if (memo is IOUData) memo.status else null,
                            autoDeduct = if (memo is IOUData) memo.autoDeduct else null,
                            totalEstStr = if (memo is ChecklistMemo) memo.totalEst else null,
                            targetStr = if (memo is SavingsWatchMemo) memo.target else null
                        )
                    )
                }
            }
        }
    }

    fun createProfile(data: OnboardingData) {
        viewModelScope.launch {
            val profileId = UUID.randomUUID().toString()
            val profile = ProfileEntity(
                id = profileId,
                name = data.fullName,
                email = data.email,
                gender = null,
                baseCurrency = data.baseCurrency,
                avatarPath = null,
                vaultLabel = data.vaultLabel,
                isOnboardingCompleted = true
            )
            database.profileDao().insertProfile(profile)
            preferenceManager.setActiveProfileId(profileId)
            _activeAccountId.value = profileId
            
            if (data.startingBalance > 0) {
                addTransaction(Transaction(
                    id = UUID.randomUUID().toString(),
                    accountId = profileId,
                    payee = "Opening Balance",
                    category = Category("opening_balance", "Opening Balance", Color(0xFF10B981), 0),
                    dateTime = LocalDateTime.now(),
                    amount = data.startingBalance,
                    paymentMethod = "System Init",
                    isCredit = true
                ))
            }
        }
    }

    private fun parseAmount(amount: String): Double {
        return amount.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
    }

    fun switchAccount(account: Account) {
        viewModelScope.launch {
            preferenceManager.setActiveProfileId(account.id)
            _activeAccountId.value = account.id
        }
    }

    fun checkLockOnStart() {
        if (hasCompletedOnboarding && (userData?.securityMode == "Biometrics" || isAppLocked)) {
            val now = System.currentTimeMillis()
            if (lockTimeoutMs == 0L || (now - lastBackgroundTime > lockTimeoutMs)) {
                isAppLocked = true
            }
        }
    }

    fun onAppStop() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            lastBackgroundTime = now
            preferenceManager.setLastBackgroundTime(now)
        }
    }

    fun unlock() {
        isAppLocked = false
    }

    fun addTransaction(transaction: Transaction) {
        val accountId = _activeAccountId.value ?: "acc1"
        val tx = transaction.copy(accountId = accountId)
        
        viewModelScope.launch {
            database.transactionDao().insertTransaction(
                TransactionEntity(
                    id = tx.id,
                    profileId = tx.accountId,
                    categoryId = tx.category.id,
                    payee = tx.payee,
                    amount = tx.amount,
                    isCredit = tx.isCredit,
                    timestamp = tx.dateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli(),
                    paymentMethod = tx.paymentMethod,
                    memo = tx.memo,
                    receiptImagePath = null,
                    sha256Hash = tx.detail?.sha256
                )
            )
        }
    }

    fun removeTransaction(transaction: Transaction) {
        viewModelScope.launch {
            database.transactionDao().deleteTransaction(
                TransactionEntity(
                    id = transaction.id,
                    profileId = transaction.accountId,
                    categoryId = transaction.category.id,
                    payee = transaction.payee,
                    amount = transaction.amount,
                    isCredit = transaction.isCredit,
                    timestamp = transaction.dateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli(),
                    paymentMethod = transaction.paymentMethod,
                    memo = transaction.memo,
                    receiptImagePath = null,
                    sha256Hash = transaction.detail?.sha256
                )
            )
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            database.transactionDao().insertTransaction(
                TransactionEntity(
                    id = transaction.id,
                    profileId = transaction.accountId,
                    categoryId = transaction.category.id,
                    payee = transaction.payee,
                    amount = transaction.amount,
                    isCredit = transaction.isCredit,
                    timestamp = transaction.dateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli(),
                    paymentMethod = transaction.paymentMethod,
                    memo = transaction.memo,
                    receiptImagePath = null,
                    sha256Hash = transaction.detail?.sha256
                )
            )
        }
    }
}
