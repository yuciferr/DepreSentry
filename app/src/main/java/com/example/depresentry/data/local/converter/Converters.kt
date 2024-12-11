package com.example.depresentry.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

class Converters {
    private val gson = Gson()

    // LocalDate converters
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }

    // Map<String, Double> converters
    @TypeConverter
    fun fromStringDoubleMap(value: Map<String, Double>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringDoubleMap(value: String?): Map<String, Double>? {
        return value?.let {
            val mapType = object : TypeToken<Map<String, Double>>() {}.type
            gson.fromJson(it, mapType)
        }
    }

    // List<Int> converters
    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return value?.let {
            val listType = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(it, listType)
        }
    }
} 