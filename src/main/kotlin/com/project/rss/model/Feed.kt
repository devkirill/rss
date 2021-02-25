package com.project.rss.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.time.ZonedDateTime

@JacksonXmlRootElement(localName = "channel")
class Feed {
    var title: String? = null
    var description: String? = null
    var language: String? = null
    var image: String? = null
    var link: String? = null
    @JsonFormat(pattern = "EEE, d MMM yyyy HH:mm:ss z", locale = "US")
    var pubDate: ZonedDateTime = ZonedDateTime.now()
    @JacksonXmlElementWrapper(useWrapping = false)
    var item = listOf<Item>()
}