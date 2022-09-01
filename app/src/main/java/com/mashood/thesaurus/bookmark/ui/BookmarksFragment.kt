package com.mashood.thesaurus.bookmark.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.mashood.thesaurus.R
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBookmarksBinding.bind(view)

        setupRecyclerView()
        observeState()
        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun handleBookmarksList(bookmarks: List<SearchResponse>) {
        bookmarkAdapter.submitList(bookmarks)
    }

    override fun onItemClicked(data: SearchResponse) {
//        TODO("Not yet implemented")
    }

}