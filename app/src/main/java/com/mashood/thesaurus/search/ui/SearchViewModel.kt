package com.mashood.thesaurus.search.ui

import androidx.lifecycle.ViewModel
import com.mashood.thesaurus.search.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
): ViewModel() {



}