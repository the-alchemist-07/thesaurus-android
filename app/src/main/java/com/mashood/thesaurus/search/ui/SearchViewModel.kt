package com.mashood.thesaurus.search.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mashood.thesaurus.app.common.states.Resource
import com.mashood.thesaurus.bookmark.data.mapper.toBookmarkEntity
import com.mashood.thesaurus.bookmark.data.source.BookmarkDao
import com.mashood.thesaurus.history.domain.model.History
import com.mashood.thesaurus.history.domain.repository.HistoryRepository
import com.mashood.thesaurus.search.domain.model.SearchResponse
import com.mashood.thesaurus.search.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val historyRepository: HistoryRepository,
    private val bookmarkDao: BookmarkDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState = _searchState.asStateFlow()

    // Argument received from BookmarkFragment, full search response will be there.
    private val wordData = savedStateHandle.get<SearchResponse>("wordData")
    fun getWordData() = wordData

    // Argument received from HistoryFragment, just the word will be there.
    private val word = savedStateHandle.get<String>("word")
    fun getWord() = word

    /*init {
        getHistoriesList()
    }*/

    fun checkKeyword(keyword: String?) = viewModelScope.launch {
        when {
            keyword.isNullOrBlank() -> _searchState.emit(SearchState.Error(EMPTY_KEYWORD))
            else -> searchKeyword(keyword)
        }
    }

    fun searchKeyword(keyword: String) = viewModelScope.launch(Dispatchers.IO) {
        searchRepository.searchKeyword(keyword).collect { state ->
            when (state) {
                is Resource.Loading -> _searchState.emit(SearchState.Loading)
                is Resource.Success -> {
                    _searchState.emit(SearchState.SearchSuccess(state.value))
                    // Check whether the word is already bookmarked before
                    isWordBookmarked(keyword)
                    // Add the searched word to history
                    addWordToHistory(word = state.value.word)
                }
                is Resource.Error -> _searchState.emit(SearchState.Error(state.error))
            }
        }
    }

    fun getHistoriesList() = viewModelScope.launch(Dispatchers.IO) {
        historyRepository.getHistoriesList().collect { state ->
            when (state) {
                is Resource.Success -> _searchState.emit(
                    SearchState.HistoryList(state.value)
                )
                is Resource.Error -> _searchState.emit(
                    SearchState.Error(state.error)
                )
                else -> Unit
            }
        }
    }

    fun addToBookmarks(data: SearchResponse) = viewModelScope.launch(Dispatchers.IO) {
        bookmarkDao.insertBookmark(data.toBookmarkEntity())
    }

    fun removeFromBookmarks(data: SearchResponse) = viewModelScope.launch(Dispatchers.IO) {
        bookmarkDao.deleteBookmark(data.toBookmarkEntity())
    }

    private fun isWordBookmarked(word: String) = viewModelScope.launch(Dispatchers.IO) {
        val count = bookmarkDao.checkBookmarked(word)
        _searchState.emit(SearchState.CheckBookmarked(count != 0))
    }

    private fun addWordToHistory(word: String) = viewModelScope.launch(Dispatchers.IO) {
        historyRepository.addHistory(
            History(word)
        )
        // Updates the history list
        getHistoriesList()
    }

    companion object {
        const val EMPTY_KEYWORD = "Please enter a keyword"
    }

}