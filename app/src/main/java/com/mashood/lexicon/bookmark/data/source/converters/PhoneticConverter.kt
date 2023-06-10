package com.mashood.lexicon.bookmark.data.source.converters

import androidx.room.TypeConverter
import com.mashood.lexicon.bookmark.data.source.PhoneticEntity
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

class PhoneticConverter {

    private val moshi: Moshi = Moshi.Builder().build()
    private val type: Type =
        Types.newParameterizedType(List::class.java, PhoneticEntity::class.java)
    private val jsonAdapter: JsonAdapter<List<PhoneticEntity>> = moshi.adapter(type)

    @TypeConverter
    fun fromString(value: String): List<PhoneticEntity> {
        return jsonAdapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun toString(data: List<PhoneticEntity>): String {
        return jsonAdapter.toJson(data)
    }

}