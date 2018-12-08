package com.aboutobjects.curriculum.readinglist.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.aboutobjects.curriculum.readinglist.R
import com.aboutobjects.curriculum.readinglist.ReadingListApp
import com.aboutobjects.curriculum.readinglist.databinding.FragmentEditBookBinding
import com.aboutobjects.curriculum.readinglist.model.Book
import com.aboutobjects.curriculum.readinglist.model.EditableBook
import com.aboutobjects.curriculum.readinglist.service.BookListService
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class EditBookFragment: Fragment() {
    companion object {
        val PARAM_SOURCE_BOOK = "${EditBookFragment::class.java}.name::source"

        @JvmStatic
        fun newInstance(app: ReadingListApp, source: Book? = null): EditBookFragment {
            return EditBookFragment().apply {
                arguments = Bundle().also { bundle ->
                    source?.let { book ->
                        bundle.putString(PARAM_SOURCE_BOOK, app.gson.toJson(book))
                    }
                }
            }
        }
    }

    private var app: ReadingListApp? = null
    private var bookListService: BookListService? = null
    private lateinit var binding: FragmentEditBookBinding
    private val paramSource: String? by lazy { arguments?.getString(PARAM_SOURCE_BOOK) }
    private val sourceBook: Book? by lazy { app?.gson?.fromJson(paramSource, Book::class.java) }

    /**
     * Called when a fragment is first attached to its context.
     * [.onCreate] will be called after this.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        app = context.applicationContext as? ReadingListApp
        bookListService = app?.bookListService
    }

    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after [.onDestroy].
     */
    override fun onDetach() {
        super.onDetach()
        bookListService = null
        app = null
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * [.onCreate] and [.onActivityCreated].
     *
     *
     * If you return a View from here, you will later be called in
     * [.onDestroyView] when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_book, container,false)
        sourceBook?.let {
            binding.book = EditableBook(source = it)
        } ?: let {
            binding.book = EditableBook()
        }
        return binding.root
    }

    /**
     * Initialize the contents of the Fragment host's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called [.setHasOptionsMenu].  See
     * [Activity.onCreateOptionsMenu]
     * for more information.
     *
     * @param menu The options menu in which you place your items.
     *
     * @see .setHasOptionsMenu
     *
     * @see .onPrepareOptionsMenu
     *
     * @see .onOptionsItemSelected
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit_book, menu)
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * [.setRetainInstance] to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after [.onCreateView]
     * and before [.onViewStateRestored].
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_save -> {
                binding.book?.let { editableBook ->
                    bookListService?.let { service ->
                        service.edit(
                            source = editableBook.source,
                            edited = editableBook.edited() )
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy(
                                onComplete = {
                                    // It's updated, so we are done
                                    activity?.supportFragmentManager?.popBackStack()
                                },
                                onError = { t ->
                                    t.message?.let { msg ->
                                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
                                            .show()
                                    }
                                }
                            )
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}