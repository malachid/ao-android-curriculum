package com.aboutobjects.curriculum.readinglist

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import java.text.SimpleDateFormat
import java.util.*

class BookListActivity : AppCompatActivity() {
    companion object {
        // from https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        const val timestampPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
        const val displayPattern = "yyyy.MM.dd 'at' HH:mm:ss z"

        val KEY_TIMESTAMP = "${BookListActivity::class.java.name}::timestamp"
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

        prefs.getString(KEY_TIMESTAMP, null)?.let {
            val time = timestampFormat.parse(it)
            findViewById<TextView>(R.id.login_text).text = resources.getString(R.string.last_login, displayFormat.format(time))
        }

        prefs.edit {
            putString(KEY_TIMESTAMP, timestamp())
        }
    }
}
