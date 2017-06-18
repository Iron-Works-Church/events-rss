package org.ironworkschurch.events

import com.fasterxml.jackson.databind.ObjectMapper
import org.ironworkschurch.events.dto.DisplaySermon
import org.ironworkschurch.events.dto.json.Sermon
import org.ironworkschurch.events.dto.json.Sermons
import org.ironworkschurch.events.service.EventsService
import javax.inject.Inject

open class SermonManager @Inject constructor(val eventsService: EventsService,
                                             private val objectMapper: ObjectMapper,
                                             val emailLookup: Map<String, String>,
                                             val iwcUrlRoot: String) {
  fun getLastSermon(): DisplaySermon? {
    val sermons = toSermons(eventsService.sermons)
    val lastSermon = sermons.maxBy { it.addedOn }

    return lastSermon?.let { fromSermon(it) }
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