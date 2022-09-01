package com.mashood.thesaurus.bookmark.data.repository

import com.mashood.thesaurus.app.common.Resource
import com.mashood.thesaurus.bookmark.data.mapper.toSearchResponse
import com.mashood.thesaurus.bookmark.data.source.BookmarkDao
import com.mashood.thesaurus.bookmark.domain.repository.BookmarkRepository
import com.mashood.thesaurus.search.domain.model.SearchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {

    override fun getBookmarksList(): Flow<Resource<List<SearchResponse>>> = flow {
        val bookmarks = getBookmarksFromDatabase().first()
        if (bookmarks.isNotEmpty()) {
            emit(Resource.Success(bookmarks))
        }
        else {
            emit(Resource.Error(EMPTY_BOOKMARKS))
        }
    }.flowOn(Dispatchers.IO)

    private fun getBookmarksFromDatabase(): Flow<List<SearchResponse>> = bookmarkDao.getBookmarks()
        .map { it.map { bookmarkEntity -> bookmarkEntity.toSearchResponse() } }


    companion object {
        const val EMPTY_BOOKMARKS = "No bookmarks are added"
    }
}