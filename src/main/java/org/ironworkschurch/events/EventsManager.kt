package org.ironworkschurch.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.Range
import org.ironworkschurch.events.dto.DisplaySermon
import org.ironworkschurch.events.dto.WeeklyItems
import org.ironworkschurch.events.dto.json.Event
import org.ironworkschurch.events.dto.json.Events
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

    val nextSunday = now.plusWeeks(1).plusDays(1)
    val nextMonth = now.plus(1, ChronoUnit.MONTHS).plusDays(1)

    val thisWeek = Range.closed(now, nextSunday)

    val thisWeekItems = (repeating + events)
      .filter { it.dateRange.isConnected(thisWeek) }

    val ongoingItems: List<Event> = ongoing

    val futureItems = events
      .filter { it !in thisWeekItems }
      .filter { it !in ongoingItems }
      .filter { it.dateRange.upperEndpoint()?.isBefore(nextMonth) ?: false}

    val value = WeeklyItems (
      thisWeekItems = thisWeekItems,
      futureItems = futureItems,
      ongoingItems = ongoingItems
    )

    return value
  }

  private fun toEvents(eventsStr: String): List<Event> {
    val events = objectMapper.readValue(eventsStr, Events::class.java)
    return events.upcoming + events.past
  }

  fun toSermons(eventsStr: String) =
    objectMapper.readValue(eventsStr, Sermons::class.java).items

}