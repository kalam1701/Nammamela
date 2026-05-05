package com.nammamela.data.model

enum class SeatStatus { AVAILABLE, RESERVED, SELECTED }

data class Seat(
    var id: String = "",
    val row: String = "",
    val seatNumber: Int = 0,
    val status: String = SeatStatus.AVAILABLE.name,
    val bookedByName: String = "",
    val seatLabel: String = ""
)
