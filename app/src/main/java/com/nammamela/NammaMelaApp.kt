package com.nammamela

import android.app.Application
import com.nammamela.data.repository.NammaMelaRepository

class NammaMelaApp : Application() {

    val repository by lazy {
        NammaMelaRepository()
    }
}
