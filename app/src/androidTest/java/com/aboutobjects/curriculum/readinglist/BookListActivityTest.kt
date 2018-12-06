package com.aboutobjects.curriculum.readinglist

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class BookListActivityTest {
    val context = ApplicationProvider.getApplicationContext<ReadingListApp>()

    @Test
    fun loadedText_noBooks() {
        assertTrue(context.getBooksLoadedMessage(0).contains("No books"))
    }

    @Test
    fun loadedText_oneBook() {
        assertTrue(context.getBooksLoadedMessage(1).contains("One book"))
    }

    @Test
    fun loadedText_twoBooks() {
        assertTrue(context.getBooksLoadedMessage(2).contains("2 books"))
    }
}