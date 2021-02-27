package com.project.rss.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service

@Service
class TemplateStorage(@Autowired var resourceLoader: ResourceLoader) {
    val classLoader = resourceLoader.classLoader ?: throw IllegalStateException("not found class loader")

    var storage : List<Parser> = listOf(
        JsoupParser(classLoader, "templates/habr.json"),
        JsoupParser(classLoader, "templates/naruto_base.json")
    )

    init {
        println(storage.filterIsInstance<JsoupParser>().map { it.url })
    }

    fun findParser(url: String): Parser {
        for (parser in storage) {
            if (parser.urlMatch(url)) {
                return parser
            }
        }
        throw IllegalStateException("not found parser for this url")
    }
}