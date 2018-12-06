package com.aboutobjects.curriculum.readinglist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aboutobjects.curriculum.readinglist.databinding.ActivityBookListBinding
import com.aboutobjects.curriculum.readinglist.model.Book
import com.aboutobjects.curriculum.readinglist.model.ReadingList
import com.aboutobjects.curriculum.readinglist.ui.ReadingListAdapter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStreamReader

class BookListActivity : AppCompatActivity() {
    companion object {
        const val JSON_FILE = "BooksAndAuthors.json"
        const val PREF_FILE = "samplePrefs"
        const val EDIT_REQUEST_CODE = 1234
        const val NEW_REQUEST_CODE = 1235
    }

    private val app: ReadingListApp by lazy { application as ReadingListApp }
    private lateinit var binding: ActivityBookListBinding
    private val viewAdapter = ReadingListAdapter(
        bookClicked = {
            startActivityForResult(EditBookActivity.getIntent(
                context = this,
                source = it
            ), EDIT_REQUEST_CODE)
        }
    )
    private lateinit var viewManager: RecyclerView.LayoutManager

    private fun loadJsonFromAssets(): ReadingList? {
        return try{
            val reader = InputStreamReader(assets.open(JSON_FILE))
            app.gson.fromJson(reader, ReadingList::class.java)
        } catch (e: Exception) {
            // @TODO introduce logging
            null
        }
    }

    private fun loadJsonFromFiles(): ReadingList? {
        return try {
            val reader = FileReader(File(filesDir, JSON_FILE))
            app.gson.fromJson(reader, ReadingList::class.java)
        } catch (e: Exception) {
            // @TODO introduce logging
            null
        }
    }

    private fun loadJson(): ReadingList? {
        return loadJsonFromFiles() ?: loadJsonFromAssets()
    }

    private fun saveJson(readingList: ReadingList) {
        try{
            val writer = FileWriter(File(filesDir, JSON_FILE))
            app.gson.toJson(readingList, writer)
            writer.flush()
            writer.close()
        }catch(e: Exception){
            // @TODO introduce logging
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_list)

        viewManager = LinearLayoutManager(this)
        binding.recycler.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            addItemDecoration(DividerItemDecoration(this@BookListActivity, DividerItemDecoration.VERTICAL))
            adapter = viewAdapter
        }

        binding.fab.setOnClickListener {
            startActivityForResult(EditBookActivity.getIntent(
                context = this
            ), NEW_REQUEST_CODE)
        }

        loadJson()?.let {
            viewAdapter.readingList = it
            saveJson(it)
        }
    }

    private fun getBook(data: Intent?, key: String): Book? {
        return data?.getStringExtra(key)?.let {
            app.gson.fromJson(it, Book::class.java)
        }
    }

    /**
     * Dispatch incoming result to the correct fragment.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // We ignore any other request we see
        if (requestCode == EDIT_REQUEST_CODE || requestCode == NEW_REQUEST_CODE) {
            // We ignore any canceled result
            if (resultCode == Activity.RESULT_OK) {
                // Grab the books from the data
                val source = getBook(data, EditBookActivity.EXTRA_SOURCE_BOOK)
                val edited = getBook(data, EditBookActivity.EXTRA_EDITED_BOOK)
                if (edited != null) {
                    // Grab the old reading list since it is read-only
                    viewAdapter.readingList?.let { oldReadingList ->
                        // Create a new ReadingList
                        val newReadingList = ReadingList(
                            title = oldReadingList.title,
                            books = oldReadingList.books
                                .toMutableList()
                                .filterNot {
                                    it.title == source?.title
                                            && it.author == source?.author
                                            && it.year == source?.year
                                }.plus(edited)
                                .toList()
                        )
                        // Save the results
                        saveJson(newReadingList)
                        // And update our view
                        viewAdapter.readingList = newReadingList
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}

fun Context.getBooksLoadedMessage(numberOfBooks: Int): String {
    return when(numberOfBooks) {
        0 -> getString(R.string.loaded_none)
        1 -> getString(R.string.loaded_one)
        else -> getString(R.string.loaded_some, numberOfBooks)
    }
}
