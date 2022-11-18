package com.mashood.thesaurus.app.auto_completion

object WordSuggestion {

    fun getWordSuggestions(word: String, wordsList: List<String>): List<String> {
        return wordsList.filter { it.startsWith(word) }
    }

}