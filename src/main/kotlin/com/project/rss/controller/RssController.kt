package com.project.rss.controller

//import com.project.rss.service.RssReader
import com.project.rss.converter.XmlConverter
import com.project.rss.model.Template
import com.project.rss.service.JsoupParser
import com.project.rss.service.TemplateStorage
import com.project.rss.utils.UrlDecoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping(path = ["/"])
class RssController {
    @Autowired
    lateinit var reader: JsoupParser
    @Autowired
    lateinit var templates: TemplateStorage

    @GetMapping(path = ["/rss"], produces = [ MediaType.APPLICATION_XML_VALUE ])
    @ResponseBody
    fun rss(@ModelAttribute("url") url: String): String {
        val template = templates.findTemplate(UrlDecoder.parseUrl(url))
        return rss(url, template)
    }

    @GetMapping(path = ["/rsst"], produces = [ MediaType.APPLICATION_XML_VALUE ])
    @ResponseBody
    fun rss(@ModelAttribute("url") url: String, @ModelAttribute("template") template: Template): String {
        val u = UrlDecoder.parseUrl(url)
        val xml = reader.getFeed(u, template)
        return "<rss>"+XmlConverter.rssToXml(xml)+"</rss>"
    }
}