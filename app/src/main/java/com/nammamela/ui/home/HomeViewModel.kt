package com.nammamela.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammamela.data.model.Play
import com.nammamela.data.repository.NammaMelaRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: NammaMelaRepository) : ViewModel() {
    val tonightsPlay = repository.tonightsPlay
    val availableSeats = repository.availableCount
    val totalSeats = repository.totalCount

    fun initData() {
        viewModelScope.launch {
            repository.initSeatsIfNeeded()
            repository.initCastIfNeeded()
        }
    }
}
