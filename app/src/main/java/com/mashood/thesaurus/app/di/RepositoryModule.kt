package com.mashood.thesaurus.app.di

import com.mashood.thesaurus.search.data.repository.SearchRepositoryImpl
import com.mashood.thesaurus.search.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindSearchRepository(searchServiceImpl: SearchRepositoryImpl): SearchRepository

}