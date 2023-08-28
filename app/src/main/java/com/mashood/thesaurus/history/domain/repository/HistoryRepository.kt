package com.mashood.thesaurus.history.domain.repository

import com.mashood.thesaurus.app.common.Resource
import com.mashood.thesaurus.history.data.source.HistoryEntity
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    fun getHistoriesList(): Flow<Resource<List<HistoryEntity>>>

}