package com.mashood.thesaurus.bookmark.data.source

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mashood.thesaurus.bookmark.data.source.converters.MeaningConverter
import com.mashood.thesaurus.bookmark.data.source.converters.PhoneticConverter
import com.mashood.thesaurus.bookmark.data.source.converters.SourceUrlConverter
import com.mashood.thesaurus.history.data.source.HistoryEntity

@TypeConverters(MeaningConverter::class, PhoneticConverter::class, SourceUrlConverter::class)
@Database(
    entities = [BookmarkEntity::class, HistoryEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class BookmarkDatabase : RoomDatabase() {
    abstract val bookmarkDao: BookmarkDao
}
