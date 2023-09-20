package com.mashood.thesaurus.history.data.mapper

import com.mashood.thesaurus.history.data.source.HistoryEntity
import com.mashood.thesaurus.history.domain.model.History

fun History.toHistoryEntity(): HistoryEntity {
    return HistoryEntity(
        word = word
    )
}

fun HistoryEntity.toHistory(): History {
    return History(
        word = word
    )
}
