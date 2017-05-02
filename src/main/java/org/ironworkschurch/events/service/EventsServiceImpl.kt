package org.ironworkschurch.events.service

import biweekly.property.DateOrDateTimeProperty
import com.google.common.collect.Range
import org.ironworkschurch.events.dto.Item
import org.ironworkschurch.events.dto.RssRoot
import org.simpleframework.xml.core.Persister
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import java.io.IOException
import java.net.URL
import java.time.LocalDateTime
import java.util.zip.GZIPInputStream
import javax.cache.annotation.CacheResult

open class EventsServiceImpl @Autowired constructor(
  @Value("\${org.ironworkschurch.events-url}") val eventsUrl: String,
  @Value("\${org.ironworkschurch.hidden-events-url}") val hiddenEventsUrl: String) : EventsService {
  protected val serializer: Persister by lazy { Persister() }
  private val logger = LoggerFactory.getLogger(EventsServiceImpl::class.java)

  @get:CacheResult
  override val rss: List<Item> get() {
    logger.debug("Fetching RSS")
    return listOf(publicEvents, hiddenEvents)
            .flatMap { serializer.read(RssRoot::class.java, it).channel.items }
            .onEach { item -> item.populateDateRange() }
    // TODO add sermon link
  }

  @get:CacheResult
  override val publicEvents: String get() {
    return getContents("public events", eventsUrl)
  }

  @get:CacheResult
  override val hiddenEvents: String get() {
    return getContents("hidden events", hiddenEventsUrl)
  }

  private fun getContents(name: String, rssUrl: String): String {
    logger.debug("Fetching $name RSS")
    Thread.sleep(500)
    val url = java.net.URL("$rssUrl?format=rss")
    val connection = url.openConnection()
    connection.setRequestProperty("Accept-Encoding", "gzip")
    val inputStream = if ("gzip" == connection.contentEncoding) {
      GZIPInputStream(connection.getInputStream())
    } else {
      connection.getInputStream()
    }

    return inputStream.bufferedReader(Charsets.UTF_8).readText()
  }

  private fun Item.populateDateRange() {
    logger.debug("Populating date range for $guid")
    Thread.sleep(500)
    val connection = URL(link + "?format=ical").openConnection()

    connection.setRequestProperty("Accept-Encoding", "gzip")
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");

    val inputStream = try {
      if ("gzip" == connection.contentEncoding) {
        GZIPInputStream(connection.getInputStream())
      } else {
        connection.getInputStream()
      }
    } catch (e: IOException) {
      logger.debug(connection.headerFields.toString())
      throw e
    }

    val icalStr = inputStream.bufferedReader(Charsets.UTF_8).readText()
    val ical = biweekly.Biweekly.parse(icalStr).first()
    val event = ical.events.first()

    dateRange = Range.closedOpen(event.dateStart.toLocalDateTime(), event.dateEnd.toLocalDateTime())
  }

  private fun DateOrDateTimeProperty.toLocalDateTime(): LocalDateTime? {
    val rawComponents = value.rawComponents
    return LocalDateTime.of(rawComponents.year, rawComponents.month, rawComponents.date, rawComponents.hour, rawComponents.minute, rawComponents.second)
  }
}


