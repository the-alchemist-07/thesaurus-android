package com.mashood.lexicon.app.common

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

object WordSuggestionsHelper {

    fun getWordsListFromJsonFile(context: Context): List<String>? {
        val moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter: JsonAdapter<List<String>> = moshi.adapter(listType)

        val file = "words_list.json"
        val wordsJson = context.assets.open(file).bufferedReader().use { it.readText() }
        return adapter.fromJson(wordsJson)
    }

    fun getWordSuggestions(word: String, wordsList: List<String>): List<String> {
        return wordsList.filter { it.startsWith(word) }
    }

}