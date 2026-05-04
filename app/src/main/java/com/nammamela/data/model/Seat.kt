package com.nammamela.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SeatStatus { AVAILABLE, RESERVED, SELECTED }

@Entity(tableName = "seats")
data class Seat(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val row: String = "",
    val seatNumber: Int = 0,
    val status: SeatStatus = SeatStatus.AVAILABLE,
    val bookedByName: String = "",
    val seatLabel: String = "" // e.g. "A-5"
)
