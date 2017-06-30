package org.ironworkschurch.events

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.Range
import mu.KotlinLogging
import org.ironworkschurch.events.dto.DisplayEvent
import org.ironworkschurch.events.dto.WeeklyItems
import org.ironworkschurch.events.dto.json.Event
import org.ironworkschurch.events.dto.json.Events
import org.ironworkschurch.events.service.EventsService
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

open class EventsManager @Inject constructor(val eventsService: EventsService,
                                             private val objectMapper: ObjectMapper,
                                             private val clock: Clock = Clock.systemDefaultZone()) {
  private val logger = KotlinLogging.logger {}

  fun getWeeklyItems(): WeeklyItems {
    val thisSaturday = getThisSaturday(LocalDateTime.now(clock)).plusHours(6)
    val isFuture: (Event) -> Boolean = { it.dateRange.upperEndpoint()?.isAfter(thisSaturday) ?: false }
    val events = toEvents(eventsService.publicEvents).filter(isFuture)
    val ongoing = toEvents(eventsService.ongoingEvents).filter(isFuture)
    val repeating = toEvents(eventsService.repeatingEvents).filter(isFuture)

    logger.debug { "Found ${events.size} public events" }
    logger.debug { "Found ${ongoing.size} ongoing events" }
    logger.debug { "Found ${repeating.size} repeating events" }

    val nextSunday = thisSaturday.plusWeeks(1).plusDays(1)
    val nextMonth = thisSaturday.plus(1, ChronoUnit.MONTHS).plusDays(1)

    val thisWeek = Range.closed(thisSaturday, nextSunday)

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

  /**
   * Returns the next saturday after the given date, or the given date if it is a saturday
   */
  private fun getThisSaturday(now: LocalDateTime): LocalDateTime {
    val truncatedDate = now.truncatedTo(ChronoUnit.DAYS)
    val thisComingSaturday = truncatedDate.plus(getDaysToAdd(truncatedDate).toLong(), ChronoUnit.DAYS)
    return thisComingSaturday; // break;
  }

  /**
   * returns the days until the next Saturday after the given date, or 0 if the given date is Saturday
   * Sunday +6
   * Monday +5
   * Tuesday +4
   * Wednesday +3
   * Thursday +2
   * Friday +1
   * Saturday +0
   */
  fun getDaysToAdd(now: LocalDateTime): Int {
    return DayOfWeek.SATURDAY.value - (now.dayOfWeek.value % 7)
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