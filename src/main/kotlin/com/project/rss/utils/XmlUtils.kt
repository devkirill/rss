package com.project.rss.utils

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

class XmlUtils {
    companion object {
        fun <T> toXml(obj: T): String {
            val xmlMapper = XmlMapper()
            xmlMapper.registerModule(JavaTimeModule())
            return xmlMapper.writeValueAsString(obj)
        }
    }
}