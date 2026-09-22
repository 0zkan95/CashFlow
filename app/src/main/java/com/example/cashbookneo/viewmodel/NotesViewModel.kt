package com.example.cashbookneo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cashbookneo.data.dao.FinancialMemoDao
import com.example.cashbookneo.data.entity.*
import com.example.cashbookneo.data.repository.TransactionRepository
import com.example.cashbookneo.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class NotesFilterTab { ALL, IOU, CHECKLISTS, ARCHIVE }

data class NotesUiState(
    val activeTab: NotesFilterTab = NotesFilterTab.ALL,
    val searchQuery: String = "",
    val totalCount: Int = 0,
    val memos: List<FinancialMemoEntity> = emptyList(),
    val isLoading: Boolean = false
)

class NotesViewModel(
    private val profileId: String,
    private val memoDao: FinancialMemoDao,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _activeTab = MutableStateFlow(NotesFilterTab.ALL)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<NotesUiState> = combine(
        memoDao.getMemosForProfile(profileId),
        _activeTab,
        _searchQuery
    ) { notes, tab, query ->
        val filtered = notes.filter { note ->
            val matchesQuery = query.isBlank() || note.title.contains(query, ignoreCase = true)
            val matchesTab = when (tab) {
                NotesFilterTab.ALL -> true
                NotesFilterTab.IOU -> note.type == MemoType.IOU
                NotesFilterTab.CHECKLISTS -> note.type == MemoType.CHECKLIST
                NotesFilterTab.ARCHIVE -> false // Placeholder
            }
            matchesQuery && matchesTab
        }
        NotesUiState(
            activeTab = tab,
            searchQuery = query,
            totalCount = notes.size,
            memos = filtered,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotesUiState(isLoading = true)
    )

    fun onTabChanged(tab: NotesFilterTab) {
        _activeTab.value = tab
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun settleIOU(note: FinancialMemoEntity) {
        viewModelScope.launch {
            memoDao.markSettled(note.id)
            // Bridge to transactions could be added here
        }
    }

    fun toggleChecklistItem(memoId: String, item: ChecklistItemEntity) {
        viewModelScope.launch {
            memoDao.updateChecklistItem(item.copy(isChecked = !item.isChecked))
        }
    }

    class Factory(
        private val profileId: String,
        private val memoDao: FinancialMemoDao,
        private val transactionRepository: TransactionRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NotesViewModel(profileId, memoDao, transactionRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
