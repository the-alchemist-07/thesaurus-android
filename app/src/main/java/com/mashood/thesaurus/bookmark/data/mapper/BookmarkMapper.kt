package com.mashood.thesaurus.bookmark.data.mapper

import com.mashood.thesaurus.bookmark.data.source.BookmarkEntity
import com.mashood.thesaurus.search.domain.model.SearchResponse

fun SearchResponse.toBookmarkEntity(): BookmarkEntity {
    return BookmarkEntity(
        word = word,
        meanings = meanings.map { it.toMeaningEntity() },
        phonetics = phonetics.map { it.toPhoneticEntity() },
        sourceUrls = sourceUrls
    )
}

fun BookmarkEntity.toSearchResponse(): SearchResponse {
    return SearchResponse(
        meanings = meanings.map { it.toMeaningModel() },
        phonetics = phonetics.map { it.toPhoneticModel() },
        sourceUrls = sourceUrls,
        word = word
    )
}