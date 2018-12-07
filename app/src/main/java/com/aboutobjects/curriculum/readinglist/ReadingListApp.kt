package com.aboutobjects.curriculum.readinglist

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class ReadingListApp : Application() {
    companion object {
        const val TAG = "ReadingListApp"
    }

    val gson: Gson by lazy {
        GsonBuilder()
            .setPrettyPrinting()
            .create()
    }
}
