package com.aboutobjects.curriculum.readinglist

import android.content.Context
import androidx.core.content.edit
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookListActivityUITests {
    @Before
    fun clearSharedPreferences() {
        val context = ApplicationProvider.getApplicationContext<ReadingListApp>()
        context.getSharedPreferences(BookListActivity.PREF_FILE, Context.MODE_PRIVATE)
            .edit { clear() }
    }

    @Test
    fun recycler_isDisplayed() {
        val scenario = ActivityScenario.launch(BookListActivity::class.java)

        onView(withId(R.id.recycler))
            .check(matches(isDisplayed()))
    }
}