package com.mashood.thesaurus.history.ui

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import com.google.android.material.snackbar.Snackbar
import com.mashood.thesaurus.R
import com.mashood.thesaurus.app.common.Constants.EMPTY_HISTORY
import com.mashood.thesaurus.databinding.FragmentHistoryBinding
import com.mashood.thesaurus.history.domain.model.History
import com.mashood.thesaurus.history.ui.adapters.HistoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history),
    HistoryAdapter.OnItemClickListener {

    private lateinit var binding: FragmentHistoryBinding
    private val viewModel by viewModels<HistoryViewModel>()
    private val historyAdapter: HistoryAdapter by lazy { HistoryAdapter(this) }
    private var historyList: List<History> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHistoryBinding.bind(view)

        // Customize the transitions
        sharedElementEnterTransition = ChangeBounds().apply {
            duration = 400
        }
        sharedElementReturnTransition = ChangeBounds().apply {
            duration = 200
        }

        setUpRecyclerView()
        setListeners()
        observeState()
    }

    private fun setUpRecyclerView() {
        binding.recycler.adapter = historyAdapter
    }

    private fun setListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            etSearch.doAfterTextChanged { text ->
                if (text.toString().isNotBlank()) {
                    btnClear.visibility = View.VISIBLE
                    if (historyList.isNotEmpty()) {
                        //new array list that will hold the filtered data
                        val filteredList = historyList.filter { history ->
                            history.word.lowercase().startsWith(text.toString().lowercase())
                        }
                        historyAdapter.submitList(filteredList)
                    }
                } else {
                    btnClear.visibility = View.GONE
                    // Set the complete history list on clearing the search query
                    if (historyList.isNotEmpty())
                        historyAdapter.submitList(historyList)
                }
            }

            btnClear.setOnClickListener {
                etSearch.text?.clear()
            }
        }
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

    private fun showHistoriesList(historiesList: List<History>) {
        historyList = historiesList
        historyAdapter.submitList(historyList)
    }

    private fun handleError(message: String) {
        if (EMPTY_HISTORY == message) {
            historyList = emptyList()
            historyAdapter.submitList(historyList)
            binding.lytError.visibility = View.VISIBLE
        } else
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onHistoryWordClicked(history: History) {
        findNavController().navigate(
            HistoryFragmentDirections.actionHistoryFragmentToSearchFragment(
                wordData = null,
                word = history.word
            )
        )
    }

    override fun onHistoryWordRemoveClicked(history: History) {
        viewModel.removeWordFromHistory(history)
    }
}
