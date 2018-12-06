package com.aboutobjects.curriculum.readinglist.model

data class ReadingList(
    val title: String? = null,
    val books: List<Book> = emptyList()
)