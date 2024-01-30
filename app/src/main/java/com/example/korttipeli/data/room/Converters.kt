package com.example.korttipeli.data.room

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun stringListToJson(list: List<String>): String {

        return Gson().toJson(list)
    }

    @TypeConverter
    fun jsonToStringList(json: String): List<String> {

        val array = Gson().fromJson(json, Array<String>::class.java)
        return array.toList()
    }

    @TypeConverter
    fun intArrayToJson(intArray: IntArray): String {

        return Gson().toJson(intArray)
    }

    @TypeConverter
    fun jsonToIntArray(json: String): IntArray {

        return Gson().fromJson(json, IntArray::class.java)
    }
}