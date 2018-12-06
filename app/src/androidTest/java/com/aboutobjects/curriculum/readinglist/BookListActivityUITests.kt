package com.aboutobjects.curriculum.readinglist

import android.content.Context
import androidx.core.content.edit
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.CoreMatchers.not
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class BookListActivityUITests {
    @Before
    fun clearSharedPreferences() {
        val context = ApplicationProvider.getApplicationContext<ReadingListApp>()
        context.getSharedPreferences(BookListActivity.PREF_FILE, Context.MODE_PRIVATE)
            .edit { clear() }
    }

    @Test
    fun lastLogin_isDisplayed() {
        val scenario = ActivityScenario.launch(BookListActivity::class.java)

        // Make sure the login_text is NOT displayed
        onView(withId(R.id.login_text))
            .check(matches(not(isDisplayed())))

        // Relaunch our activity a second time
        scenario.recreate()

        // Make sure the login_text is displayed
        onView(withId(R.id.login_text))
            .check(matches(isDisplayed()))
    }
}