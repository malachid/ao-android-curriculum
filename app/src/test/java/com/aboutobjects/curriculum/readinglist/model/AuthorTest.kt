package com.aboutobjects.curriculum.readinglist.model

import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class AuthorTest {
    @Test
    fun deserialize() {
        val json = "{ \"firstName\" : \"Ernest\", \"lastName\" : \"Hemingway\" }"
        val author = Gson().fromJson<Author>(json, Author::class.java)
        Assert.assertEquals("Ernest", author.firstName)
        Assert.assertEquals("Hemingway", author.lastName)
    }
}