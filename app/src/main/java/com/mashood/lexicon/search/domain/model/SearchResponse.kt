package com.mashood.lexicon.search.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
data class SearchResponse(
    val meanings: @RawValue List<MeaningModel>,
    val phonetics: @RawValue List<PhoneticModel>,
    val sourceUrls: List<String>,
    val word: String
): Parcelable {

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