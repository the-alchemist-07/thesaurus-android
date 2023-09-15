package com.mashood.thesaurus.history.data.repository

import com.mashood.thesaurus.app.common.Resource
import com.mashood.thesaurus.history.data.mapper.toHistory
import com.mashood.thesaurus.history.data.mapper.toHistoryEntity
import com.mashood.thesaurus.history.data.source.HistoryDao
import com.mashood.thesaurus.history.domain.model.History
import com.mashood.thesaurus.history.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
): HistoryRepository {

    override fun getHistoriesList(): Flow<Resource<List<History>>> = flow {
        val historyEntityList = historyDao.getHistories().first()
        if (historyEntityList.isNotEmpty()) {
            val historyList = historyEntityList.reversed().map { it.toHistory() }
            emit(Resource.Success(historyList))
        }
        else
            emit(Resource.Error(EMPTY_HISTORY))
    }

    override suspend fun addHistory(history: History) {
        historyDao.insertHistory(history.toHistoryEntity())
    }

    override suspend fun deleteHistory(history: History): Flow<Int> = flow {
        emit(historyDao.deleteHistory(history.toHistoryEntity()))
    }

    companion object {
        const val EMPTY_HISTORY = "No history found!"
    }
}