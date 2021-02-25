package com.project.rss.service

import com.google.gson.Gson
import com.project.rss.model.StoredTemplate
import com.project.rss.model.Template
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.regex.Pattern

@Service
class TemplateStorage {
    var storage = getResourceFiles("templates")
        .map {
            Gson().fromJson(getClassLoader().getResource("templates/$it").readText(), StoredTemplate::class.java)
        }

    fun findTemplate(url: String): Template {
        for (storedTemplate in storage) {
            if (Pattern.matches(storedTemplate.url, url)) {
                return storedTemplate.template
            }
        }
        throw IllegalStateException("not found template for this url")
    }

    companion object {
        @Throws(IOException::class)
        private fun getResourceFiles(path: String): List<String> {
            val filenames: MutableList<String> = ArrayList()
            getResourceAsStream(path).use {
                BufferedReader(InputStreamReader(it)).use { br ->
                    var resource = br.readLine()
                    while (resource != null) {
                        filenames.add(resource)
                        resource = br.readLine()
                    }
//                    var resource: String? = null
//                    while (br.readLine()?.also { resource = it } != null) {
//                        filenames.add(resource!!)
//                    }
                }
            }
            return filenames
        }

        private fun getResourceAsStream(resource: String) = getClassLoader().getResourceAsStream(resource)
//            getContextClassLoader().getResourceAsStream(resource) ?: javaClass.getResourceAsStream(resource)

        private fun getClassLoader() = getContextClassLoader() ?: javaClass.classLoader

        private fun getContextClassLoader() = Thread.currentThread().contextClassLoader
    }
}