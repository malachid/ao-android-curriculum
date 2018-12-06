package com.aboutobjects.curriculum.readinglist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.aboutobjects.curriculum.readinglist.databinding.ActivityEditBookBinding
import com.aboutobjects.curriculum.readinglist.model.Book
import com.aboutobjects.curriculum.readinglist.model.EditableBook

class EditBookActivity: AppCompatActivity() {
    companion object {
        val EXTRA_BOOK = "${EditBookActivity::class.java}.name::book"

        fun getIntent(context: Context, book: Book): Intent {
            val gson = (context.applicationContext as ReadingListApp).gson
            return Intent(context, EditBookActivity::class.java).apply {
                putExtra(EXTRA_BOOK, gson.toJson(book))
            }
        }
    }

    private val extraBook: String? by lazy { intent?.getStringExtra(EXTRA_BOOK) }
    private val app: ReadingListApp by lazy { application as ReadingListApp }
    private lateinit var binding: ActivityEditBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_book)
        extraBook?.let {
            binding.book = EditableBook(
                source = app.gson.fromJson(it, Book::class.java)
            )
        }
    }
}