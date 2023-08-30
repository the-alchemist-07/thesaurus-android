package com.mashood.thesaurus.history.ui

import androidx.lifecycle.ViewModel
import com.mashood.thesaurus.history.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
): ViewModel() {

    private val _historyState = MutableStateFlow<HistoryState>(HistoryState.Idle)
    val historyState = _historyState.asStateFlow()

}