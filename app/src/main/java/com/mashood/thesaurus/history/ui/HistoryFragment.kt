package com.mashood.thesaurus.history.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.mashood.thesaurus.R
import com.mashood.thesaurus.databinding.FragmentHistoryBinding
import com.mashood.thesaurus.history.domain.model.History
import com.mashood.thesaurus.history.ui.adapters.HistoryAdapter
import com.mashood.thesaurus.search.domain.model.SearchResponse
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryFragment : Fragment(R.layout.fragment_history),
    HistoryAdapter.OnItemClickListener {

    private lateinit var binding: FragmentHistoryBinding
    private val viewModel by viewModels<HistoryViewModel>()
    private val historyAdapter: HistoryAdapter by lazy { HistoryAdapter(this) }
    private var historiesList: List<SearchResponse> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHistoryBinding.bind(view)

        setUpRecyclerView()
        observeState()
    }

    private fun setUpRecyclerView() {
        binding.recycler.adapter = historyAdapter
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.historyState.collectLatest {
                    when (it) {
                        is HistoryState.SuccessHistoryList -> showHistoriesList(it.historyList)
                        is HistoryState.Error -> handleError(it.message)
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showHistoriesList(historyList: List<History>) {

    }

    private fun handleError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onWordClicked(word: String) {
        TODO("Not yet implemented")
    }

    override fun onWordRemoveClicked(word: String) {
        TODO("Not yet implemented")
    }
}
