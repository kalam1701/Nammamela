package com.nammamela.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nammamela.data.model.Play

@Dao
interface PlayDao {
    @Query("SELECT * FROM plays WHERE isActive = 1 LIMIT 1")
    fun getTonightsPlay(): LiveData<Play?>

    @Query("SELECT * FROM plays WHERE isActive = 1 LIMIT 1")
    suspend fun getTonightsPlayOnce(): Play?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlay(play: Play)

    @Update
    suspend fun updatePlay(play: Play)

    @Query("UPDATE plays SET isActive = 0")
    suspend fun deactivateAll()

    @Query("DELETE FROM plays WHERE id = :id")
    suspend fun deletePlay(id: Int)
}
