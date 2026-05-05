package com.nammamela.ui.seat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammamela.data.repository.NammaMelaRepository
import kotlinx.coroutines.launch

class SeatViewModel(private val repository: NammaMelaRepository) : ViewModel() {
    val seats = repository.allSeats
    val availableCount = repository.availableCount
    val totalCount = repository.totalCount

    fun reserveSeat(seatId: String, name: String) {
        viewModelScope.launch {
            repository.reserveSeat(seatId, name)
        }
    }

    fun resetAllSeats() {
        viewModelScope.launch {
            repository.resetAllSeats()
        }
    }
}
