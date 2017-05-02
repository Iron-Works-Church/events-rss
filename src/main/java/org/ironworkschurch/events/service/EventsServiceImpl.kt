package org.ironworkschurch.events.service

open class EventsServiceImpl : EventsService {
  @org.springframework.beans.factory.annotation.Value("\${events-url}")
  protected lateinit var eventsUrl: String
  @org.springframework.beans.factory.annotation.Value("\${hidden-events-url}")
  protected lateinit var hiddenEventsUrl: String
  protected val serializer: org.simpleframework.xml.core.Persister by lazy { org.simpleframework.xml.core.Persister() }
  private val logger = org.slf4j.LoggerFactory.getLogger(org.ironworkschurch.events.config.CacheConfig::class.java)

  @get:javax.cache.annotation.CacheResult
  override val rss: List<org.ironworkschurch.events.dto.Item> get() {
    logger.debug("Fetching RSS")
    return listOf(publicEvents, hiddenEvents)
            .flatMap { serializer.read(org.ironworkschurch.events.dto.RssRoot::class.java, it).channel.items }
            .onEach { item -> item.populateDateRange() }
    // TODO add sermon link
  }

  @get:javax.cache.annotation.CacheResult
  override val publicEvents: String get() {
    return getContents("public events", eventsUrl)
  }

  @get:javax.cache.annotation.CacheResult
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
      java.util.zip.GZIPInputStream(connection.getInputStream())
    } else {
      connection.getInputStream()
    }

    return inputStream.bufferedReader(Charsets.UTF_8).readText()
  }

  private fun org.ironworkschurch.events.dto.Item.populateDateRange() {
    logger.debug("Populating date range for $guid")
    Thread.sleep(500)
    val connection = java.net.URL(link + "?format=ical").openConnection()

    connection.setRequestProperty("Accept-Encoding", "gzip")
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");

    val inputStream = try {
      if ("gzip" == connection.contentEncoding) {
        java.util.zip.GZIPInputStream(connection.getInputStream())
      } else {
        connection.getInputStream()
      }
    } catch (e: java.io.IOException) {
      logger.debug(connection.headerFields.toString())
      throw e
    }

    val icalStr = inputStream.bufferedReader(Charsets.UTF_8).readText()
    val ical = biweekly.Biweekly.parse(icalStr).first();
    val event = ical.events.first()

    dateRange = com.google.common.collect.Range.closedOpen(event.dateStart.toLocalDateTime(), event.dateEnd.toLocalDateTime())
  }

  private fun biweekly.property.DateOrDateTimeProperty.toLocalDateTime(): java.time.LocalDateTime? {
    val rawComponents = value.rawComponents
    return java.time.LocalDateTime.of(rawComponents.year, rawComponents.month, rawComponents.date, rawComponents.hour, rawComponents.minute, rawComponents.second)
  }
}


