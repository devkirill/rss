package com.project.rss.service

import com.project.rss.model.Feed
import com.project.rss.model.FeedTemplate
import com.project.rss.model.Item
import com.project.rss.model.Template
import com.project.rss.utils.DateUtil
import com.project.rss.utils.Json
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.util.regex.Pattern
import java.util.stream.Collectors

class JsoupParser(val url: String, val template: Template) : Parser() {
//    private val pattern: Regex by lazy { Regex(url) }

    override fun urlMatch(selectedUrl: String): Boolean {
        return selectedUrl.matches(Regex(url))
    }

    override fun getFeed(url: String): Feed {
        try {
            val baseUrl = URL(url)

            System.setProperty("http.agent", "insomnia/6.6.2")
            val document = Jsoup.parse(baseUrl, 5000)

            return parse(baseUrl, document)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    fun parse(url: URL, root: Document): Feed {
        val feed = parseFeed(url, root, template.feed)
        feed.item = getNodes(root).map { parsePost(url, it) }
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
        feed.pubDate = DateUtil.parse(rawDate) ?: ZonedDateTime.now()
        return feed
    }

    fun parsePost(url: URL, node: Element): Item {
        val postTemplate = template.post
        val post = Item()
        post.title = getValue(node, postTemplate.title)
        post.description = getValue(node, postTemplate.description)
        post.author = getValue(node, postTemplate.author)
        val rawDate = getValue(node, postTemplate.pubDate)
        post.rawPubDate = rawDate
        post.pubDate = DateUtil.parse(rawDate)
        post.link = url + getValue(node, postTemplate.link)
        post.guid = getValue(node, postTemplate.guid)
        if (post.guid?.isEmpty() != false) post.guid = post.link

        post.img = listOf()
        if (postTemplate.img != null) {
            val img = getValue(node, postTemplate.img)
            if (img.isNotBlank())
                post.img += url + img
        }
        if (postTemplate.innerImg != null && post.link != null) {
            try {
                val innerDocument = Jsoup.parse(URL(post.link), 5000)

                post.img += getValues(innerDocument, postTemplate.innerImg).map { url + it }
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

    fun getNodes(root: Document, path: String? = template.root): List<Element> {
        return root.select(path)
    }

    companion object {
        operator fun invoke(classLoader: ClassLoader, resource: String): JsoupParser {
            val inputStream = classLoader.getResourceAsStream(resource)
                ?: throw IllegalStateException("not found resource - $resource")
            val content = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"))
            return Json.read(content)
        }
    }
}