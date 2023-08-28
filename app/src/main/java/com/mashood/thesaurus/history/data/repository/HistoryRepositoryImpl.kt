package com.mashood.thesaurus.history.data.repository

import com.mashood.thesaurus.app.common.Resource
import com.mashood.thesaurus.history.data.source.HistoryEntity
import com.mashood.thesaurus.history.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow

class HistoryRepositoryImpl: HistoryRepository {

    override fun getHistoriesList(): Flow<Resource<List<HistoryEntity>>> {
        TODO("Not yet implemented")
    }

}