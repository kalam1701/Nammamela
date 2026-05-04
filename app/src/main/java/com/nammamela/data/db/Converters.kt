package com.nammamela.data.db

import androidx.room.TypeConverter
import com.nammamela.data.model.SeatStatus

class Converters {
    @TypeConverter
    fun fromSeatStatus(value: SeatStatus): String = value.name

    @TypeConverter
    fun toSeatStatus(value: String): SeatStatus = SeatStatus.valueOf(value)
}
