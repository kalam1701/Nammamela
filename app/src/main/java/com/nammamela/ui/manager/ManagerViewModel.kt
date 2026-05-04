package com.nammamela.ui.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammamela.data.model.CastMember
import com.nammamela.data.model.Play
import com.nammamela.data.repository.NammaMelaRepository
import kotlinx.coroutines.launch

class ManagerViewModel(private val repository: NammaMelaRepository) : ViewModel() {
    val tonightsPlay = repository.tonightsPlay
    val castList = repository.allCast

    fun savePlay(play: Play) {
        viewModelScope.launch {
            repository.savePlay(play)
        }
    }

    fun updatePlay(play: Play) {
        viewModelScope.launch {
            repository.updatePlay(play)
        }
    }

    fun addCastMember(castMember: CastMember) {
        viewModelScope.launch {
            repository.insertCastMember(castMember)
        }
    }

    fun updateCastMember(castMember: CastMember) {
        viewModelScope.launch {
            repository.updateCastMember(castMember)
        }
    }

    fun deleteCastMember(castMember: CastMember) {
        viewModelScope.launch {
            repository.deleteCastMember(castMember)
        }
    }

    fun resetSeats() {
        viewModelScope.launch {
            repository.resetAllSeats()
        }
    }
}
