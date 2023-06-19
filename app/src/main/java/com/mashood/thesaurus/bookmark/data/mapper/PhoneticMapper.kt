package com.mashood.thesaurus.bookmark.data.mapper

import com.mashood.thesaurus.bookmark.data.source.PhoneticEntity
import com.mashood.thesaurus.search.domain.model.SearchResponse

fun SearchResponse.PhoneticModel.toPhoneticEntity(): PhoneticEntity {
    return PhoneticEntity(
        audio = audio,
        sourceUrl = sourceUrl,
        text = text
    )
}

fun PhoneticEntity.toPhoneticModel(): SearchResponse.PhoneticModel {
    return SearchResponse.PhoneticModel(
        audio = audio,
        sourceUrl = sourceUrl,
        text = text
    )
}