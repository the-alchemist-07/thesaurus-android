package com.mashood.thesaurus.search.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.mashood.thesaurus.app.common.constants.Constants.STORE_LINK
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SearchResponse(
    val meanings: List<MeaningModel>,
    val phonetics: List<PhoneticModel>,
    val sourceUrls: List<String>,
    val word: String
) : Parcelable {

    @Parcelize
    data class MeaningModel(
        val antonyms: List<String>,
        val definitions: List<DefinitionModel>,
        val partOfSpeech: String,
        val synonyms: List<String>
    ) : Parcelable {
        @Parcelize
        data class DefinitionModel(
            val antonyms: List<String>,
            val definition: String,
            val example: String,
            val synonyms: List<String>
        ) : Parcelable
    }

    @Parcelize
    data class PhoneticModel(
        val audio: String,
        val sourceUrl: String,
        val text: String
    ) : Parcelable


    fun toNormalText(): String {
        val wordDetails = StringBuilder()
        wordDetails.append(word)

        phonetics.map { phonetic ->
            if (phonetic.text.isNotBlank() && phonetic.audio.isNotBlank()) {
                wordDetails.append("\n\nPronunciation: ${phonetic.text}")
                wordDetails.append("\nAudio link:${phonetic.audio}")
                return@map
            }
        }

        meanings.map { meaningData ->
            wordDetails.append("\n\n\nPart of speech: ${meaningData.partOfSpeech}")
            var definitionCount = 1
            meaningData.definitions.map { definitionData ->
                wordDetails.append("\n\n$definitionCount. ${definitionData.definition}")
                definitionCount++
                // Example
                if (definitionData.example.isNotBlank())
                    wordDetails.append("\nExample: ${definitionData.example}")
                // Synonyms
                if (definitionData.synonyms.isNotEmpty()) {
                    wordDetails.append("\nSynonyms:")
                    definitionData.synonyms.map { synonym ->
                        wordDetails.append("\n➼ $synonym")
                    }
                }// Antonyms
                if (definitionData.antonyms.isNotEmpty()) {
                    wordDetails.append("\nAntonyms:")
                    definitionData.antonyms.map { antonym ->
                        wordDetails.append("\n➼ $antonym")
                    }
                }
            }
        }

        wordDetails.append("\n\nShared from Lexicon app. Get the app from play store: $STORE_LINK\n")
        return wordDetails.toString()
    }

    fun toWhatsappFormattedText(): String {
        val wordDetails = StringBuilder()
        wordDetails.append("*$word*")

        run breaking@{
            phonetics.forEach { phonetic ->
                if (phonetic.text.isNotBlank() && phonetic.audio.isNotBlank()) {
                    wordDetails.append("\n\nPronunciation: ${phonetic.text}")
                    wordDetails.append("\nAudio link:${phonetic.audio}")
                    return@breaking
                }
            }
        }

        meanings.map { meaningData ->
            wordDetails.append("\n\n\n*Part of speech: _${meaningData.partOfSpeech}_*")
            var definitionCount = 1
            meaningData.definitions.map { definitionData ->
                wordDetails.append("\n\n*$definitionCount*. ${definitionData.definition}")
                definitionCount++
                // Example
                if (definitionData.example.isNotBlank())
                    wordDetails.append("\nExample: _${definitionData.example}_")
                // Synonyms
                if (definitionData.synonyms.isNotEmpty()) {
                    wordDetails.append("\n*Synonyms:*")
                    definitionData.synonyms.map { synonym ->
                        wordDetails.append("\n➼ $synonym")
                    }
                }// Antonyms
                if (definitionData.antonyms.isNotEmpty()) {
                    wordDetails.append("\n*Antonyms:*")
                    definitionData.antonyms.map { antonym ->
                        wordDetails.append("\n➼ $antonym")
                    }
                }
            }
        }

        wordDetails.append("\n\nShared from *Lexicon* app. Get the app from play store: $STORE_LINK\n")
        return wordDetails.toString()
    }
}
