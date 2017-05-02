package org.ironworkschurch.events.service

import com.google.common.collect.Range
import org.ironworkschurch.events.dto.Item
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.GZIPInputStream
import javax.cache.annotation.CacheResult

@Component
open class EventsServiceFromHtml @Autowired constructor(
  @Value("\${org.ironworkschurch.events-url}") val eventsUrl: String,
  @Value("\${org.ironworkschurch.hidden-events-url}") val hiddenEventsUrl: String) : EventsService {
  private val logger = LoggerFactory.getLogger(EventsServiceFromHtml::class.java)

  @get:CacheResult
  override val rss: List<Item> get() {
    logger.debug("Fetching HTML")
    return listOf(eventsUrl, hiddenEventsUrl) .flatMap { getItemsOnPage(it) }
  }

  private fun getItemsOnPage(it: String): List<Item> {
    val document = Jsoup.connect(it).get()
    val upcomingEvents = document.getElementsByClass("eventlist--upcoming").first()
    val articles = upcomingEvents.getElementsByTag("article")
    return articles.map { it.toItem() }
  }

  @get:CacheResult
  override val publicEvents: String get() {
    return getContents("public events", eventsUrl)
  }

  @get:CacheResult
  override val hiddenEvents: String get() {
    return getContents("hidden events", hiddenEventsUrl)
  }

  private fun getContents(name: String, htmlUrl: String): String {
    logger.debug("Fetching $name RSS")
    Thread.sleep(500)
    val url = java.net.URL(htmlUrl)
    val connection = url.openConnection()
    connection.setRequestProperty("Accept-Encoding", "gzip")
    val inputStream = if ("gzip" == connection.contentEncoding) {
      GZIPInputStream(connection.getInputStream())
    } else {
      connection.getInputStream()
    }

    return inputStream.bufferedReader(Charsets.UTF_8).readText()
  }

  private fun org.ironworkschurch.events.service.Article.toItem(): Item {
    val icalUrl = getElementsByClass("eventlist-meta-export-google").first().attr("href")
    val dateRange = getDateRange(icalUrl)

    val link = getElementsByClass("eventlist-title-link").first().attr("href")
    return Item(
      guid = link,
      title = getElementsByClass("eventlist-title-link").first().text(),
      link = link,
      description = getElementsByClass("eventlist-excerpt").first().html(),
      pubDate = "",
      dateRange = dateRange
    )
  }

  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
  val regex = "&dates=(.+Z)/(.+Z)".toRegex()

  fun getDateRange(icalUrl: String): Range<LocalDateTime>? {
    val matchResult = regex.find(icalUrl)
    val dates = (matchResult
            ?.groups ?: listOf<MatchGroup?>())
            .drop(1)
            .filterNotNull()
            .map { it.value }
            .map { java.time.LocalDateTime.parse(it, dateTimeFormatter) }
    val dateRange = if (dates.size == 2) {
      Range.closedOpen(dates[0], dates[1])
    } else {
      null
    }
    return dateRange
  }
}

typealias Article = org.jsoup.nodes.Element




