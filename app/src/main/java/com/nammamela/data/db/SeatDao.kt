package com.nammamela.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nammamela.data.model.Seat
import com.nammamela.data.model.SeatStatus

@Dao
interface SeatDao {
    @Query("SELECT * FROM seats ORDER BY row ASC, seatNumber ASC")
    fun getAllSeats(): LiveData<List<Seat>>

    @Query("SELECT * FROM seats ORDER BY row ASC, seatNumber ASC")
    suspend fun getAllSeatsOnce(): List<Seat>

    @Query("SELECT COUNT(*) FROM seats WHERE status = 'AVAILABLE'")
    fun getAvailableCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM seats")
    fun getTotalCount(): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeat(seat: Seat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeats(seats: List<Seat>)

    @Update
    suspend fun updateSeat(seat: Seat)

    @Query("UPDATE seats SET status = 'AVAILABLE', bookedByName = '' WHERE id = :seatId")
    suspend fun releaseSeat(seatId: Int)

    @Query("UPDATE seats SET status = 'RESERVED', bookedByName = :name WHERE id = :seatId")
    suspend fun reserveSeat(seatId: Int, name: String)

    @Query("UPDATE seats SET status = 'AVAILABLE', bookedByName = ''")
    suspend fun resetAllSeats()

    @Query("DELETE FROM seats")
    suspend fun deleteAllSeats()

    @Query("SELECT COUNT(*) FROM seats")
    suspend fun getSeatCount(): Int
}
