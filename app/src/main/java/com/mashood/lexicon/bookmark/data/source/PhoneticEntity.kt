package com.mashood.lexicon.bookmark.data.source

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhoneticEntity(
    val audio: String,
    val sourceUrl: String,
    val text: String
)
