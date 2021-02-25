package com.project.rss.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import java.util.regex.Pattern

object DateUtil {
    private val FORMATS = Arrays.asList(
        "EEE, d MMM yyyy HH:mm:ss z",
        "[EEE,][ ][dd MMM yyyy][ ]['T'][HH:mm:ss][ ][zzz][ZZZ]",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "dd.MM.yyyy HH:mm:ss Z"
    )
    private val LOCALES = Arrays.asList(Locale.US, Locale("ru"))
    private val TODAY_PATTERN = Pattern.compile("^\\s*(?:today|сегодня)\\s*(?:in|в)?\\s*(?<h>\\d{2}):(?<m>\\d{2})\\s*$")
    private val YESTERDAY_PATTERN =
        Pattern.compile("^\\s*(?:yesterday|вчера)\\s*(?:in|в)?\\s*(?<h>\\d{2}):(?<m>\\d{2})\\s*$")

    fun parse(stringDate: String, format: String, locale: Locale): ZonedDateTime? {
        return try {
            val formatter = DateTimeFormatter.ofPattern(format, locale)
            ZonedDateTime.parse(stringDate, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    fun parse(stringDate: String, format: String): ZonedDateTime? {
        for (locale in LOCALES) {
            val parsed = parse(stringDate, format, locale)
            if (parsed != null) return parsed
        }
        return null
    }

    /**
     * Парсер дат, задумывалось что он умеет парсить основные человеко читаемые форматы (в том числе и относительные текущей даты)
     * @param stringDate - строка с датой
     * @return
     */
    fun parse(stringDate: String): ZonedDateTime? {
        for (format in FORMATS) {
            val parsedDate = parse(stringDate, format)
            if (parsedDate != null) return parsedDate
        }
        val matchToday = TODAY_PATTERN.matcher(stringDate)
        if (matchToday.find()) {
            val time = matchToday.group("h") + ":" + matchToday.group("m")
            val localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
            val zone = ZoneId.systemDefault()
            return ZonedDateTime.of(LocalDate.now(zone), localTime, zone)
        }
        val matchYesterday = YESTERDAY_PATTERN.matcher(stringDate)
        if (matchYesterday.find()) {
            val time = matchYesterday.group("h") + ":" + matchYesterday.group("m")
            val localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
            val zone = ZoneId.systemDefault()
            return ZonedDateTime.of(LocalDate.now(zone).minusDays(1), localTime, zone)
        }
        return null
    }
}