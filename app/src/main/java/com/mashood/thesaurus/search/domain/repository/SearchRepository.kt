package com.mashood.thesaurus.search.domain.repository

import com.mashood.thesaurus.app.common.Resource
import com.mashood.thesaurus.search.domain.model.SearchResponse
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    fun searchKeyword(keyword: String): Flow<Resource<SearchResponse>>

}