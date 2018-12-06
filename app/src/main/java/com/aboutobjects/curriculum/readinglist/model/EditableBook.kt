package com.aboutobjects.curriculum.readinglist.model

import androidx.databinding.ObservableField

class EditableBook(val source: Book? = null) {
    val title = ObservableField<String>(source?.title)
    val author = ObservableField<String>(source?.author?.displayName())
    val year = ObservableField<String>(source?.year)

    fun edited(): Book {
        return Book(
            title = title.get(),
            author = Author.from(author.get()),
            year = year.get()
        )
    }
}