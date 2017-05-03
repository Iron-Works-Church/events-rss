package org.ironworkschurch.events.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.Range
import org.ironworkschurch.events.dto.Item
import org.ironworkschurch.events.dto.json.Event
import org.ironworkschurch.events.dto.json.Events
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.zip.GZIPInputStream

class EventsService constructor(
  val eventsUrl: String,
  val hiddenEventsUrl: String,
  private val objectMapper: ObjectMapper) {
  private val logger = LoggerFactory.getLogger(EventsService::class.java)

  val rss: List<Item>
    get() = listOf(publicEvents, hiddenEvents)
      .map { objectMapper.readValue(it, Events::class.java) }
      .flatMap { it.upcoming + it.past }
      .map { it.toItem() }

  val publicEvents: String
    get() = getContents("public events", eventsUrl)

  val hiddenEvents: String
    get() = getContents("hidden events", hiddenEventsUrl)

  private fun getContents(name: String, pageUrl: String): String {
    logger.debug("Fetching $name RSS")
    Thread.sleep(500)
    val url = URL("$pageUrl?format=json")
    val connection = url.openConnection()
    connection.setRequestProperty("Accept-Encoding", "gzip")
    val inputStream = if ("gzip" == connection.contentEncoding) {
      GZIPInputStream(connection.getInputStream())
    } else {
      connection.getInputStream()
    }

    return inputStream.bufferedReader(Charsets.UTF_8).readText()
  }

  private fun  Event.toItem(): Item {
    return Item(guid = id,
      title = title,
      link = fullUrl,
      description = excerpt,
      pubDate = Date(publishOn).toString(),
      creator = author.displayName,
      encoded = body,
      dateRange = Range.closedOpen(startDate.localDateTime(), endDate.localDateTime())
    )
  }

  private fun EpochSeconds.localDateTime() = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())

}

typealias EpochSeconds = Long
