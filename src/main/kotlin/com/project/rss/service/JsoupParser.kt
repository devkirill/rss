package com.project.rss.service

import com.project.rss.model.*
import com.project.rss.utils.DateUtil.parse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.springframework.stereotype.Service
import java.net.URL
import java.time.ZonedDateTime
import java.util.regex.Pattern
import java.util.stream.Collectors

@Service
class JsoupParser {
    fun getFeed(url: String, template: Template): Feed {
        try {
            val baseUrl = URL(url)

            System.setProperty("http.agent", "insomnia/6.6.2")
            val document = Jsoup.parse(baseUrl, 5000)

            return parse(baseUrl, document, template)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

//                    "naruto-base.su" -> {
//                    val latestId = latest.replace(Regex("\\D"), "").toInt()
//                    val list = Jsoup.connect(s.key).get()
//                            .select("#allEntries div.title a")
//                            .map { it.text() to "https://naruto-base.su${it.attr("href")}" }
//                            .filter { it.first.replace(Regex("\\D"), "").toInt() > latestId }
//                            .sortedBy { it.first.replace(Regex("\\D"), "").toInt() }
//                    if (list.isNotEmpty()) {
//                        var newLatest = latest
//                        for (i in list) {
//                            val doc = Jsoup.connect(i.second).get()
//                            val urls = doc.select("div.yellowBox div a[rel~=iLoad]")
//                                    .map { element ->
//                                        val href = element.attr("href")
//                                        "https://naruto-base.su$href"
//                                    }
//                            if (urls.isNotEmpty()) {
//                                for (chatId in s.value) {
//                                    sendImages(chatId, urls)
//                                }
//                                newLatest = i.first
//                            }
//                        }
//                        if (latest != newLatest)
//                            storage.save(Manga(s.key, newLatest))
//                    }
//                }

    fun parse(url: URL, root: Document, template: Template): Feed {
        val feed = parseFeed(url, root, template.feed)
        feed.item = getNodes(root, template).map { parsePost(url, it, template.post) }
        return feed
    }

    operator fun URL.plus(path: String): String {
        return URL(this, path).toString()
    }

    fun parseFeed(url: URL, node: Element, template: FeedTemplate): Feed {
        val feed = Feed()
        feed.title = getValue(node, template.title)
        feed.description = getValue(node, template.description)
        feed.image = getValue(node, template.image)
        feed.language = getValue(node, template.language)
        feed.link = getValue(node, template.link)
        val rawDate = getValue(node, template.pubDate)
        feed.pubDate = parse(rawDate) ?: ZonedDateTime.now()
        return feed
    }

    fun parsePost(url: URL, node: Element, template: PostTemplate): Item {
        val post = Item()
        post.title = getValue(node, template.title)
        post.description = getValue(node, template.description)
        post.author = getValue(node, template.author)
        val rawDate = getValue(node, template.pubDate)
        post.rawPubDate = rawDate
        post.pubDate = parse(rawDate)
        post.link = url + getValue(node, template.link)
        post.guid = getValue(node, template.guid)
        if (post.guid?.isEmpty() != false) post.guid = post.link

        post.img = listOf()
        if (template.img != null) {
            val img = getValue(node, template.img)
            if (img.isNotBlank())
                post.img += url + img
        }
        if (template.innerImg != null && post.link != null) {
            try {
                val innerDocument = Jsoup.parse(URL(post.link), 5000)

                post.img += getValues(innerDocument, template.innerImg).map { url + it }
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }
        }

        return post
    }

    fun getValue(element: Element, path: String?): String {
        return try {
            if (path == null || path.trim { it <= ' ' }.isEmpty()) return ""
            var select = path
            var attr = ""
            var withHtml = false
            val pattern = Pattern.compile("^(.*)(?:\\s@([^@]*))\\s*$")
            val matcher = pattern.matcher(path)
            if (matcher.find()) {
                select = matcher.group(1)
                attr = matcher.group(2)
                withHtml = true
            }
            val selectedElement = element.select(select)
            if (attr.isEmpty()) {
                if (withHtml) selectedElement.stream()
                    .flatMap { el: Element ->
                        el.childNodes().stream()
                    }
                    .map { obj: Node -> obj.toString() }
                    .collect(Collectors.joining()).trim { it <= ' ' } else selectedElement.text().trim { it <= ' ' }
            } else selectedElement.attr(attr).trim { it <= ' ' }
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    fun getValues(element: Element, path: String?): List<String> {
        return try {
            if (path == null || path.trim { it <= ' ' }.isEmpty()) return listOf()
            var select = path
            var attr = ""
            var withHtml = false
            val pattern = Pattern.compile("^(.*)(?:\\s@([^@]*))\\s*$")
            val matcher = pattern.matcher(path)
            if (matcher.find()) {
                select = matcher.group(1)
                attr = matcher.group(2)
                withHtml = true
            }
            val selectedElement = element.select(select)
            if (attr.isEmpty()) {
                listOf()
            } else selectedElement.map { it.attr(attr) }
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    fun getNodes(root: Document, path: String?): List<Element> {
        return root.select(path)
    }

    fun getNodes(root: Document, template: Template): List<Element> = getNodes(root, template.root)
}