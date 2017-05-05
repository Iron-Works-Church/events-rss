package org.ironworkschurch.events

import org.ironworkschurch.events.dto.json.Event
import org.ironworkschurch.events.dto.json.SermonItem
import org.ironworkschurch.events.service.EventsService
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

open class EventsManager @Inject constructor(val eventsService: EventsService) {
  fun getWeeklyItems(): WeeklyItems {
    val items = eventsService.rss
    val lastSermon = eventsService.sermons
            .sortedByDescending { it.addedOn }
            .firstOrNull()

    val now = LocalDateTime.now()
    val nextSunday = now.plusWeeks(1).plusDays(1)
    val nextMonth = now.plus(1, ChronoUnit.MONTHS).plusDays(1)

    var futureItems = items
      .filter { it.dateRange.upperEndpoint()?.isAfter(now) ?: false }

    val thisWeekItems = futureItems
      .filter { it.dateRange.lowerEndpoint()?.isAfter(now) ?: false }
      .filter { it.dateRange.lowerEndpoint()?.isBefore(nextSunday) ?: false }

    val ongoingItems = futureItems
      .filter { it.dateRange.lowerEndpoint()?.isBefore(now) ?: false }
      .filter { it.dateRange.upperEndpoint()?.isAfter(nextSunday) ?: false }

    futureItems = futureItems
      .filter { it !in thisWeekItems }
      .filter { it !in ongoingItems }
      .filter { it.dateRange.upperEndpoint()?.isBefore(nextMonth) ?: false}

    return WeeklyItems (
      thisWeekItems = thisWeekItems,
      futureItems = futureItems,
      ongoingItems = ongoingItems,
      lastSermon = lastSermon
    )
  }

  data class WeeklyItems(val thisWeekItems: List<Event>, val futureItems: List<Event>, val ongoingItems: List<Event>, val lastSermon: SermonItem?)
}