package com.example.depresentry.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromString(value: String?): Map<String, Double> {
        if (value == null) {
            return emptyMap()
        }
        val mapType = object : TypeToken<Map<String, Double>>() {}.type
        return gson.fromJson(value, mapType)
    }

    @TypeConverter
    fun fromMap(map: Map<String, Double>): String {
        return gson.toJson(map)
    }
} 