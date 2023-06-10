package com.mashood.lexicon.search.ui

import com.mashood.lexicon.search.domain.model.SearchResponse

sealed class SearchState {
    data class SearchSuccess(val searchResponse: SearchResponse): SearchState()
    data class Error(val message: String): SearchState()
    data class CheckBookmarked(val isBookmarked: Boolean): SearchState()
    object Loading: SearchState()
    object Idle: SearchState()
}
