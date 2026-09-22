package com.example.cashbookneo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cashbookneo.data.dao.FinancialMemoDao
import com.example.cashbookneo.data.entity.*
import com.example.cashbookneo.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class EditNoteUiState(
    val noteId: String? = null,
    val isEditMode: Boolean = false,
    val noteType: MemoType = MemoType.IOU,
    val title: String = "",
    val contactName: String = "",
    val principalAmount: String = "",
    val currency: String = "EUR",
    val isReceivable: Boolean = true,
    val dueDate: Long? = System.currentTimeMillis() + (14L * 24 * 60 * 60 * 1000),
    val reminderEnabled: Boolean = true,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val category: String = "",
    val description: String = "",
    val checklistItems: List<ChecklistItemEntity> = emptyList(),
    val isSaving: Boolean = false,
    val isDeleted: Boolean = false
)

class EditNoteViewModel(
    private val memoDao: FinancialMemoDao,
    private val profileId: String,
    private val initialNoteId: String? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditNoteUiState(noteId = initialNoteId, isEditMode = initialNoteId != null))
    val uiState: StateFlow<EditNoteUiState> = _uiState.asStateFlow()

    init {
        if (initialNoteId != null) {
            loadExistingNote(initialNoteId)
        }
    }

    private fun loadExistingNote(id: String) {
        viewModelScope.launch {
            val entity = memoDao.getMemoById(id) ?: return@launch
            val items = memoDao.getChecklistItemsForMemo(id).first()
            
            _uiState.update { it.copy(
                noteId = entity.id,
                isEditMode = true,
                noteType = entity.type ?: MemoType.IOU,
                title = entity.title,
                contactName = entity.contactName ?: "",
                principalAmount = entity.principalAmount?.toString() ?: "",
                currency = entity.currency ?: "EUR",
                isReceivable = !entity.isSettled,
                dueDate = entity.dueDate,
                reminderEnabled = entity.reminderEnabled,
                isPinned = entity.isPinned,
                isArchived = entity.isArchived,
                category = entity.category ?: "",
                description = entity.description ?: "",
                checklistItems = items
            ) }
        }
    }

    fun onTitleChange(newTitle: String) = _uiState.update { it.copy(title = newTitle) }
    fun onContactNameChange(name: String) = _uiState.update { it.copy(contactName = name) }
    fun onAmountChange(amt: String) = _uiState.update { it.copy(principalAmount = amt) }
    fun onCurrencyChange(curr: String) = _uiState.update { it.copy(currency = curr) }
    fun onTypeChange(type: MemoType) = _uiState.update { it.copy(noteType = type) }
    fun onPinToggle(pinned: Boolean) = _uiState.update { it.copy(isPinned = pinned) }
    fun onArchiveToggle(archived: Boolean) = _uiState.update { it.copy(isArchived = archived) }

    fun addChecklistItem(label: String) {
        val newItem = ChecklistItemEntity(
            id = UUID.randomUUID().toString(),
            memoId = _uiState.value.noteId ?: "",
            label = label,
            amountStr = null,
            isChecked = false
        )
        _uiState.update { it.copy(checklistItems = it.checklistItems + newItem) }
    }

    fun removeChecklistItem(id: String) {
        _uiState.update { it.copy(checklistItems = it.checklistItems.filter { it.id != id }) }
    }

    fun toggleChecklistItem(id: String) {
        _uiState.update { it.copy(checklistItems = it.checklistItems.map { 
            if (it.id == id) it.copy(isChecked = !it.isChecked) else it 
        }) }
    }

    fun saveNote(onSuccess: () -> Unit) {
        val s = _uiState.value
        val amountDouble = s.principalAmount.toDoubleOrNull() ?: 0.0
        val noteId = s.noteId ?: UUID.randomUUID().toString()
        
        val noteEntity = FinancialMemoEntity(
            id = noteId,
            profileId = profileId,
            title = s.title.ifBlank { "Untitled Note" },
            dateStr = "Today",
            type = s.noteType,
            isPinned = s.isPinned,
            isArchived = s.isArchived,
            updatedAt = System.currentTimeMillis(),
            principalAmount = if (amountDouble > 0) amountDouble else null,
            currency = s.currency,
            contactName = s.contactName.ifBlank { null },
            dueDate = s.dueDate,
            reminderEnabled = s.reminderEnabled,
            category = s.category.ifBlank { null },
            description = s.description.ifBlank { null }
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            memoDao.insertFullMemo(noteEntity, s.checklistItems.map { it.copy(memoId = noteId) })
            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }

    fun deleteNote(onSuccess: () -> Unit) {
        val id = _uiState.value.noteId ?: return
        viewModelScope.launch {
            memoDao.deleteMemoById(id)
            _uiState.update { it.copy(isDeleted = true) }
            onSuccess()
        }
    }

    class Factory(
        private val memoDao: FinancialMemoDao,
        private val profileId: String,
        private val initialNoteId: String? = null
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditNoteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EditNoteViewModel(memoDao, profileId, initialNoteId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
