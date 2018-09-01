package com.rodolfonavalon.canadatransit.controller.util

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

object MoshiUtil {

    fun <T: Any> toJson(value: List<T>): String
         = Moshi.Builder().build().adapter(List::class.java).nonNull().toJson(value)

    fun <T: Any> fromJson(value: String, valueClazz: Class<T>): List<T> {
        val parameterizedType = Types.newParameterizedType(List::class.java, valueClazz)
        val adapter: JsonAdapter<List<T>> = Moshi.Builder().build().adapter(parameterizedType)
        return adapter.nonNull().fromJson(value)!!
    }
}
