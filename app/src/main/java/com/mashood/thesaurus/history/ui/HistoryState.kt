package com.mashood.thesaurus.history.ui

import com.mashood.thesaurus.history.data.source.HistoryEntity

sealed class HistoryState {

    data class SuccessHistoryList(val historyList: List<HistoryEntity>) : HistoryState()
    data class Error(val message: String) : HistoryState()
    object Idle : HistoryState()

}