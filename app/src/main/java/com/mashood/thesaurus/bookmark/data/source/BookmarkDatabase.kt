package com.mashood.thesaurus.bookmark.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mashood.thesaurus.bookmark.data.source.converters.MeaningConverter
import com.mashood.thesaurus.bookmark.data.source.converters.PhoneticConverter
import com.mashood.thesaurus.bookmark.data.source.converters.SourceUrlConverter

@TypeConverters(MeaningConverter::class, PhoneticConverter::class, SourceUrlConverter::class)
@Database(entities = [BookmarkEntity::class], version = 1, exportSchema = false)
abstract class BookmarkDatabase: RoomDatabase() {
    abstract val bookmarkDao: BookmarkDao
}
