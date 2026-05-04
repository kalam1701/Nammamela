package com.nammamela.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nammamela.data.model.CastMember

@Dao
interface CastDao {
    @Query("SELECT * FROM cast_members")
    fun getAllCast(): LiveData<List<CastMember>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCastMember(castMember: CastMember)

    @Update
    suspend fun updateCastMember(castMember: CastMember)

    @Delete
    suspend fun deleteCastMember(castMember: CastMember)

    @Query("DELETE FROM cast_members")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM cast_members")
    suspend fun getCount(): Int
}
