package com.mashood.thesaurus.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mashood.thesaurus.app.common.Resource
import com.mashood.thesaurus.search.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
): ViewModel() {

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
            when(it) {
                is Resource.Loading -> _searchState.emit(SearchState.Loading)
                is Resource.Success -> _searchState.emit(SearchState.SearchSuccess(it.value))
                is Resource.Error -> _searchState.emit(SearchState.Error(it.error))
            }
        }
    }

    companion object {
        const val EMPTY_KEYWORD = "Please enter a keyword"
    }

}