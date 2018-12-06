package com.aboutobjects.curriculum.readinglist.ui

import com.aboutobjects.curriculum.readinglist.model.Book

class BookClickListener(val bookClicked: (Book) -> Unit) {
    fun onBookClicked(book: Book) {
        bookClicked.invoke(book)
    }
}