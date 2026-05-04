package com.nammamela

import android.app.Application
import com.nammamela.data.db.NammaMelaDatabase
import com.nammamela.data.repository.NammaMelaRepository

class NammaMelaApp : Application() {

    val database by lazy { NammaMelaDatabase.getDatabase(this) }

    val repository by lazy {
        NammaMelaRepository(
            database.playDao(),
            database.seatDao(),
            database.castDao(),
            database.fanPostDao()
        )
    }
}
