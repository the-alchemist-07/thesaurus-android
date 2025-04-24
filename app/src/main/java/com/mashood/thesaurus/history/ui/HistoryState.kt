package com.mashood.thesaurus.history.ui

import com.mashood.thesaurus.history.domain.model.History

sealed class HistoryState {
    data class SuccessHistoryList(val historyList: List<History>) : HistoryState()
    data class Error(val message: String) : HistoryState()
    data object Idle : HistoryState()
}