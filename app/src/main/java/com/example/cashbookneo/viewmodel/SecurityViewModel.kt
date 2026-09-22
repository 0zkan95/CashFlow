package com.example.cashbookneo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cashbookneo.data.PreferenceManager
import com.example.cashbookneo.model.Account
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SecurityUiState(
    val activeAccount: Account,
    val isAppLockEnabled: Boolean = true,
    val lockTimeoutMs: Long = 30000L,
    val isBiometricsEnabled: Boolean = true,
    val scrambleKeypad: Boolean = false,
    val lockOnScreenOff: Boolean = true,
    val screenMasking: Boolean = true,
    val pinConfigured: Boolean = true,
    val currentPin: String = "",
    val hasChanges: Boolean = false
)

class SecurityViewModel(
    private val activeAccount: Account,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecurityUiState(activeAccount = activeAccount))
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                preferenceManager.isAppLockEnabled,
                preferenceManager.lockTimeout,
                preferenceManager.biometricsEnabled,
                preferenceManager.scrambleKeypad,
                preferenceManager.lockOnScreenOff,
                preferenceManager.screenMasking
            ) { flows ->
                _uiState.update { 
                    it.copy(
                        isAppLockEnabled = flows[0] as Boolean,
                        lockTimeoutMs = flows[1] as Long,
                        isBiometricsEnabled = flows[2] as Boolean,
                        scrambleKeypad = flows[3] as Boolean,
                        lockOnScreenOff = flows[4] as Boolean,
                        screenMasking = flows[5] as Boolean
                    )
                }
            }.collect()
        }
    }

    fun onAppLockToggle(enabled: Boolean) {
        _uiState.update { it.copy(isAppLockEnabled = enabled, hasChanges = true) }
    }

    fun onTimeoutChange(timeoutMs: Long) {
        _uiState.update { it.copy(lockTimeoutMs = timeoutMs, hasChanges = true) }
    }

    fun onBiometricsToggle(enabled: Boolean) {
        _uiState.update { it.copy(isBiometricsEnabled = enabled, hasChanges = true) }
    }

    fun onScrambleToggle(enabled: Boolean) {
        _uiState.update { it.copy(scrambleKeypad = enabled, hasChanges = true) }
    }

    fun onLockOnScreenOffToggle(enabled: Boolean) {
        _uiState.update { it.copy(lockOnScreenOff = enabled, hasChanges = true) }
    }

    fun onScreenMaskingToggle(enabled: Boolean) {
        _uiState.update { it.copy(screenMasking = enabled, hasChanges = true) }
    }

    fun saveSettings(onSuccess: () -> Unit) {
        val s = _uiState.value
        viewModelScope.launch {
            preferenceManager.setAppLockEnabled(s.isAppLockEnabled)
            preferenceManager.setLockTimeout(s.lockTimeoutMs)
            preferenceManager.setBiometricsEnabled(s.isBiometricsEnabled)
            preferenceManager.setScrambleKeypad(s.scrambleKeypad)
            preferenceManager.setLockOnScreenOff(s.lockOnScreenOff)
            preferenceManager.setScreenMasking(s.screenMasking)
            _uiState.update { it.copy(hasChanges = false) }
            onSuccess()
        }
    }

    class Factory(
        private val activeAccount: Account,
        private val preferenceManager: PreferenceManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SecurityViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SecurityViewModel(activeAccount, preferenceManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
