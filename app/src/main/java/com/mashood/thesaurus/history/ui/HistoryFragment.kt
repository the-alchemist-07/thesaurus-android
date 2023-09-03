package com.mashood.thesaurus.history.ui

import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mashood.thesaurus.R
import com.mashood.thesaurus.databinding.FragmentHistoryBinding
import com.mashood.thesaurus.history.data.source.HistoryEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var binding: FragmentHistoryBinding
    private val viewModel by viewModels<HistoryViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHistoryBinding.bind(view)

        observeState()
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

    private fun showHistoriesList(historyList: List<HistoryEntity>) {

    }

    private fun handleError(message: String) {

    }
}
