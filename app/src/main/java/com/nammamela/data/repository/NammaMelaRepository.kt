package com.nammamela.data.repository

import androidx.lifecycle.LiveData
import com.nammamela.data.db.*
import com.nammamela.data.model.*

class NammaMelaRepository(
    private val playDao: PlayDao,
    private val seatDao: SeatDao,
    private val castDao: CastDao,
    private val fanPostDao: FanPostDao
) {
    // Play
    val tonightsPlay: LiveData<Play?> = playDao.getTonightsPlay()

    suspend fun savePlay(play: Play) {
        playDao.deactivateAll()
        playDao.insertPlay(play.copy(isActive = true))
    }

    suspend fun updatePlay(play: Play) = playDao.updatePlay(play)
    suspend fun getTonightsPlayOnce() = playDao.getTonightsPlayOnce()

    // Seats
    val allSeats: LiveData<List<Seat>> = seatDao.getAllSeats()
    val availableCount: LiveData<Int> = seatDao.getAvailableCount()
    val totalCount: LiveData<Int> = seatDao.getTotalCount()

    suspend fun initSeatsIfNeeded() {
        val count = seatDao.getSeatCount()
        if (count == 0) {
            val seats = mutableListOf<Seat>()
            val rows = listOf("A","B","C","D","E","F","G","H","I","J")
            for (row in rows) {
                for (num in 1..12) {
                    seats.add(Seat(row = row, seatNumber = num, status = SeatStatus.AVAILABLE, seatLabel = "$row-$num"))
                }
            }
            seatDao.insertSeats(seats)
        }
    }

    suspend fun reserveSeat(seatId: Int, name: String) = seatDao.reserveSeat(seatId, name)
    suspend fun resetAllSeats() = seatDao.resetAllSeats()

    // Cast
    val allCast: LiveData<List<CastMember>> = castDao.getAllCast()

    suspend fun insertCastMember(castMember: CastMember) {
        castDao.insertCastMember(castMember)
    }

    suspend fun updateCastMember(castMember: CastMember) = castDao.updateCastMember(castMember)
    suspend fun deleteCastMember(castMember: CastMember) = castDao.deleteCastMember(castMember)

    suspend fun initCastIfNeeded() {
        val count = castDao.getCount()
        if (count == 0) {
            castDao.insertCastMember(CastMember(name = "Rajkumar Rao", role = "Lead Actor", bio = "Veteran stage artist with 20 years of Company Nataka experience.", photoUrl = ""))
            castDao.insertCastMember(CastMember(name = "Siddappa Hasyagar", role = "Comedian", bio = "The crowd's favourite! Known for his lightning-fast wit.", photoUrl = ""))
            castDao.insertCastMember(CastMember(name = "Kavitha Suresh", role = "Singer", bio = "Classical vocalist who sets the emotional tone of every act.", photoUrl = ""))
        }
    }

    // Fan Wall
    val allPosts: LiveData<List<FanPost>> = fanPostDao.getAllPosts()
    val postCount: LiveData<Int> = fanPostDao.getPostCount()

    suspend fun addPost(post: FanPost) = fanPostDao.insertPost(post)
    suspend fun deletePost(id: Int) = fanPostDao.deletePostById(id)
}
