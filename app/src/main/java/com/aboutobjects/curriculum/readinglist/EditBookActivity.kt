package com.aboutobjects.curriculum.readinglist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.aboutobjects.curriculum.readinglist.databinding.ActivityEditBookBinding
import com.aboutobjects.curriculum.readinglist.model.Book
import com.aboutobjects.curriculum.readinglist.model.EditableBook

class EditBookActivity: AppCompatActivity() {
    companion object {
        val EXTRA_SOURCE_BOOK = "${EditBookActivity::class.java}.name::source"
        val EXTRA_EDITED_BOOK = "${EditBookActivity::class.java}.name::edited"

        fun getIntent(context: Context, source: Book? = null, edited: Book? = null): Intent {
            val gson = (context.applicationContext as ReadingListApp).gson
            return Intent(context, EditBookActivity::class.java).apply {
                source?.let {
                    putExtra(EXTRA_SOURCE_BOOK, gson.toJson(source))
                }
                edited?.let {
                    putExtra(EXTRA_EDITED_BOOK, gson.toJson(edited))
                }
            }
        }
    }

    private val extraBook: String? by lazy { intent?.getStringExtra(EXTRA_SOURCE_BOOK) }
    private val app: ReadingListApp by lazy { application as ReadingListApp }
    private lateinit var binding: ActivityEditBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_book)
        extraBook?.let {
            binding.book = EditableBook(
                source = app.gson.fromJson(it, Book::class.java)
            )
        } ?: let {
            binding.book = EditableBook()
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     *
     *
     * This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * [.onPrepareOptionsMenu].
     *
     *
     * The default implementation populates the menu with standard system
     * menu items.  These are placed in the [Menu.CATEGORY_SYSTEM] group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     *
     *
     * You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     *
     *
     * When you add items to the menu, you can implement the Activity's
     * [.onOptionsItemSelected] method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     *
     * @see .onPrepareOptionsMenu
     *
     * @see .onOptionsItemSelected
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_book, menu)
        return true
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     *
     *
     * Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     *
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     *
     * @see .onCreateOptionsMenu
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.action_save -> {
                binding.book?.let {
                    setResult(RESULT_OK, getIntent(
                        context = this,
                        source = it.source,
                        edited = it.edited()
                    ))
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}