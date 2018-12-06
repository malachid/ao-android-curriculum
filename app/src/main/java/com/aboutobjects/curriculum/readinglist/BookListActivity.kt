package com.aboutobjects.curriculum.readinglist

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.aboutobjects.curriculum.readinglist.model.ReadingList
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
    }

    private val gson: Gson by lazy {
        GsonBuilder()
            .setPrettyPrinting()
            .create()
    }

    private fun loadJson(): ReadingList? {
        return try{
            val reader = InputStreamReader(assets.open(JSON_FILE))
            gson.fromJson(reader, ReadingList::class.java)
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
        getSharedPreferences("samplePrefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        loadJson()?.let {
            findViewById<TextView>(R.id.hello_text).text = resources.getString(R.string.hello_text, it.books.size)
            try{
                val writer = FileWriter(File(filesDir, JSON_FILE))
                gson.toJson(it, writer)
                writer.flush()
                writer.close()
            }catch(e: Exception){
                // @TODO introduce logging
            }
        }

        prefs.getString(KEY_TIMESTAMP, null)?.let {
            val time = timestampFormat.parse(it)
            findViewById<TextView>(R.id.login_text).text = resources.getString(R.string.last_login, displayFormat.format(time))
        }

        prefs.edit {
            putString(KEY_TIMESTAMP, timestamp())
        }
    }
}
