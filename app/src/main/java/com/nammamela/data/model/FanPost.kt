package com.nammamela.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fan_posts")
data class FanPost(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nickname: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val posterName: String = ""
)
