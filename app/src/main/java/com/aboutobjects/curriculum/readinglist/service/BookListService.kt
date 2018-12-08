package com.aboutobjects.curriculum.readinglist.service

import android.util.Log
import com.aboutobjects.curriculum.readinglist.ReadingListApp
import com.aboutobjects.curriculum.readinglist.model.Book
import com.aboutobjects.curriculum.readinglist.model.ReadingList
import io.reactivex.Completable
import io.reactivex.Completable.complete
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStreamReader

class BookListService(val app: ReadingListApp) {
    companion object {
        const val JSON_FILE = "BooksAndAuthors.json"
    }

    val readingList: BehaviorSubject<ReadingList> = BehaviorSubject.create()

    init {
        val loadDisposable = loadFromFiles()
            .switchIfEmpty(loadFromAssets())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { readingList -> save(readingList) },
                onError = { Log.w(ReadingListApp.TAG, "Error loading books", it) },
                onComplete = { Log.w(ReadingListApp.TAG, "No books found") }
            )
    }

    private fun loadFromAssets(): Maybe<ReadingList> {
        return try{
            val reader = InputStreamReader(app.assets.open(JSON_FILE))
            Maybe.just(app.gson.fromJson(reader, ReadingList::class.java))
        } catch (e: Exception) {
            Log.d(ReadingListApp.TAG, "Failed to load Json from assets/${BookListService.JSON_FILE}",e)
            Maybe.empty<ReadingList>()
        }
    }

    private fun loadFromFiles(): Maybe<ReadingList> {
        return try {
            val reader = FileReader(File(app.filesDir, JSON_FILE))
            Maybe.just(app.gson.fromJson(reader, ReadingList::class.java))
        } catch (e: Exception) {
            Log.d(ReadingListApp.TAG, "Failed to load Json from files/${BookListService.JSON_FILE}",e)
            Maybe.empty<ReadingList>()
        }
    }

    fun save(readingList: ReadingList): Completable {
        return try{
            val writer = FileWriter(File(app.filesDir, JSON_FILE))
            app.gson.toJson(readingList, writer)
            writer.flush()
            writer.close()
            this.readingList.onNext(readingList)
            complete()
        }catch(e: Exception){
            Log.d(ReadingListApp.TAG, "Failed to save Json to files/${BookListService.JSON_FILE}",e)
            error(e)
        }
    }

    fun edit(source: Book?, edited: Book): Completable {
        return readingList.value?.let { oldReadingList ->
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
            save(newReadingList)
        } ?: error(IllegalArgumentException("Reading List not found"))
    }
}