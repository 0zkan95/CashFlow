package com.example.cashbookneo.security

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class BiometricPromptManager(
    private val activity: AppCompatActivity?
) {
    private val resultChannel = Channel<BiometricResult>()
    val promptResults = resultChannel.receiveAsFlow()

    fun showBiometricPrompt(
        title: String,
        description: String
    ) {
        val activity = activity ?: return
        val manager = BiometricManager.from(activity)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
            .setAllowedAuthenticators(authenticators)
            .build()

        when (manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                resultChannel.trySend(BiometricResult.HardwareUnavailable)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                resultChannel.trySend(BiometricResult.HardwareUnavailable)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                resultChannel.trySend(BiometricResult.AuthenticationNotSet)
                return
            }
            else -> Unit
        }

        val prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    resultChannel.trySend(BiometricResult.AuthenticationError(errString.toString()))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    resultChannel.trySend(BiometricResult.AuthenticationSuccess)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    resultChannel.trySend(BiometricResult.AuthenticationFailed)
                }
            }
        )
        prompt.authenticate(promptInfo)
    }

    sealed interface BiometricResult {
        object HardwareUnavailable : BiometricResult
        object AuthenticationNotSet : BiometricResult
        object AuthenticationSuccess : BiometricResult
        object AuthenticationFailed : BiometricResult
        data class AuthenticationError(val error: String) : BiometricResult
    }
}
