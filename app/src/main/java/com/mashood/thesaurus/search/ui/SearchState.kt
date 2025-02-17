package com.mashood.thesaurus.search.ui

import com.mashood.thesaurus.history.domain.model.History
import com.mashood.thesaurus.search.domain.model.SearchResponse

sealed class SearchState {
    data class SearchSuccess(val searchResponse: SearchResponse): SearchState()
    data class Error(val message: String): SearchState()
    data class CheckBookmarked(val isBookmarked: Boolean): SearchState()
    data class HistoryList(val historyList: List<History>): SearchState()
    data object Loading: SearchState()
    data object Idle: SearchState()
}
