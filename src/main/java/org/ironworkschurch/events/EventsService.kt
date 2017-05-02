package org.ironworkschurch.events

import org.ironworkschurch.events.dto.Item
import javax.cache.annotation.CacheResult

interface EventsService {
  @get:CacheResult val rss: List<Item>
  @get:CacheResult val publicEvents: String
  @get:CacheResult val hiddenEvents: String
}