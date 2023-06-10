package com.mashood.lexicon.bookmark.data.repository

import com.mashood.lexicon.app.common.Resource
import com.mashood.lexicon.bookmark.data.mapper.toSearchResponse
import com.mashood.lexicon.bookmark.data.source.BookmarkDao
import com.mashood.lexicon.bookmark.domain.repository.BookmarkRepository
import com.mashood.lexicon.search.domain.model.SearchResponse
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