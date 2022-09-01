package com.mashood.thesaurus.bookmark.domain.repository

import com.mashood.thesaurus.app.common.Resource
import com.mashood.thesaurus.search.domain.model.SearchResponse
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {

    fun getBookmarksList(): Flow<Resource<List<SearchResponse>>>

}