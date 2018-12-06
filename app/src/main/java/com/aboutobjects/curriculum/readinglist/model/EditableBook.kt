package com.aboutobjects.curriculum.readinglist.model

import androidx.databinding.ObservableField

class EditableBook(val source: Book) {
    val title = ObservableField<String>(source.title)
    val author = ObservableField<String>(source.author?.displayName())
    val year = ObservableField<String>(source.year)
}