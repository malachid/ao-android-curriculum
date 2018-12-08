package com.aboutobjects.curriculum.readinglist.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aboutobjects.curriculum.readinglist.BookListV2Activity
import com.aboutobjects.curriculum.readinglist.R
import com.aboutobjects.curriculum.readinglist.ReadingListApp
import com.aboutobjects.curriculum.readinglist.databinding.FragmentBookListBinding
import com.aboutobjects.curriculum.readinglist.service.BookListService
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class BookListFragment: Fragment() {

    private lateinit var binding: FragmentBookListBinding
    private var app: ReadingListApp? = null
    private var bookListService: BookListService? = null
    private var fragManager: FragmentManager? = null
    private var disposable: Disposable? = null

    private val viewAdapter = ReadingListAdapter(
        bookClicked = { book ->
            app?.let { readingListApp ->
                fragManager?.let {
                    it.beginTransaction()
                    .replace(R.id.container, EditBookFragment.newInstance(
                        app = readingListApp,
                        source= book))
                    .addToBackStack(null)
                    .commit()
                }
            }
        }
    )

    /**
     * Called when a fragment is first attached to its context.
     * [.onCreate] will be called after this.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        app = context.applicationContext as? ReadingListApp
        bookListService = app?.bookListService
        fragManager = (context as BookListV2Activity).supportFragmentManager
    }

    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after [.onDestroy].
     */
    override fun onDetach() {
        super.onDetach()
        disposable?.dispose()
        bookListService = null
        app = null
        fragManager = null
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_list, container, false)

        binding.recycler.apply {
            setHasFixedSize(true)
            activity?.let {
                layoutManager = LinearLayoutManager(it)
                addItemDecoration(CustomDivider(context = it as Context))
            }
            adapter = viewAdapter
        }

        binding.fab.setOnClickListener {
            app?.let { readingListApp ->
                fragManager?.let {
                    it.beginTransaction()
                    .replace(R.id.container, EditBookFragment.newInstance(app = readingListApp))
                    .addToBackStack(null)
                    .commit()
                }
            }
        }

        bookListService?.let { service ->
            disposable = service.readingList
                .toFlowable(BackpressureStrategy.LATEST)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.i(ReadingListApp.TAG, "${it.books.size} books loaded")
                    viewAdapter.readingList = it
                }
        }

        return binding.root
    }
}