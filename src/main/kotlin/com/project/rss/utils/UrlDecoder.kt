package com.project.rss.utils

import org.apache.commons.codec.binary.Base32
import java.nio.charset.Charset

object UrlDecoder {
    fun parseUrl(url: String): String {
        var tt = url
        if (!tt.startsWith("http")) {
            tt = String(Base32().decode(tt.toUpperCase()), Charset.forName("UTF-8"))
        }
        return tt
    }
}