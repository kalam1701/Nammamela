package com.nammamela.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plays")
data class Play(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val genre: String = "",
    val duration: String = "",
    val synopsis: String = "",
    val posterUrl: String = "",
    val showTime: String = "",
    val isActive: Boolean = true
)
