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

  fun fromSermon(sermon: Sermon): DisplaySermon = DisplaySermon(
    title = sermon.title,
    url = sermon.fullUrl?.let { iwcUrlRoot.trimEnd('/') + '/' + it.trimStart('/') },
    author = sermon.tags.firstOrNull{ sermon.excerpt?.contains(it) ?: false },
    authorEmail = emailLookup[sermon.tags.firstOrNull{ sermon.excerpt?.contains(it) ?: false }]
  )

  fun toSermons(sermonsStr: String) =
    objectMapper.readValue(sermonsStr, Sermons::class.java).items

}