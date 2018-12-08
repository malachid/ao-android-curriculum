package com.aboutobjects.curriculum.readinglist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.aboutobjects.curriculum.readinglist.databinding.ActivityBookListV2Binding
import com.aboutobjects.curriculum.readinglist.ui.BookListFragment

class BookListV2Activity: AppCompatActivity() {
    private lateinit var binding: ActivityBookListV2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_list_v2)

        savedInstanceState?.let {
            Log.d(ReadingListApp.TAG, "UI already loaded")
        } ?: let {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, BookListFragment())
                .commitAllowingStateLoss()
        }
    }
}