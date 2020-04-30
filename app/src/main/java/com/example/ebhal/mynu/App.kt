package com.example.ebhal.mynu

import android.app.Application
import com.example.ebhal.mynu.utils.Database

class App : Application() {

    companion object {
        lateinit var instance : App
        val database : Database by lazy { Database(instance) }
    }

    init{
        super.onCreate()
        instance = this
    }
}