package com.mashood.thesaurus.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mashood.thesaurus.app.common.Resource
import com.mashood.thesaurus.bookmark.data.mapper.toBookmarkEntity
import com.mashood.thesaurus.bookmark.data.source.BookmarkDao
import com.mashood.thesaurus.search.domain.model.SearchResponse
import com.mashood.thesaurus.search.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val bookmarkDao: BookmarkDao
) : ViewModel() {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState = _searchState.asStateFlow()


    fun checkKeyword(keyword: String) = viewModelScope.launch {
        when {
            keyword.isBlank() -> _searchState.emit(SearchState.Error(EMPTY_KEYWORD))
            else -> searchKeyword(keyword)
        }
    }

    private fun searchKeyword(keyword: String) = viewModelScope.launch {
        searchRepository.searchKeyword(keyword).collect {
            when (it) {
                is Resource.Loading -> _searchState.emit(SearchState.Loading)
                is Resource.Success -> {
                    _searchState.emit(SearchState.SearchSuccess(it.value))
                    isWordBookmarked(keyword)
                }
                is Resource.Error -> _searchState.emit(SearchState.Error(it.error))
            }
        }
    }

    fun addToBookmarks(data: SearchResponse) = viewModelScope.launch {
        bookmarkDao.insertBookmark(data.toBookmarkEntity())
    }

    fun removeFromBookmarks(data: SearchResponse) = viewModelScope.launch {
        bookmarkDao.deleteBookmark(data.toBookmarkEntity())
    }

    private fun isWordBookmarked(word: String) = CoroutineScope(Dispatchers.IO).launch {
        val count = bookmarkDao.checkBookmarked(word)
        _searchState.emit(SearchState.CheckBookmarked(count != 0))
    }

    companion object {
        const val EMPTY_KEYWORD = "Please enter a keyword"
    }

}