//package com.project.rss
//
//import com.fasterxml.jackson.dataformat.xml.XmlMapper
//import com.google.gson.Gson
//import com.project.rss.converter.XmlConverter
//import com.project.rss.model.Template
//import com.project.rss.service.JsoupParser
//import com.project.rss.utils.XmlUtils
//import java.io.File
//import java.io.Writer
//import java.io.FileWriter
//
//import java.io.BufferedWriter
//
//
//
//
//fun main() {
//    val parser = JsoupParser()
//
//    val template = Template().apply {
//        root = "article.post"
//        feed.apply {
//            title = "meta[property*=title] @content"
//            description = "meta[property*=description] @content"
//            image = "meta[property*=image] @content"
//            language = "html[lang] @lang"
//        }
//        post.apply {
//            guid = "div.post__body a.btn @href"
//            title = "a.post__title_link"
//            description = "div.post__text @"
//            link = "div.post__body a.btn @href"
//            author = "span.user-info__nickname"
//            pubDate = "span.post__time"
//        }
//    }
//
//    val feed = parser.getFeed("https://habr.com/ru/all/all/", template)
////    val feed = parser.getFeed("http://www.feedforall.com/sample.xml", template)
//    val rss = XmlConverter.rssToXml(feed)
//    println(rss)
//    val writer = BufferedWriter(FileWriter("simple.xml", false))
//    writer.append(rss)
//    writer.close()
//}