package com.mashood.thesaurus.bookmark.data.source.converters

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

class SourceUrlConverter {

    private val moshi: Moshi = Moshi.Builder().build()
    private val type: Type = Types.newParameterizedType(List::class.java, String::class.java)
    private val jsonAdapter: JsonAdapter<List<String>> = moshi.adapter(type)

    @TypeConverter
    fun fromString(value: String): List<String> {
        return jsonAdapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun toString(data: List<String>): String {
        return jsonAdapter.toJson(data)
    }

}