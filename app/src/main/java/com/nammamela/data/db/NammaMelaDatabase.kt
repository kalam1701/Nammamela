package com.nammamela.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nammamela.data.model.CastMember
import com.nammamela.data.model.FanPost
import com.nammamela.data.model.Play
import com.nammamela.data.model.Seat

@Database(
    entities = [Play::class, Seat::class, CastMember::class, FanPost::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NammaMelaDatabase : RoomDatabase() {

    abstract fun playDao(): PlayDao
    abstract fun seatDao(): SeatDao
    abstract fun castDao(): CastDao
    abstract fun fanPostDao(): FanPostDao

    companion object {
        @Volatile
        private var INSTANCE: NammaMelaDatabase? = null

        fun getDatabase(context: Context): NammaMelaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NammaMelaDatabase::class.java,
                    "namma_mela_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
