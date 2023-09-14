package com.mashood.thesaurus.history.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mashood.thesaurus.app.common.Resource
import com.mashood.thesaurus.history.domain.model.History
import com.mashood.thesaurus.history.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _historyState = MutableStateFlow<HistoryState>(HistoryState.Idle)
    val historyState = _historyState.asStateFlow()

    init {
        getHistoriesList()
    }

    private fun getHistoriesList() = viewModelScope.launch {
        historyRepository.getHistoriesList().collect { state ->
            when (state) {
                is Resource.Success -> _historyState.emit(
                    HistoryState.SuccessHistoryList(state.value)
                )
                is Resource.Error -> _historyState.emit(
                    HistoryState.Error(state.error)
                )
                else -> Unit
            }
        }
    }

    fun removeWordFromHistory(history: History) = viewModelScope.launch {
        historyRepository.deleteHistory(history)
    }
}