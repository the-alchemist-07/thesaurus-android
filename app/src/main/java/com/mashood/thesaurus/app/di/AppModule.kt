package com.mashood.thesaurus.app.di

import android.app.Application
import androidx.room.Room
import com.mashood.thesaurus.bookmark.data.source.BookmarkDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideBookmarkDatabase(application: Application) =
        Room.databaseBuilder(application, BookmarkDatabase::class.java, "bookmark_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideBookmarkDao(bookmarkDatabase: BookmarkDatabase) = bookmarkDatabase.bookmarkDao

}