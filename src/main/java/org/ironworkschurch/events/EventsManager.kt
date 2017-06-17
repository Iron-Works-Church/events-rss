package org.ironworkschurch.events

import com.fasterxml.jackson.databind.ObjectMapper
import org.ironworkschurch.events.dto.DisplaySermon
import org.ironworkschurch.events.dto.WeeklyItems
import org.ironworkschurch.events.dto.json.Event
import org.ironworkschurch.events.dto.json.Events
import org.ironworkschurch.events.dto.json.Sermon
import org.ironworkschurch.events.dto.json.Sermons
import org.ironworkschurch.events.service.EventsService
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

open class EventsManager @Inject constructor(val eventsService: EventsService,
                                             private val objectMapper: ObjectMapper) {
  fun getWeeklyItems(): WeeklyItems {
    val now = LocalDateTime.now()
    val isFuture: (Event) -> Boolean = { it.dateRange.upperEndpoint()?.isAfter(now) ?: false }
    val events = toEvents(eventsService.publicEvents).filter(isFuture)
    val ongoing = toEvents(eventsService.ongoingEvents).filter(isFuture)
    val repeating = toEvents(eventsService.repeatingEvents).filter(isFuture)
    val sermons = toSermons(eventsService.sermons)
    val lastSermon = sermons.maxBy { it.addedOn }

    val nextSunday = now.plusWeeks(1).plusDays(1)
    val nextMonth = now.plus(1, ChronoUnit.MONTHS).plusDays(1)

    val thisWeekItems = repeating
      .filter { it.dateRange.lowerEndpoint()?.isAfter(now) ?: false }
      .filter { it.dateRange.lowerEndpoint()?.isBefore(nextSunday) ?: false }

    val ongoingItems: List<Event> = ongoing
      //.filter { it.dateRange.lowerEndpoint()?.isBefore(now) ?: false }
      //.filter { it.dateRange.upperEndpoint()?.isAfter(nextSunday) ?: false }

    var futureItems = events
      .filter { it !in thisWeekItems }
      .filter { it !in ongoingItems }
      .filter { it.dateRange.upperEndpoint()?.isBefore(nextMonth) ?: false}

    val value = WeeklyItems (
      thisWeekItems = thisWeekItems,
      futureItems = futureItems,
      ongoingItems = ongoingItems,
      lastSermon = lastSermon.displaySermon()
    )

    return value
  }

  private fun Sermon?.displaySermon(): DisplaySermon? {
    return null
  }

  private fun toEvents(eventsStr: String): List<Event> {
    val events = objectMapper.readValue(eventsStr, Events::class.java)
    return events.upcoming + events.past
  }

  private fun toSermons(eventsStr: String) =
    objectMapper.readValue(eventsStr, Sermons::class.java).items

}