package com.mashood.thesaurus.bookmark.data.source

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BookmarkEntity::class], version = 1, exportSchema = false)
abstract class BookmarkDatabase: RoomDatabase() {
    abstract val bookmarkDao: BookmarkDao
}
