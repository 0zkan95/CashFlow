package com.example.cashbookneo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.cashbookneo.model.DummyVaults
import com.example.cashbookneo.model.SavingsVault
import java.util.Locale

class SavingsViewModel : ViewModel() {
    var selectedAssetFilter by mutableStateOf("All Assets")
    var selectedPeriodFilter by mutableStateOf("1M")
    
    private val _vaults = mutableStateListOf<SavingsVault>().apply { addAll(DummyVaults) }
    val vaults: List<SavingsVault> get() = _vaults

    var selectedVaultId by mutableStateOf<String?>(null)
    
    // Mock rates
    val goldPricePerGram = 87.10
    val eurToUsd = 1.08

    fun getTrendData(): List<Float> {
        return when (selectedAssetFilter) {
            "Fiat" -> listOf(0.4f, 0.5f, 0.45f, 0.6f, 0.7f, 0.8f)
            "Gold" -> listOf(0.2f, 0.3f, 0.25f, 0.4f, 0.35f, 0.5f)
            else -> listOf(0.3f, 0.5f, 0.4f, 0.7f, 0.6f, 1.0f)
        }
    }

    fun updateAmount(vaultId: String, delta: Double) {
        val index = _vaults.indexOfFirst { it.id == vaultId }
        if (index != -1) {
            val vault = _vaults[index]
            val currentAmountValue = parseAmount(vault.amount)
            val newAmountValue = currentAmountValue + delta
            
            val newVault = vault.copy(
                amount = formatAmount(newAmountValue, vault.asset),
                amountUsd = calculateUsdEquiv(newAmountValue, vault.asset),
                progress = vault.targetAmount?.let { (newAmountValue / it).toFloat() } ?: 0f,
                isCompleted = vault.targetAmount?.let { newAmountValue >= it } ?: false
            )
            _vaults[index] = newVault
        }
    }

    fun getVault(id: String?): SavingsVault? {
        return _vaults.find { it.id == id }
    }

    fun saveVault(vault: SavingsVault) {
        val index = _vaults.indexOfFirst { it.id == vault.id }
        if (index != -1) {
            _vaults[index] = vault
        } else {
            _vaults.add(vault)
        }
    }

    fun deleteVault(id: String) {
        _vaults.removeIf { it.id == id }
    }

    fun onDeposit(vaultId: String, amount: Double, note: String?) {
        updateAmount(vaultId, amount)
    }

    fun onWithdraw(vaultId: String, amount: Double, note: String?) {
        updateAmount(vaultId, -amount)
    }

    fun parseAmount(amount: String): Double {
        return amount.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
    }

    private fun formatAmount(value: Double, asset: String): String {
        return when (asset) {
            "Gold" -> String.format(Locale.US, "%.1f Grams", value)
            "EUR" -> String.format(Locale.US, "€%,.2f", value)
            "RSD" -> String.format(Locale.US, "RSD %,.2f", value)
            else -> String.format(Locale.US, "%,.2f", value)
        }
    }

    private fun calculateUsdEquiv(value: Double, asset: String): String {
        val usd = when (asset) {
            "Gold" -> value * goldPricePerGram
            "EUR" -> value * eurToUsd
            "RSD" -> value / 100.0 // Mock rate
            else -> value
        }
        return String.format(Locale.US, "~$%,.2f USD", usd)
    }
}
