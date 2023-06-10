package com.mashood.lexicon.search.data.repository

import com.mashood.lexicon.app.common.Constants.GENERIC_ERROR_MESSAGE
import com.mashood.lexicon.app.common.Constants.NO_INTERNET_ERROR_MESSAGE
import com.mashood.lexicon.app.common.Constants.SERVER_ERROR
import com.mashood.lexicon.app.common.Resource
import com.mashood.lexicon.search.data.service.SearchService
import com.mashood.lexicon.search.domain.model.SearchResponse
import com.mashood.lexicon.search.domain.repository.SearchRepository
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchService: SearchService
): SearchRepository {

    override fun searchKeyword(keyword: String): Flow<Resource<SearchResponse>> = flow {
        emit(Resource.Loading)

        searchService.searchKeyword(keyword)
            .suspendOnSuccess {
                val searchResponseListDto = this.data
                if (searchResponseListDto.isNotEmpty()) {
                    val searchResponse = searchResponseListDto[0].toSearchResponseModel()
                    emit(Resource.Success(searchResponse))
                }
                else {
                    emit(Resource.Error(NO_RESULT))
                }
            }
            .suspendOnError {
                when(this.statusCode) {
                    StatusCode.InternalServerError -> emit(Resource.Error(SERVER_ERROR))
                    else -> emit(Resource.Error(GENERIC_ERROR_MESSAGE))
                }
            }
            .suspendOnFailure {
                emit(Resource.Error(NO_INTERNET_ERROR_MESSAGE))
            }
    }



    companion object {
        const val NO_RESULT = "No search result found!"
    }

}