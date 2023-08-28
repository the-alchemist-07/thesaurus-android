package com.mashood.thesaurus.history.ui

import androidx.lifecycle.ViewModel
import com.mashood.thesaurus.history.domain.repository.HistoryRepository
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
): ViewModel() {
}