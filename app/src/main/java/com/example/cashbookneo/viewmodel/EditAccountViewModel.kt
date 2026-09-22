package com.example.cashbookneo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cashbookneo.data.dao.ProfileDao
import com.example.cashbookneo.data.entity.ProfileEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditAccountUiState(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val gender: String = "Male",
    val baseCurrency: String = "RSD",
    val vaultLabel: String = "Personal Main Ledger",
    val avatarPath: String? = null,
    val isSaving: Boolean = false,
    val hasChanges: Boolean = false,
    val isPrimary: Boolean = true
)

class EditAccountViewModel(
    private val profileId: String,
    private val profileDao: ProfileDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditAccountUiState(id = profileId))
    val uiState: StateFlow<EditAccountUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            profileDao.getProfileById(profileId)?.let { entity ->
                _uiState.update { 
                    it.copy(
                        name = entity.name,
                        email = entity.email ?: "",
                        gender = entity.gender ?: "Male",
                        baseCurrency = entity.baseCurrency,
                        vaultLabel = entity.vaultLabel,
                        avatarPath = entity.avatarPath,
                        isPrimary = entity.id == "acc1" // Simplified for now
                    )
                }
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName, hasChanges = true) }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, hasChanges = true) }
    }

    fun onGenderChange(newGender: String) {
        _uiState.update { it.copy(gender = newGender, hasChanges = true) }
    }

    fun onVaultLabelChange(newLabel: String) {
        _uiState.update { it.copy(vaultLabel = newLabel, hasChanges = true) }
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val profile = ProfileEntity(
                id = s.id,
                name = s.name,
                email = s.email,
                gender = s.gender,
                baseCurrency = s.baseCurrency,
                avatarPath = s.avatarPath,
                vaultLabel = s.vaultLabel,
                isOnboardingCompleted = true
            )
            profileDao.insertProfile(profile)
            _uiState.update { it.copy(isSaving = false, hasChanges = false) }
            onSuccess()
        }
    }

    class Factory(
        private val profileId: String,
        private val profileDao: ProfileDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditAccountViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EditAccountViewModel(profileId, profileDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
