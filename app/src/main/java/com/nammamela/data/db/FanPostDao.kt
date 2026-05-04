package com.nammamela.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nammamela.data.model.FanPost

@Dao
interface FanPostDao {
    @Query("SELECT * FROM fan_posts ORDER BY timestamp DESC")
    fun getAllPosts(): LiveData<List<FanPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(fanPost: FanPost)

    @Delete
    suspend fun deletePost(fanPost: FanPost)

    @Query("DELETE FROM fan_posts WHERE id = :id")
    suspend fun deletePostById(id: Int)

    @Query("SELECT COUNT(*) FROM fan_posts")
    fun getPostCount(): LiveData<Int>
}
