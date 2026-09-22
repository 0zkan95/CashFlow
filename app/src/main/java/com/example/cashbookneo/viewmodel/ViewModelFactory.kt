package com.example.cashbookneo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cashbookneo.data.PreferenceManager
import com.example.cashbookneo.data.AppDatabase

class ViewModelFactory(
    private val preferenceManager: PreferenceManager,
    private val database: AppDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(preferenceManager, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
