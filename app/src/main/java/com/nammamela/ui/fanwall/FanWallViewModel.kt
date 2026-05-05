package com.nammamela.ui.fanwall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammamela.data.model.FanPost
import com.nammamela.data.repository.NammaMelaRepository
import kotlinx.coroutines.launch

class FanWallViewModel(private val repository: NammaMelaRepository) : ViewModel() {
    val posts = repository.allPosts
    val postCount = repository.postCount

    fun addPost(nickname: String, message: String, posterName: String) {
        viewModelScope.launch {
            repository.addPost(
                FanPost(
                    nickname = nickname,
                    message = message,
                    posterName = posterName,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun deletePost(id: String) {
        viewModelScope.launch {
            repository.deletePost(id)
        }
    }
}
