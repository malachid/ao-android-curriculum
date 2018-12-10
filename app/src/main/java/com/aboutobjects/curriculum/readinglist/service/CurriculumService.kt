package com.aboutobjects.curriculum.readinglist.service

import com.aboutobjects.curriculum.readinglist.BuildConfig
import com.aboutobjects.curriculum.readinglist.ReadingListApp
import com.aboutobjects.curriculum.readinglist.model.Author
import com.aboutobjects.curriculum.readinglist.model.Book
import com.aboutobjects.curriculum.readinglist.model.ReadingList
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class CurriculumService(val app: ReadingListApp) {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.CURRICULUM_SERVER)
        .addConverterFactory(GsonConverterFactory.create(app.gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .build()

    private val backend = retrofit.create(CurriculumAPI::class.java)

    fun getReadingList(id: Int): Single<ReadingList> {
        return backend.getReadingList(id = id)
    }

    private fun createAuthor(author: Author): Single<Author> {
        return backend.createAuthor(
            model = Author(
                firstName = author.firstName,
                lastName = author.lastName
            )
        )
    }

    private fun findOrCreateAuthor(source: Author): Single<Author> {
        return backend.findAuthor(
            firstName = source.firstName,
            lastName = source.lastName
        ).onErrorResumeNext{
            createAuthor(author = source)
        }
    }

    fun createBook(source: Book): Single<Book> {
        return findOrCreateAuthor(source = source.author ?: throw IllegalArgumentException("Source Author missing"))
            .flatMap { author ->
                backend.createBook(
                    model = Book(
                        title = source.title,
                        year = source.year,
                        authorId = author.id ?: throw IllegalArgumentException("Source Author ID missing")
                    )
                )
            }
    }

    fun updateBook(source: Book, edited: Book): Single<Book> {
        if (source.id == null) {
            throw IllegalArgumentException("Source Book ID missing")
        }

        if (source.author?.id == null) {
            throw IllegalArgumentException("Source Author missing")
        }

        return when {
            // source author HAS id, but edited author does NOT
            source.author.isSameAs(edited.author) -> Single.just(source.author)
            // maybe we typed a different existing authors' name?
            edited.author != null -> findOrCreateAuthor(edited.author)
            // fail-safe
            else -> Single.just(source.author)
        }.flatMap { author ->
            backend.updateBook(
                id = source.id,
                model = Book(
                    id = source.id,
                    title = edited.title,
                    year = edited.year,
                    authorId = author.id ?: throw IllegalArgumentException("Source Author ID missing")
                )
            )
        }
    }

    fun addBookToReadingList(readingList: ReadingList, book: Book): Single<ReadingList> {
        if (book.id == null) {
            throw IllegalArgumentException("Book ID missing")
        }

        return backend.updateReadingList(
            id = readingList.id ?: throw IllegalArgumentException("Reading List ID missing"),
            // Send a version with just the IDs
            model = ReadingList(
                id = readingList.id,
                title = readingList.title ?: "",
                bookIds = readingList.books
                    .mapNotNull { it -> it.id }
                    .toMutableList()
                    .plus(book.id)
                    .distinct()
                    .sorted()
                    .toList()
            )
        )
    }

}