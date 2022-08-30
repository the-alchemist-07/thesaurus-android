package com.mashood.thesaurus.search.domain.model

import androidx.annotation.Keep

@Keep
data class SearchResponse(
    val meanings: List<MeaningModel>,
    val phonetics: List<PhoneticModel>,
    val sourceUrls: List<String>,
    val word: String
) {

    data class MeaningModel(
        val antonyms: List<String>,
        val definitions: List<DefinitionModel>,
        val partOfSpeech: String,
        val synonyms: List<String>
    ) {
        data class DefinitionModel(
            val antonyms: List<Any>,
            val definition: String,
            val example: String,
            val synonyms: List<Any>
        )
    }

    data class PhoneticModel(
        val audio: String,
        val sourceUrl: String,
        val text: String
    )
}