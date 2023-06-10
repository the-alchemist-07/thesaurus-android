package com.mashood.lexicon.search.domain.repository

import com.mashood.lexicon.app.common.Resource
import com.mashood.lexicon.search.domain.model.SearchResponse
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    fun searchKeyword(keyword: String): Flow<Resource<SearchResponse>>

}