package com.mashood.thesaurus.history.data.repository

import com.mashood.thesaurus.app.common.Resource
import com.mashood.thesaurus.history.data.source.HistoryDao
import com.mashood.thesaurus.history.data.source.HistoryEntity
import com.mashood.thesaurus.history.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
): HistoryRepository {

    override fun getHistoriesList(): Flow<Resource<List<HistoryEntity>>> = flow {
        val histories = historyDao.getHistories().first()
        if (histories.isNotEmpty())
            emit(Resource.Success(histories))
        else
            emit(Resource.Error(EMPTY_HISTORY))
    }

    companion object {
        const val EMPTY_HISTORY = "No history found!"
    }
}