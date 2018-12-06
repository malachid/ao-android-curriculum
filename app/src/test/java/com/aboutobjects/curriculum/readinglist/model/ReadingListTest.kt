package com.aboutobjects.curriculum.readinglist.model

import com.google.common.truth.Truth
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.junit.Assert
import org.junit.Test

class ReadingListTest {

    @Test
    fun no_books() {
        val readingList = ReadingList(
            title = "no_books_title"
        )
        Assert.assertEquals(0, readingList.books.size)
    }

    @Test
    fun two_books() {
        val readingList = ReadingList(
            title = "two books title",
            books = listOf(
                Book(),
                Book(title = "second book title")
            )
        )
        Assert.assertEquals(2, readingList.books.size)
        Assert.assertEquals(null, readingList.books[0].title)
        Assert.assertEquals("second book title", readingList.books[1].title)

        // Only one of the 'assertThat' below can have a static import
        // Hamcrest
        MatcherAssert.assertThat(readingList.books[1].title, `is`(equalTo("second book title")))
        // Truth
        Truth.assertThat(readingList.books[1].title).isEqualTo("second book title")
    }
}