package com.mashood.thesaurus.app.di

import com.mashood.thesaurus.bookmark.data.repository.BookmarkRepositoryImpl
import com.mashood.thesaurus.bookmark.domain.repository.BookmarkRepository
import com.mashood.thesaurus.history.data.repository.HistoryRepositoryImpl
import com.mashood.thesaurus.history.domain.repository.HistoryRepository
import com.mashood.thesaurus.search.data.repository.SearchRepositoryImpl
import com.mashood.thesaurus.search.domain.repository.SearchRepository
import com.mashood.thesaurus.shortcuts.data.repository.UninstallRepositoryImpl
import com.mashood.thesaurus.shortcuts.domain.repository.UninstallRepository
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

    @Binds
    abstract fun bindHistoryRepository(historyRepositoryImpl: HistoryRepositoryImpl): HistoryRepository

    @Binds
    abstract fun bindUninstallRepository(uninstallRepositoryImpl: UninstallRepositoryImpl): UninstallRepository
}