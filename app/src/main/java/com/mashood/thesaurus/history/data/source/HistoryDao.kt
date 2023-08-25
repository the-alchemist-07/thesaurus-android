package com.mashood.thesaurus.history.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Delete
    suspend fun deleteHistory(history: HistoryEntity)

    @Query("DELETE FROM history_table")
    suspend fun deleteAllHistories()

    @Query("SELECT * FROM history_table")
    fun getHistories(): Flow<List<HistoryEntity>>

}