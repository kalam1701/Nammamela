package com.nammamela.ui.cast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammamela.data.model.CastMember
import com.nammamela.data.repository.NammaMelaRepository
import kotlinx.coroutines.launch

class CastViewModel(private val repository: NammaMelaRepository) : ViewModel() {
    val castList = repository.allCast

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
}
