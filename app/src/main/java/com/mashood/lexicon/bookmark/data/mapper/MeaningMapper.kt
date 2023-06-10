package com.mashood.lexicon.bookmark.data.mapper

import com.mashood.lexicon.bookmark.data.source.MeaningEntity
import com.mashood.lexicon.search.domain.model.SearchResponse

fun SearchResponse.MeaningModel.toMeaningEntity(): MeaningEntity {
    return MeaningEntity(
        antonyms = antonyms,
        definitions = definitions.map { it.toDefinitionEntity() },
        partOfSpeech = partOfSpeech,
        synonyms = synonyms
    )
}

fun MeaningEntity.toMeaningModel(): SearchResponse.MeaningModel {
    return SearchResponse.MeaningModel(
        antonyms = antonyms,
        definitions = definitions.map { it.toDefinitionModel() },
        partOfSpeech = partOfSpeech,
        synonyms = synonyms
    )
}