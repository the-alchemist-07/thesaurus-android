package com.mashood.thesaurus.bookmark.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import com.mashood.thesaurus.R
import com.mashood.thesaurus.app.common.constants.Constants.EMPTY_BOOKMARK
import com.mashood.thesaurus.bookmark.ui.adapters.BookmarkAdapter
import com.mashood.thesaurus.databinding.FragmentBookmarksBinding
import com.mashood.thesaurus.search.domain.model.SearchResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookmarksFragment : Fragment(R.layout.fragment_bookmarks),
    BookmarkAdapter.OnItemClickListener {

    private lateinit var binding: FragmentBookmarksBinding
    private val viewModel by viewModels<BookmarkViewModel>()
    private val bookmarkAdapter: BookmarkAdapter by lazy { BookmarkAdapter(this) }
    private var bookmarksList: List<SearchResponse> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBookmarksBinding.bind(view)

        setupTransitions()
        setupRecyclerView()
        observeState()
        setListeners()
    }

    private fun setupTransitions() {
        // Customize the transitions
        sharedElementEnterTransition = ChangeBounds().apply {
            duration = 400
        }
        sharedElementReturnTransition = ChangeBounds().apply {
            duration = 200
        }
    }

    private fun setListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            etSearch.doAfterTextChanged { text ->
                if (text.toString().isNotBlank()) {
                    btnClear.visibility = View.VISIBLE
                    if (bookmarksList.isNotEmpty()) {
                        //new array list that will hold the filtered data
                        val filteredList = bookmarksList.filter { bookmark ->
                            bookmark.word.lowercase().startsWith(text.toString().lowercase())
                        }
                        bookmarkAdapter.submitList(filteredList)
                    }
                } else {
                    btnClear.visibility = View.GONE
                    // Set the complete bookmarks list on clearing the search query
                    if (bookmarksList.isNotEmpty())
                        bookmarkAdapter.submitList(bookmarksList)
                }
            }

            btnClear.setOnClickListener {
                etSearch.text?.clear()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recycler.adapter = bookmarkAdapter
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookmarkState.collect {
                    when (it) {
                        is BookmarkState.SuccessBookmarks -> handleBookmarksList(it.bookmarks)
                        is BookmarkState.Error -> handleError(it.message)
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun handleError(message: String) {
        if (EMPTY_BOOKMARK == message) {
            bookmarksList = emptyList()
            bookmarkAdapter.submitList(bookmarksList)
            binding.lytError.visibility = View.VISIBLE
        } else
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun handleBookmarksList(bookmarks: List<SearchResponse>) {
        bookmarksList = bookmarks
        bookmarkAdapter.submitList(bookmarksList)
    }

    override fun onItemClicked(data: SearchResponse) {
        findNavController().navigate(
            BookmarksFragmentDirections.actionBookmarksFragmentToSearchFragment(
                wordData = data,
                word = null
            )
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.getBookmarksList()
    }

}