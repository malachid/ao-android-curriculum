package com.aboutobjects.curriculum.readinglist

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aboutobjects.curriculum.readinglist.databinding.ActivityBookListBinding
import com.aboutobjects.curriculum.readinglist.model.ReadingList
import com.aboutobjects.curriculum.readinglist.ui.ReadingListAdapter
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader

class BookListActivity : AppCompatActivity() {
    companion object {
        const val JSON_FILE = "BooksAndAuthors.json"
        const val PREF_FILE = "samplePrefs"
    }

    private val app: ReadingListApp by lazy { application as ReadingListApp }
    private lateinit var binding: ActivityBookListBinding
    private val viewAdapter = ReadingListAdapter()
    private lateinit var viewManager: RecyclerView.LayoutManager

    private fun loadJson(): ReadingList? {
        return try{
            val reader = InputStreamReader(assets.open(JSON_FILE))
            app.gson.fromJson(reader, ReadingList::class.java)
        } catch (e: Exception) {
            // @TODO introduce logging
            null
        }
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

        loadJson()?.let {
            viewAdapter.readingList = it
            saveJson(it)
        }
    }
}

fun Context.getBooksLoadedMessage(numberOfBooks: Int): String {
    return when(numberOfBooks) {
        0 -> getString(R.string.loaded_none)
        1 -> getString(R.string.loaded_one)
        else -> getString(R.string.loaded_some, numberOfBooks)
    }
}
