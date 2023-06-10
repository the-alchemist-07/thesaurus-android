package com.mashood.lexicon.bookmark.data.source

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark_table")
data class BookmarkEntity(
    @PrimaryKey
    val word: String,
    val meanings: List<MeaningEntity>,
    val phonetics: List<PhoneticEntity>,
    val sourceUrls: List<String>
)
