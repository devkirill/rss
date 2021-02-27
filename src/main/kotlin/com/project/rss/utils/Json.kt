package com.project.rss.utils

import com.google.gson.Gson

object Json {
    val gson = Gson()

    fun <T> read(json: String, clazz: Class<T>): T = gson.fromJson(json, clazz)

    inline fun <reified T> read(json: String): T = read(json, T::class.java)
}