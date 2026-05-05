package com.nammamela.data.model

data class Play(
    var id: String = "",
    val title: String = "",
    val genre: String = "",
    val duration: String = "",
    val synopsis: String = "",
    val posterUrl: String = "",
    val showTime: String = "",
    val date: String = "",
    val venue: String = "",
    val isActive: Boolean = true
)
