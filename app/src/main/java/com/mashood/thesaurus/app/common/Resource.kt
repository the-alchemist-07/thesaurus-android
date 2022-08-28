package com.mashood.thesaurus.app.common

sealed class Resource<out T> {
    data class Success<out T>(val value: T) : Resource<T>()
    data class Error( val error: String ) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}