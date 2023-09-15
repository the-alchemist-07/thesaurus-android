package com.mashood.thesaurus.history.domain.repository

import com.mashood.thesaurus.app.common.Resource
import com.mashood.thesaurus.history.domain.model.History
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    fun getHistoriesList(): Flow<Resource<List<History>>>

    suspend fun addHistory(history: History)

    suspend fun deleteHistory(history: History)

}