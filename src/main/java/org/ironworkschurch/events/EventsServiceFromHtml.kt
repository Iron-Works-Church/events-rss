package org.ironworkschurch.events

import biweekly.Biweekly
import biweekly.property.DateOrDateTimeProperty
import com.google.common.collect.Range
import org.ironworkschurch.events.config.CacheConfig
import org.ironworkschurch.events.dto.Item
import org.ironworkschurch.events.dto.RssRoot
import org.simpleframework.xml.core.Persister
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URL
import java.time.LocalDateTime
import java.util.zip.GZIPInputStream
import javax.cache.annotation.CacheResult
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Component
open class EventsServiceFromHtml : EventsService {
  @Value("\${events-url}")
  protected lateinit var eventsUrl: String
  @Value("\${hidden-events-url}")
  protected lateinit var hiddenEventsUrl: String
  protected val serializer: Persister by lazy { Persister() }
  private val logger = LoggerFactory.getLogger(CacheConfig::class.java)

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
    val url = URL(htmlUrl)
    val connection = url.openConnection()
    connection.setRequestProperty("Accept-Encoding", "gzip")
    val inputStream = if ("gzip" == connection.contentEncoding) {
      GZIPInputStream(connection.getInputStream())
    } else {
      connection.getInputStream()
    }

    return inputStream.bufferedReader(Charsets.UTF_8).readText()
  }

  private fun DateOrDateTimeProperty.toLocalDateTime(): LocalDateTime? {
    val rawComponents = value.rawComponents
    return LocalDateTime.of(rawComponents.year, rawComponents.month, rawComponents.date, rawComponents.hour, rawComponents.minute, rawComponents.second)
  }

  private fun Article.toItem(): Item {
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
            .map { LocalDateTime.parse(it, dateTimeFormatter) }
    val dateRange = if (dates.size == 2) {
      Range.closedOpen(dates[0], dates[1])
    } else {
      null
    }
    return dateRange
  }
}

typealias Article = Element





