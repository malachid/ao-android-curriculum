package com.aboutobjects.curriculum.readinglist

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import com.aboutobjects.curriculum.readinglist.databinding.ActivityBookListBinding
import com.aboutobjects.curriculum.readinglist.model.ReadingList
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class BookListActivity : AppCompatActivity() {
    companion object {
        // from https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        const val timestampPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
        const val displayPattern = "yyyy.MM.dd 'at' HH:mm:ss z"

        val KEY_TIMESTAMP = "${BookListActivity::class.java.name}::timestamp"
        const val JSON_FILE = "BooksAndAuthors.json"
        const val PREF_FILE = "samplePrefs"
    }

    private val app: ReadingListApp by lazy { application as ReadingListApp }
    private lateinit var binding: ActivityBookListBinding

    private fun loadJson(): ReadingList? {
        return try{
            val reader = InputStreamReader(assets.open(JSON_FILE))
            app.gson.fromJson(reader, ReadingList::class.java)
        } catch (e: Exception) {
            // @TODO introduce logging
            null
        }
    }

    private val timestampFormat: SimpleDateFormat by lazy {
        // Don't set Locale.getDefault() in companion because user may change it at runtime
        SimpleDateFormat(timestampPattern, Locale.getDefault())
    }

    private val displayFormat: SimpleDateFormat by lazy {
        SimpleDateFormat(displayPattern, Locale.getDefault())
    }

    private fun timestamp(): String {
        return timestampFormat.format(Calendar.getInstance().time)
    }

    private val prefs: SharedPreferences by lazy {
        getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_list)

        loadJson()?.let {
            binding.helloText.text = getBooksLoadedMessage(it.books.size)
            try{
                val writer = FileWriter(File(filesDir, JSON_FILE))
                app.gson.toJson(it, writer)
                writer.flush()
                writer.close()
            }catch(e: Exception){
                // @TODO introduce logging
            }
        }

        prefs.getString(KEY_TIMESTAMP, null)?.let {
            val time = timestampFormat.parse(it)
            binding.loginText.text = resources.getString(R.string.last_login, displayFormat.format(time))
        }

        prefs.edit {
            putString(KEY_TIMESTAMP, timestamp())
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
