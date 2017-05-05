package org.ironworkschurch.events.service

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import org.ironworkschurch.events.dto.json.Event
import org.ironworkschurch.events.dto.json.Events
import org.ironworkschurch.events.dto.json.SermonItem
import org.ironworkschurch.events.dto.json.Sermons
import org.slf4j.LoggerFactory
import java.io.Reader
import java.net.URL
import java.util.zip.GZIPInputStream

class EventsService constructor(
  val eventsUrl: String,
  val hiddenEventsUrl: String,
  val sermonsUrl: String,
  private val objectMapper: ObjectMapper) {
  private val logger = LoggerFactory.getLogger(EventsService::class.java)

  val rss: List<Event>
    get() = listOf(publicEvents, hiddenEvents)
      .map { it.use { objectMapper.readValue(it, Events::class.java) } }
      .flatMap { it.upcoming + it.past }

  val publicEvents: Reader
    get() = getContents("public events", eventsUrl)

  val hiddenEvents: Reader
    get() = getContents("hidden events", hiddenEventsUrl)

  private fun getContents(name: String, pageUrl: String): Reader {
    logger.debug("Fetching $name RSS")
    val url = URL("$pageUrl?format=json")
    val connection = url.openConnection()
    connection.setRequestProperty("Accept-Encoding", "gzip")
    val inputStream = if ("gzip" == connection.contentEncoding) {
      GZIPInputStream(connection.getInputStream())
    } else {
      connection.getInputStream()
    }

    return inputStream.bufferedReader(Charsets.UTF_8)
  }

  val sermons: List<SermonItem>
    get() = getContents("sermons", sermonsUrl)
            .use {
              objectMapper.readValue(it, Sermons::class.java)
            }
            .items
}