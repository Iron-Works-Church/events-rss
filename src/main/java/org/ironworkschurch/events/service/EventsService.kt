package org.ironworkschurch.events.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.ironworkschurch.events.dto.json.Event
import org.ironworkschurch.events.dto.json.Events
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.zip.GZIPInputStream

class EventsService constructor(
  val eventsUrl: String,
  val hiddenEventsUrl: String,
  private val objectMapper: ObjectMapper) {
  private val logger = LoggerFactory.getLogger(EventsService::class.java)

  val rss: List<Event>
    get() = listOf(publicEvents, hiddenEvents)
      .map { objectMapper.readValue(it, Events::class.java) }
      .flatMap { it.upcoming + it.past }

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
}

