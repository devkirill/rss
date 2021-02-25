package com.project.rss.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import java.time.ZonedDateTime

class Item {
    var guid: String? = null
    var author: String? = null
    var link: String? = null
    @JsonFormat(pattern = "EEE, d MMM yyyy HH:mm:ss z", locale = "US")
    var pubDate: ZonedDateTime? = ZonedDateTime.now()
    var rawPubDate: String? = null
    var title: String? = null
    var description: String? = null
    @JacksonXmlElementWrapper(useWrapping = false)
    var img = listOf<String>()
}