package com.aboutobjects.curriculum.readinglist

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class ReadingListApp : Application() {
    val gson: Gson by lazy {
        GsonBuilder()
            .setPrettyPrinting()
            .create()
    }
}
