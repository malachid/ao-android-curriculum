package com.aboutobjects.curriculum.readinglist.service

import android.util.Log
import com.aboutobjects.curriculum.readinglist.ReadingListApp
import com.aboutobjects.curriculum.readinglist.model.Book
import com.aboutobjects.curriculum.readinglist.model.ReadingList
import io.reactivex.Completable
import io.reactivex.Completable.complete
import io.reactivex.Maybe
import io.reactivex.Single
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
        val loadDisposable = loadFromServer()
            .switchIfEmpty(loadFromFiles())
            .switchIfEmpty(loadFromAssets())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { readingList -> save(readingList) },
                onError = { Log.w(ReadingListApp.TAG, "Error loading books", it) },
                onComplete = { Log.w(ReadingListApp.TAG, "No books found") }
            )
    }
    private fun loadFromServer(): Maybe<ReadingList> {
        return app.curriculumService
            .getReadingList(id = 0)
            .toMaybe()
            .onErrorComplete()
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

    fun edit(source: Book?, edited: Book): Single<Book> {
        return when (source) {
            null -> app.curriculumService.createBook(source = edited)
            else -> app.curriculumService.updateBook(source = source, edited = edited)
        }.flatMap { book ->
            app.curriculumService
                .addBookToReadingList(
                    readingList = readingList.value ?: throw IllegalArgumentException("Reading List not loaded"),
                    book = book
                )
                .map { it -> Pair(it, book) }
                .doOnSuccess { (readingList, _) -> save(readingList) }
        }.map { (_, book) ->
            book
        }
    }
}