package com.mashood.lexicon.app.di

import com.mashood.lexicon.bookmark.data.repository.BookmarkRepositoryImpl
import com.mashood.lexicon.bookmark.domain.repository.BookmarkRepository
import com.mashood.lexicon.search.data.repository.SearchRepositoryImpl
import com.mashood.lexicon.search.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindSearchRepository(searchServiceImpl: SearchRepositoryImpl): SearchRepository

    @Binds
    abstract fun bindBookmarkRepository(bookmarkRepositoryImpl: BookmarkRepositoryImpl): BookmarkRepository

}