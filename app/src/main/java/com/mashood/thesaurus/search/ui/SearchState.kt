package com.mashood.thesaurus.search.ui

import com.mashood.thesaurus.search.domain.model.SearchResponse

sealed class SearchState {
    data class SearchSuccess(val searchResponse: SearchResponse): SearchState()
    data class Error(val message: String): SearchState()
    object Loading: SearchState()
    object Idle: SearchState()
}
