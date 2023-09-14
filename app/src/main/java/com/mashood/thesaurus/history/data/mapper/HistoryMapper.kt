package com.mashood.thesaurus.history.data.mapper

import com.mashood.thesaurus.history.data.source.HistoryEntity

fun String.toHistoryEntity(): HistoryEntity {
    return HistoryEntity(
        word = this
    )
}

fun HistoryEntity.toString(): String {
    return word
}
