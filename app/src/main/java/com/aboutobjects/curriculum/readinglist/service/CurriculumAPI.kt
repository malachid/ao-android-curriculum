package com.aboutobjects.curriculum.readinglist.service

import com.aboutobjects.curriculum.readinglist.model.Author
import com.aboutobjects.curriculum.readinglist.model.Book
import com.aboutobjects.curriculum.readinglist.model.ReadingList
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface CurriculumAPI {

    // Simple GETs

    @GET("/lists")
    fun getReadingLists( @Query("full") full: String = "true" ): Single<List<ReadingList>>

    @GET("/books")
    fun getBooks( @Query("full") full: String = "true" ): Single<List<Book>>

    @GET("/authors")
    fun getAuthors(): Single<List<Author>>

    // GET by id

    @GET("/lists/{id}")
    fun getReadingList(
        @Path("id") id: Int,
        @Query("full") full: String = "true"
    ): Single<ReadingList>

    @GET("/books/{id}")
    fun getBook(
        @Path("id") id: Int,
        @Query("full") full: String = "true"
    ): Single<Book>

    @GET("/authors/{id}")
    fun getAuthor(@Path("id") id: Int): Single<Author>

    // Create with PUT

    @PUT("/lists/create")
    fun createReadingList(
        @Body model: ReadingList,
        @Query("full") full: String = "true"
    ): Single<ReadingList>

    @PUT("/books/create")
    fun createBook(
        @Body model: Book,
        @Query("full") full: String = "true"
    ): Single<Book>

    @PUT("/authors/create")
    fun createAuthor(@Body model: Author): Single<Author>

    // Update with POST

    @POST("/lists/update/{id}")
    fun updateReadingList(
        @Path("id") id: Int,
        @Body model: ReadingList,
        @Query("full") full: String = "true"
    ): Single<ReadingList>

    @POST("/books/update/{id}")
    fun updateBook(
        @Path("id") id: Int,
        @Body model: Book,
        @Query("full") full: String = "true"
    ): Single<Book>

    @POST("/authors/update/{id}")
    fun updateAuthor(
        @Path("id") id: Int,
        @Body model: Author
    ): Single<Author>

    // Remove with DELETE

    @DELETE("/lists/delete/{id}")
    fun deleteReadingList(@Path("id") id: Int): Completable

    @DELETE("/books/delete/{id}")
    fun deleteBook(@Path("id") id: Int): Completable

    @DELETE("/authors/delete/{id}")
    fun deleteAuthor(@Path("id") id: Int): Completable

    // Find with Forms

    @FormUrlEncoded
    @POST("/authors/find")
    fun findAuthor(
        @Field("firstName") firstName: String?,
        @Field("lastName") lastName: String?
    ): Single<Author>
}