package org.ironworkschurch.events.config

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled

@Configuration
open class CacheConfig {
  private val logger = LoggerFactory.getLogger(CacheConfig::class.java)

  @CacheEvict(allEntries = true, cacheNames = arrayOf("org.ironworkschurch.events.publicEvents()",
          "org.ironworkschurch.events.hiddenEvents()",
          "org.ironworkschurch.events.rss()"))
  @Scheduled(fixedDelay = (60 * 60 * 1000).toLong()) // evict every hour
  open fun reportCacheEvict() {
    logger.debug("Flushing cache")
  }

}
