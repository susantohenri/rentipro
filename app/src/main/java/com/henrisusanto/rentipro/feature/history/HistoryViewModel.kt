package com.henrisusanto.rentipro.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrisusanto.rentipro.core.data.RentalRepository
import com.henrisusanto.rentipro.core.database.model.HistoryRentalWithDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(
    private val rentalRepository: RentalRepository,
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = rentalRepository.observeHistory()
        .map { items -> 
            HistoryUiState(
                items = items,
                totalRentals = items.size,
                totalRevenue = items.sumOf { it.rental.price }
            ) 
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryUiState(),
        )
}

data class HistoryUiState(
    val items: List<HistoryRentalWithDetails> = emptyList(),
    val totalRentals: Int = 0,
    val totalRevenue: Int = 0,
)
