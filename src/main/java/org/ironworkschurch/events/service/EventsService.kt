package org.ironworkschurch.events.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.ironworkschurch.events.dto.json.Event
import org.ironworkschurch.events.dto.json.Events
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.zip.GZIPInputStream

open class EventsService constructor(val eventsUrl: String,
                                     val ongoingEventsUrl: String,
                                     val repeatingEventsUrl: String,
                                     val sermonsUrl: String,
                                     private val objectMapper: ObjectMapper,
                                     val sleepMillis: Long) {
  private val logger = LoggerFactory.getLogger(EventsService::class.java)

  val rss: List<Event>
    get() = listOf(publicEvents, ongoingEvents, repeatingEvents)
      .map { objectMapper.readValue(it, Events::class.java) }
      .flatMap { it.upcoming + it.past }

  open val publicEvents: String
    get() = getContents("public events", eventsUrl)

  open val ongoingEvents: String
    get() = getContents("ongoing events", ongoingEventsUrl)

  open val repeatingEvents: String
    get() = getContents("repeating events", repeatingEventsUrl)

  private fun getContents(name: String, pageUrl: String): String {
    logger.debug("Fetching $name RSS")
    Thread.sleep(sleepMillis)
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

  open val sermons: String
   get() = getContents("sermons", sermonsUrl)
}
