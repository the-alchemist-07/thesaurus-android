package com.mashood.thesaurus.history.data.source

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class HistoryEntity(
    @PrimaryKey
    val word: String
)
