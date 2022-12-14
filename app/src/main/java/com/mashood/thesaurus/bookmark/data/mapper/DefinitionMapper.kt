package com.mashood.thesaurus.bookmark.data.mapper

import com.mashood.thesaurus.bookmark.data.source.MeaningEntity
import com.mashood.thesaurus.search.domain.model.SearchResponse

fun SearchResponse.MeaningModel.DefinitionModel.toDefinitionEntity(): MeaningEntity.DefinitionEntity {
    return MeaningEntity.DefinitionEntity(
        antonyms = antonyms,
        definition = definition,
        example = example,
        synonyms = synonyms
    )
}

fun MeaningEntity.DefinitionEntity.toDefinitionModel(): SearchResponse.MeaningModel.DefinitionModel {
    return SearchResponse.MeaningModel.DefinitionModel(
        antonyms = antonyms,
        definition = definition,
        example = example,
        synonyms = synonyms
    )
}