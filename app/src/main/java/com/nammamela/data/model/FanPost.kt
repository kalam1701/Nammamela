package com.nammamela.data.model

data class FanPost(
    var id: String = "",
    val nickname: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val posterName: String = ""
)
