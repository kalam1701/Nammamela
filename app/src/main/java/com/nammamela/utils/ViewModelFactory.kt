package com.nammamela.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nammamela.data.repository.NammaMelaRepository
import com.nammamela.ui.home.HomeViewModel
import com.nammamela.ui.seat.SeatViewModel
import com.nammamela.ui.cast.CastViewModel
import com.nammamela.ui.fanwall.FanWallViewModel
import com.nammamela.ui.manager.ManagerViewModel

class ViewModelFactory(private val repository: NammaMelaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository) as T
            modelClass.isAssignableFrom(SeatViewModel::class.java) -> SeatViewModel(repository) as T
            modelClass.isAssignableFrom(CastViewModel::class.java) -> CastViewModel(repository) as T
            modelClass.isAssignableFrom(FanWallViewModel::class.java) -> FanWallViewModel(repository) as T
            modelClass.isAssignableFrom(ManagerViewModel::class.java) -> ManagerViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
