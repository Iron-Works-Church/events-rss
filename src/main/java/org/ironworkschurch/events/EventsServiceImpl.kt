package org.ironworkschurch.events

import org.ironworkschurch.events.config.CacheConfig
import org.ironworkschurch.events.dto.RssRoot
import org.simpleframework.xml.core.Persister
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URL
import javax.cache.annotation.CacheResult

@Component
open class EventsServiceImpl : EventsService {
  @Value("\${events-url}")
  protected lateinit var eventsUrl: String
  protected val serializer: Persister by lazy { Persister() }
  private val logger = LoggerFactory.getLogger(CacheConfig::class.java)

  @get:CacheResult
  override val rss: RssRoot get() {
    logger.debug("Fetching RSS")
    return URL(eventsUrl)
            .openStream()
            .use {
              serializer.read(RssRoot::class.java, it)
            }
  }

  @get:CacheResult
  override val contents: String get() {
    logger.debug("Fetching RSS")
    return URL(eventsUrl).readText(Charsets.UTF_8)
  }
}
