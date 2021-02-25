package com.project.rss.converter

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

object XmlConverter {
    fun<T> rssToXml(obj: T): String {
        val xmlMapper = XmlMapper()
        xmlMapper.registerModule(JavaTimeModule())
        return xmlMapper.writer()/*.withRootName("rss")*/.writeValueAsString(obj)
//        return xmlMapper.writeValueAsString(obj)
    }
}