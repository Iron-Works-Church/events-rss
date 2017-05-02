package org.ironworkschurch.events.service

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
open class EventsServiceFromHtml : EventsService {
  @org.springframework.beans.factory.annotation.Value("\${events-url}")
  protected lateinit var eventsUrl: String
  @org.springframework.beans.factory.annotation.Value("\${hidden-events-url}")
  protected lateinit var hiddenEventsUrl: String
  protected val serializer: org.simpleframework.xml.core.Persister by lazy { org.simpleframework.xml.core.Persister() }
  private val logger = org.slf4j.LoggerFactory.getLogger(org.ironworkschurch.events.config.CacheConfig::class.java)

  @get:javax.cache.annotation.CacheResult
  override val rss: List<org.ironworkschurch.events.dto.Item> get() {
    logger.debug("Fetching HTML")
    return listOf(eventsUrl, hiddenEventsUrl) .flatMap { getItemsOnPage(it) }
  }

  private fun getItemsOnPage(it: String): List<org.ironworkschurch.events.dto.Item> {
    val document = org.jsoup.Jsoup.connect(it).get()
    val upcomingEvents = document.getElementsByClass("eventlist--upcoming").first()
    val articles = upcomingEvents.getElementsByTag("article")
    return articles.map { it.toItem() }
  }

  @get:javax.cache.annotation.CacheResult
  override val publicEvents: String get() {
    return getContents("public events", eventsUrl)
  }

  @get:javax.cache.annotation.CacheResult
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
      java.util.zip.GZIPInputStream(connection.getInputStream())
    } else {
      connection.getInputStream()
    }

    return inputStream.bufferedReader(Charsets.UTF_8).readText()
  }

  private fun biweekly.property.DateOrDateTimeProperty.toLocalDateTime(): java.time.LocalDateTime? {
    val rawComponents = value.rawComponents
    return java.time.LocalDateTime.of(rawComponents.year, rawComponents.month, rawComponents.date, rawComponents.hour, rawComponents.minute, rawComponents.second)
  }

  private fun org.ironworkschurch.events.service.Article.toItem(): org.ironworkschurch.events.dto.Item {
    val icalUrl = getElementsByClass("eventlist-meta-export-google").first().attr("href")
    val dateRange = getDateRange(icalUrl)

    val link = getElementsByClass("eventlist-title-link").first().attr("href")
    return org.ironworkschurch.events.dto.Item(
      guid = link,
      title = getElementsByClass("eventlist-title-link").first().text(),
      link = link,
      description = getElementsByClass("eventlist-excerpt").first().html(),
      pubDate = "",
      dateRange = dateRange
    )
  }

  val dateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
  val regex = "&dates=(.+Z)/(.+Z)".toRegex()

  fun getDateRange(icalUrl: String): com.google.common.collect.Range<LocalDateTime>? {
    val matchResult = regex.find(icalUrl)
    val dates = (matchResult
            ?.groups ?: listOf<MatchGroup?>())
            .drop(1)
            .filterNotNull()
            .map { it.value }
            .map { java.time.LocalDateTime.parse(it, dateTimeFormatter) }
    val dateRange = if (dates.size == 2) {
      com.google.common.collect.Range.closedOpen(dates[0], dates[1])
    } else {
      null
    }
    return dateRange
  }
}

typealias Article = org.jsoup.nodes.Element





