package com.mashood.thesaurus.search.data.service

import com.mashood.thesaurus.search.data.dto.SearchResponseDtoItem
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface SearchService {

    @GET("api/v2/entries/en/{keyword}")
    suspend fun searchKeyword(@Path("keyword") keyword: String): ApiResponse<List<SearchResponseDtoItem>>

}
