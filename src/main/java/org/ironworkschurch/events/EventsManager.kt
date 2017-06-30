package org.ironworkschurch.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.Range
import mu.KotlinLogging
import org.ironworkschurch.events.dto.DisplayEvent
import org.ironworkschurch.events.dto.DisplaySermon
import org.ironworkschurch.events.dto.WeeklyItems
import org.ironworkschurch.events.dto.json.Event
import org.ironworkschurch.events.dto.json.Events
import org.ironworkschurch.events.service.EventsService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

open class EventsManager @Inject constructor(val eventsService: EventsService,
                                             private val objectMapper: ObjectMapper) {
  private val logger = KotlinLogging.logger {}

  fun getWeeklyItems(): WeeklyItems {
    val now = LocalDateTime.now()
    val isFuture: (Event) -> Boolean = { it.dateRange.upperEndpoint()?.isAfter(now) ?: false }
    val events = toEvents(eventsService.publicEvents).filter(isFuture)
    val ongoing = toEvents(eventsService.ongoingEvents).filter(isFuture)
    val repeating = toEvents(eventsService.repeatingEvents).filter(isFuture)

    logger.debug { "Found ${events.size} public events" }
    logger.debug { "Found ${ongoing.size} ongoing events" }
    logger.debug { "Found ${repeating.size} repeating events" }

    val nextSunday = now.plusWeeks(1).plusDays(1)
    val nextMonth = now.plus(1, ChronoUnit.MONTHS).plusDays(1)

    val thisWeek = Range.closed(now, nextSunday)

    logger.debug { "Using $thisWeek as \"this week\"" }

    val thisWeekItems = (repeating + events)
      .filter { it.dateRange.isConnected(thisWeek) }

    logger.debug { "Found ${thisWeekItems.size} items for \"this week\"" }

    val ongoingItems: List<Event> = ongoing

    logger.debug { "Found ${ongoingItems.size} items for \"ongoing\"" }

    logger.debug { "Using ${Range.closed(nextSunday, nextMonth)} for \"upcoming\" items " }

    val futureItems = events
      .filter { it !in thisWeekItems }
      .filter { it !in ongoingItems }
      .filter { it.dateRange.upperEndpoint()?.isBefore(nextMonth) ?: false}


    logger.debug { "Found ${futureItems.size} items for \"upcoming\"" }

    return WeeklyItems (
      thisWeekItems = thisWeekItems.map { toDisplayItem(it) },
      futureItems = futureItems.map { toDisplayItem(it) },
      ongoingItems = ongoingItems.map { toDisplayItem(it) }
    )
  }

  private fun toDisplayItem(event: Event): DisplayEvent {
    val dateRange = event.dateRange
    val startDate = dateRange.lowerEndpoint().truncatedTo(ChronoUnit.DAYS)
    val endDate = dateRange.upperEndpoint().truncatedTo(ChronoUnit.DAYS)
    val dateStr: String? =
            when (startDate) {
              endDate -> startDate.format(DateTimeFormatter.ofPattern("MMMM d"))
              else -> null
            }
    return DisplayEvent(title = event.title,
            excerpt = event.excerpt?.removeSurrounding("<p>", "</p>"),
            date = dateStr)
  }

  private fun toEvents(eventsStr: String): List<Event> {
    val events = objectMapper.readValue(eventsStr, Events::class.java)
    return events.upcoming + events.past
  }
}