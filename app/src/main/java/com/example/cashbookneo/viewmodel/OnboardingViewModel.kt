package com.example.cashbookneo.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay

data class ProfileDraft(
    val fullName: String = "Hidayet Aslan",
    val email: String = "hidayetaslan2315@gmail.com",
    val gender: String = "Male",
    val avatarUri: Uri? = null,
    val vaultLabel: String = "Personal Treasury"
)

class OnboardingViewModel : ViewModel() {
    var profileDraft by mutableStateOf(ProfileDraft())
        private set

    var baseCurrency by mutableStateOf("RSD")
    var trackMetals by mutableStateOf(true)
    
    // Step 2 State
    var securityMode by mutableStateOf("Biometrics")
    var screenMasking by mutableStateOf(true)
    var autoLockTimeout by mutableStateOf("Immediately")
    var userPin by mutableStateOf("")
    
    // Step 3 State
    var ledgerTemplate by mutableStateOf("ESSENTIAL_PRESETS")
    var startingBalance by mutableStateOf("10,000")
    var recordOpeningBalance by mutableStateOf(true)
    var isInitializing by mutableStateOf(false)

    fun updateFullName(name: String) {
        profileDraft = profileDraft.copy(fullName = name)
    }

    fun updateEmail(email: String) {
        profileDraft = profileDraft.copy(email = email)
    }

    fun updateGender(gender: String) {
        profileDraft = profileDraft.copy(gender = gender)
    }
    
    fun updateAvatar(uri: Uri?) {
        profileDraft = profileDraft.copy(avatarUri = uri)
    }
    
    fun updateVaultLabel(label: String) {
        profileDraft = profileDraft.copy(vaultLabel = label)
    }

    fun completeSecuritySetup(mode: String, pin: String) {
        securityMode = mode
        userPin = pin
    }

    fun toggleScreenMasking(enabled: Boolean) {
        screenMasking = enabled
    }

    fun updateLockTimeout(timeout: String) {
        autoLockTimeout = timeout
    }

    suspend fun performVaultInitialization(onComplete: () -> Unit) {
        isInitializing = true
        // Simulate SQLCipher key generation & DB setup
        delay(2000)
        isInitializing = false
        onComplete()
    }
}
