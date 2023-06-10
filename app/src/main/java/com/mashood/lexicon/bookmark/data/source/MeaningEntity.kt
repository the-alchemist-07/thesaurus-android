package com.mashood.lexicon.bookmark.data.source

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MeaningEntity(
    val antonyms: List<String>,
    val definitions: List<DefinitionEntity>,
    val partOfSpeech: String,
    val synonyms: List<String>
) {
    @JsonClass(generateAdapter = true)
    data class DefinitionEntity(
        val antonyms: List<Any>,
        val definition: String,
        val example: String,
        val synonyms: List<Any>
    )
}