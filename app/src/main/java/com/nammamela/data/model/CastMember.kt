package com.nammamela.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cast_members")
data class CastMember(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val role: String = "",
    val bio: String = "",
    val photoUrl: String = ""
)
