package com.aboutobjects.curriculum.readinglist

import android.app.Application
import com.aboutobjects.curriculum.readinglist.service.BookListService
import com.aboutobjects.curriculum.readinglist.service.CurriculumService
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

    val bookListService: BookListService by lazy {
        BookListService(app = this)
    }

    val curriculumService: CurriculumService by lazy {
        CurriculumService(app = this)
    }
}
