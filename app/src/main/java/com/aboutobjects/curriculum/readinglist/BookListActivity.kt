package com.aboutobjects.curriculum.readinglist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aboutobjects.curriculum.readinglist.databinding.ActivityBookListBinding
import com.aboutobjects.curriculum.readinglist.model.Book
import com.aboutobjects.curriculum.readinglist.model.ReadingList
import com.aboutobjects.curriculum.readinglist.ui.ReadingListAdapter
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
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
    private var loadJsonDisposable: Disposable? = null

    private val viewAdapter = ReadingListAdapter(
        bookClicked = {
            startActivityForResult(EditBookActivity.getIntent(
                context = this,
                source = it
            ), EDIT_REQUEST_CODE)
        }
    )
    private lateinit var viewManager: RecyclerView.LayoutManager

    private fun loadJsonFromAssets(): Maybe<ReadingList> {
        return try{
            val reader = InputStreamReader(assets.open(JSON_FILE))
            Maybe.just(app.gson.fromJson(reader, ReadingList::class.java))
        } catch (e: Exception) {
            Log.d(ReadingListApp.TAG, "Failed to load Json from assets/$JSON_FILE",e)
            Maybe.empty<ReadingList>()
        }
    }

    private fun loadJsonFromFiles(): Maybe<ReadingList> {
        return try {
            val reader = FileReader(File(filesDir, JSON_FILE))
            Maybe.just(app.gson.fromJson(reader, ReadingList::class.java))
        } catch (e: Exception) {
            Log.d(ReadingListApp.TAG, "Failed to load Json from files/$JSON_FILE",e)
            Maybe.empty<ReadingList>()
        }
    }

    private fun loadJson(): Maybe<ReadingList> {
        return loadJsonFromFiles()
            .switchIfEmpty(loadJsonFromAssets())
    }

    private fun saveJson(readingList: ReadingList): Completable {
        return try{
            val writer = FileWriter(File(filesDir, JSON_FILE))
            app.gson.toJson(readingList, writer)
            writer.flush()
            writer.close()
            Completable.complete()
        }catch(e: Exception){
            Log.d(ReadingListApp.TAG, "Failed to save Json to files/$JSON_FILE",e)
            Completable.error(e)
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

        loadJsonDisposable = loadJson()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    Log.i(ReadingListApp.TAG, "${it.books.size} books loaded")
                    viewAdapter.readingList = it
                    saveJson(it)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
                },
                onError = {
                    Log.e(ReadingListApp.TAG, "Error loading books: ${it.message}", it)
                    it.message?.let {
                        Snackbar.make(binding.recycler, it, Snackbar.LENGTH_LONG)
                            .show()
                    }
                },
                onComplete = {
                    Log.w(ReadingListApp.TAG, "Unable to load any books")
                }
            )
    }

    override fun onDestroy() {
        loadJsonDisposable?.dispose()
        super.onDestroy()
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
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy(
                                onComplete = {
                                    // And update our view
                                    viewAdapter.readingList = newReadingList
                                },
                                onError = {
                                    it.message?.let {
                                        Snackbar.make(binding.recycler, it, Snackbar.LENGTH_LONG)
                                            .show()
                                    }
                                }
                            )
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
