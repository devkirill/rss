package com.project.rss.service

import com.project.rss.model.Feed

abstract class Parser {
    abstract fun urlMatch(url: String): Boolean

    abstract fun getFeed(url: String): Feed
}