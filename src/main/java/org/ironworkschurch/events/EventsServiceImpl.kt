package org.ironworkschurch.events

import biweekly.Biweekly
import biweekly.property.DateOrDateTimeProperty
import biweekly.property.DateStart
import com.google.common.collect.Range
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.ironworkschurch.events.config.CacheConfig
import org.ironworkschurch.events.dto.Item
import org.ironworkschurch.events.dto.RssRoot
import org.simpleframework.xml.core.Persister
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URL
import java.util.zip.GZIPInputStream
import javax.cache.annotation.CacheResult
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event
import java.time.LocalDateTime
import java.time.ZonedDateTime
import kotlinx.coroutines.experimental.*
import java.io.IOException
import java.net.HttpURLConnection

@Component
open class EventsServiceImpl : EventsService {
  @Value("\${events-url}")
  protected lateinit var eventsUrl: String
  protected val serializer: Persister by lazy { Persister() }
  private val logger = LoggerFactory.getLogger(CacheConfig::class.java)

  @get:CacheResult
  override val rss: RssRoot get() {
    logger.debug("Fetching RSS")
    val rssRoot = serializer.read(RssRoot::class.java, contents)
    val items = rssRoot.channel.items
    runBlocking<Unit> {
      val jobs = items.map { item ->  // create a lot of coroutines and list their jobs
        launch(CommonPool) {
          item.populateDateRange()
        }
      }
      jobs.forEach { it.join() } // wait for all jobs to complete
    }

    return rssRoot
  }

  @get:CacheResult
  override val contents: String get() {
    logger.debug("Fetching RSS")
    val url = URL(eventsUrl)
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
    val ical = Biweekly.parse(icalStr).first();
    val event = ical.events.first()

    dateRange = Range.closedOpen(event.dateStart.toLocalDateTime(), event.dateEnd.toLocalDateTime())
  }

  private fun DateOrDateTimeProperty.toLocalDateTime(): LocalDateTime? {
    val rawComponents = value.rawComponents
    return LocalDateTime.of(rawComponents.year, rawComponents.month, rawComponents.date, rawComponents.hour, rawComponents.minute, rawComponents.second)
  }
}


