package com.example.cashbookneo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cashbookneo.data.DatabaseMaintenanceService
import com.example.cashbookneo.data.PreferenceManager
import com.example.cashbookneo.model.Account
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val activeAccount: Account,
    val isDarkMode: Boolean = true,
    val accentColorHex: String = "#818CF8",
    val chartType: String = "Donut",
    val compactView: Boolean = false,
    val firstDayOfWeek: String = "Mon",
    val defaultEntryType: String = "Outflow",
    val fiscalMonthStart: Long = 1L,
    val screenMasking: Boolean = true,
    val multiCurrencyConverter: Boolean = true,
    val biometricsActive: Boolean = true,
    val cachedReceiptsSizeMb: Double = 0.0,
    val isRunningVacuum: Boolean = false,
    val statusFeedbackMessage: String? = null,
    val appVersion: String = "v2.4.1 (Build 842 - Stable)"
)

class SettingsViewModel(
    private val activeAccount: Account,
    private val preferenceManager: PreferenceManager,
    private val dbMaintenanceService: DatabaseMaintenanceService
) : ViewModel() {

    private val _statusFeedback = MutableStateFlow<String?>(null)
    private val _isRunningVacuum = MutableStateFlow(false)

    val uiState: StateFlow<SettingsUiState> = combine(
        preferenceManager.isDarkMode,
        preferenceManager.accentColorHex,
        preferenceManager.chartType,
        preferenceManager.compactView,
        preferenceManager.firstDayOfWeek,
        preferenceManager.defaultEntryType,
        preferenceManager.fiscalMonthStart,
        preferenceManager.screenMasking,
        preferenceManager.multiCurrencyConverter,
        preferenceManager.biometricsEnabled,
        _statusFeedback,
        _isRunningVacuum
    ) { flows ->
        SettingsUiState(
            activeAccount = activeAccount,
            isDarkMode = flows[0] as Boolean,
            accentColorHex = flows[1] as String,
            chartType = flows[2] as String,
            compactView = flows[3] as Boolean,
            firstDayOfWeek = flows[4] as String,
            defaultEntryType = flows[5] as String,
            fiscalMonthStart = flows[6] as Long,
            screenMasking = flows[7] as Boolean,
            multiCurrencyConverter = flows[8] as Boolean,
            biometricsActive = flows[9] as Boolean,
            statusFeedbackMessage = flows[10] as? String,
            isRunningVacuum = flows[11] as Boolean,
            cachedReceiptsSizeMb = dbMaintenanceService.getReceiptCacheSize()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState(activeAccount))

    fun updateDarkMode(enabled: Boolean) = viewModelScope.launch { preferenceManager.setDarkMode(enabled) }
    fun updateAccentColor(hex: String) = viewModelScope.launch { preferenceManager.setAccentColor(hex) }
    fun updateChartType(type: String) = viewModelScope.launch { preferenceManager.setChartType(type) }
    fun updateCompactView(enabled: Boolean) = viewModelScope.launch { preferenceManager.setCompactView(enabled) }
    fun updateFirstDayOfWeek(day: String) = viewModelScope.launch { preferenceManager.setFirstDayOfWeek(day) }
    fun updateDefaultEntryType(type: String) = viewModelScope.launch { preferenceManager.setDefaultEntryType(type) }
    fun updateFiscalMonthStart(day: Long) = viewModelScope.launch { preferenceManager.setFiscalMonthStart(day) }
    fun updateScreenMasking(enabled: Boolean) = viewModelScope.launch { preferenceManager.setScreenMasking(enabled) }
    fun updateMultiCurrency(enabled: Boolean) = viewModelScope.launch { preferenceManager.setMultiCurrencyConverter(enabled) }
    fun updateBiometrics(enabled: Boolean) = viewModelScope.launch { preferenceManager.setBiometricsEnabled(enabled) }

    fun onClearCacheClicked() = viewModelScope.launch {
        val bytes = dbMaintenanceService.clearReceiptCache()
        _statusFeedback.value = "Reclaimed ${(bytes / (1024 * 1024)).toInt()} MB"
    }

    fun onIntegrityCheckAndVacuumClicked() = viewModelScope.launch {
        _isRunningVacuum.value = true
        val (success, message) = dbMaintenanceService.performIntegrityCheckAndVacuum()
        _isRunningVacuum.value = false
        _statusFeedback.value = message
    }

    fun clearFeedback() {
        _statusFeedback.value = null
    }

    class Factory(
        private val activeAccount: Account,
        private val preferenceManager: PreferenceManager,
        private val dbMaintenanceService: DatabaseMaintenanceService
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(activeAccount, preferenceManager, dbMaintenanceService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
