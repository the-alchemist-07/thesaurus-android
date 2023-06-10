package com.mashood.lexicon.bookmark.domain.repository

import com.mashood.lexicon.app.common.Resource
import com.mashood.lexicon.search.domain.model.SearchResponse
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {

    fun getBookmarksList(): Flow<Resource<List<SearchResponse>>>

}