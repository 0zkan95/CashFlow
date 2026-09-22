package com.example.cashbookneo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cashbookneo.data.repository.TransactionRepository
import com.example.cashbookneo.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

data class SearchUiState(
    val filter: SearchFilterState = SearchFilterState(),
    val results: List<TransactionSearchResult> = emptyList(),
    val totalMatchedCount: Int = 0,
    val netSumAmount: Double = 0.0,
    val isLoading: Boolean = false
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class CustomSearchViewModel(
    private val repository: TransactionRepository
) : ViewModel() {
    private val _filterState = MutableStateFlow(SearchFilterState())
    val filterState: StateFlow<SearchFilterState> = _filterState.asStateFlow()

    val uiState: StateFlow<SearchUiState> = _filterState
        .debounce(250)
        .flatMapLatest { filter ->
            repository.searchTransactions(filter)
                .map { items ->
                    SearchUiState(
                        filter = filter,
                        results = items,
                        totalMatchedCount = items.size,
                        netSumAmount = items.sumOf { it.amount },
                        isLoading = false
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchUiState(isLoading = true)
        )

    fun onSearchQueryChanged(newQuery: String) {
        _filterState.update { it.copy(searchQuery = newQuery) }
    }

    fun onAmountRangeChanged(min: Double, max: Double) {
        _filterState.update { it.copy(minAmount = min, maxAmount = max) }
    }

    fun onPaymentChannelSelected(channel: PaymentChannel) {
        _filterState.update { it.copy(paymentChannel = channel) }
    }

    fun onReceiptToggleChanged(enabled: Boolean) {
        _filterState.update { it.copy(onlyWithReceipt = enabled) }
    }

    fun onDatePresetSelected(preset: DateHorizonPreset) {
        _filterState.update { it.copy(datePreset = preset) }
    }

    fun toggleMatrixCollapse() {
        _filterState.update { it.copy(isMatrixCollapsed = !it.isMatrixCollapsed) }
    }

    fun resetFilters() {
        _filterState.value = SearchFilterState(searchQuery = "")
    }

    class Factory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CustomSearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CustomSearchViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
