package com.mashood.thesaurus.search.data.dto

import androidx.annotation.Keep
import com.mashood.thesaurus.search.domain.model.SearchResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class SearchResponseDtoItem(
    @Json(name = "license")
    val license: License?,
    @Json(name = "meanings")
    val meanings: List<Meaning>?,
    @Json(name = "phonetics")
    val phonetics: List<Phonetic>?,
    @Json(name = "sourceUrls")
    val sourceUrls: List<String>?,
    @Json(name = "word")
    val word: String?
) {
    @JsonClass(generateAdapter = true)
    @Keep
    data class License(
        @Json(name = "name")
        val name: String?,
        @Json(name = "url")
        val url: String?
    )

    @JsonClass(generateAdapter = true)
    @Keep
    data class Meaning(
        @Json(name = "antonyms")
        val antonyms: List<String>?,
        @Json(name = "definitions")
        val definitions: List<Definition>?,
        @Json(name = "partOfSpeech")
        val partOfSpeech: String?,
        @Json(name = "synonyms")
        val synonyms: List<String>?
    ) {
        @JsonClass(generateAdapter = true)
        @Keep
        data class Definition(
            @Json(name = "antonyms")
            val antonyms: List<String>?,
            @Json(name = "definition")
            val definition: String?,
            @Json(name = "example")
            val example: String?,
            @Json(name = "synonyms")
            val synonyms: List<String>?
        ) {
            fun toDefinitionModel(): SearchResponse.MeaningModel.DefinitionModel {
                return SearchResponse.MeaningModel.DefinitionModel(
                    definition = definition ?: "",
                    antonyms = antonyms ?: emptyList(),
                    synonyms = synonyms ?: emptyList(),
                    example = example ?: ""
                )
            }
        }

        fun toMeaningModel(): SearchResponse.MeaningModel {
            return SearchResponse.MeaningModel(
                partOfSpeech = partOfSpeech ?: "",
                definitions = definitions?.map { it.toDefinitionModel() } ?: emptyList(),
                synonyms = synonyms ?: emptyList(),
                antonyms = antonyms ?: emptyList()
            )
        }
    }

    @JsonClass(generateAdapter = true)
    @Keep
    data class Phonetic(
        @Json(name = "audio")
        val audio: String?,
        @Json(name = "license")
        val license: License?,
        @Json(name = "sourceUrl")
        val sourceUrl: String?,
        @Json(name = "text")
        val text: String?
    ) {
        @JsonClass(generateAdapter = true)
        @Keep
        data class License(
            @Json(name = "name")
            val name: String?,
            @Json(name = "url")
            val url: String?
        )

        fun toPhoneticModel(): SearchResponse.PhoneticModel {
            return SearchResponse.PhoneticModel(
                text = text ?: "",
                audio = audio ?: "",
                sourceUrl = sourceUrl ?: "",
            )
        }
    }

    fun toSearchResponseModel(): SearchResponse {
        return SearchResponse(
            meanings = meanings?.map { it.toMeaningModel() } ?: emptyList(),
            phonetics = phonetics?.map { it.toPhoneticModel() } ?: emptyList(),
            sourceUrls = sourceUrls ?: emptyList(),
            word = word ?: ""
        )
    }
}