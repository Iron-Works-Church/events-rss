package org.ironworkschurch.events

import org.ironworkschurch.events.dto.RssRoot
import javax.cache.annotation.CacheResult

interface EventsService {
  @get:CacheResult val rss: RssRoot
  @get:CacheResult val contents: String
}