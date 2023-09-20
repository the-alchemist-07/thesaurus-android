package com.mashood.thesaurus.bookmark.data.source

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    @Query("SELECT * FROM bookmark_table")
    fun getBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT count(*) FROM bookmark_table WHERE word=:word")
    fun checkBookmarked(word: String): Int

}