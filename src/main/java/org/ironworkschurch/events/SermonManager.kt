package org.ironworkschurch.events

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.ironworkschurch.events.dto.DisplaySermon
import org.ironworkschurch.events.dto.json.Sermon
import org.ironworkschurch.events.dto.json.Sermons
import org.ironworkschurch.events.service.EventsService
import org.slf4j.LoggerFactory
import javax.inject.Inject

open class SermonManager @Inject constructor(val eventsService: EventsService,
                                             private val objectMapper: ObjectMapper,
                                             val emailLookup: Map<String, String>,
                                             val iwcUrlRoot: String) {
  private val logger = KotlinLogging.logger {}

  fun getLastSermon(): DisplaySermon? {
    val sermons = toSermons(eventsService.sermons)

    logger.debug { "found ${sermons.size} sermons" }
    val lastSermon = sermons.maxBy { it.addedOn }

    logger.debug {
      when(lastSermon) {
        null -> "Did not find last sermon"
        else -> "Found last sermon"
      }
    }

    val sermon = lastSermon?.let { fromSermon(it) }

    logger.debug { sermon }
    return sermon
  }

  val authorRegex = """data-author="([^"]+)"""".toRegex()

  fun fromSermon(sermon: Sermon): DisplaySermon {
    val author = sermon.getAuthor()
    return DisplaySermon(
            title = sermon.title,
            url = sermon.fullUrl?.let { iwcUrlRoot.trimEnd('/') + '/' + it.trimStart('/') },
            author = author,
            authorEmail = emailLookup[author]
    )
  }

  private fun Sermon.getAuthor(): String? =
    authorRegex.find(body)?.groupValues?.get(1)
            ?: tags.firstOrNull { excerpt?.contains(it) ?: false }


  fun toSermons(sermonsStr: String) =
    objectMapper.readValue(sermonsStr, Sermons::class.java).items

}