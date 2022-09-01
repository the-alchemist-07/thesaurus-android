package com.mashood.thesaurus.bookmark.ui

import com.mashood.thesaurus.search.domain.model.SearchResponse

sealed class BookmarkState {
    data class SuccessBookmarks(val bookmarks: List<SearchResponse>) : BookmarkState()
    data class Error(val message: String) : BookmarkState()
    object Idle : BookmarkState()
}