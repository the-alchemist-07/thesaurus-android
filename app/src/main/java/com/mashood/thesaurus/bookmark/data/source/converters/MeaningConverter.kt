package com.mashood.thesaurus.bookmark.data.source.converters

import androidx.room.TypeConverter
import com.mashood.thesaurus.bookmark.data.source.MeaningEntity
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

class MeaningConverter {

    private val moshi: Moshi = Moshi.Builder().build()
    private val type: Type =
        Types.newParameterizedType(List::class.java, MeaningEntity::class.java)
    private val jsonAdapter: JsonAdapter<List<MeaningEntity>> = moshi.adapter(type)

    @TypeConverter
    fun fromString(value: String): List<MeaningEntity> {
        return jsonAdapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun toString(data: List<MeaningEntity>): String {
        return jsonAdapter.toJson(data)
    }

}
