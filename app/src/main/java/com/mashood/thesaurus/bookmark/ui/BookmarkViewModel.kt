package com.mashood.thesaurus.bookmark.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mashood.thesaurus.app.common.Resource
import com.mashood.thesaurus.bookmark.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
): ViewModel() {

    private val _bookmarkState = MutableStateFlow<BookmarkState>(BookmarkState.Idle)
    val bookmarkState = _bookmarkState.asStateFlow()

    init {
        getBookmarksList()
    }

    private fun getBookmarksList() = viewModelScope.launch {
        bookmarkRepository.getBookmarksList().collect {
            when(it) {
                is Resource.Success -> _bookmarkState.emit(BookmarkState.SuccessBookmarks(it.value))
                is Resource.Error -> _bookmarkState.emit(BookmarkState.Error(it.error))
                else -> Unit
            }
        }
    }

}