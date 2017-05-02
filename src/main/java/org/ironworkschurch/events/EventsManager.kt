package org.ironworkschurch.events

import org.ironworkschurch.events.dto.Item
import org.ironworkschurch.events.service.EventsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
open class EventsManager @Autowired constructor(val eventsService: EventsService) {
  fun getWeeklyItems(): WeeklyItems {
    val items = eventsService.rss

    val now = LocalDateTime.now()
    val nextSunday = now.plusWeeks(1).plusDays(1)
    val nextMonth = now.plus(1, ChronoUnit.MONTHS).plusDays(1)

    var futureItems = items
      .filter { it.dateRange?.upperEndpoint()?.isAfter(now) ?: false }

    val thisWeekItems = futureItems
      .filter { it.dateRange?.lowerEndpoint()?.isAfter(now) ?: false }
      .filter { it.dateRange?.lowerEndpoint()?.isBefore(nextSunday) ?: false }

    val ongoingItems = futureItems
      .filter { it.dateRange?.lowerEndpoint()?.isBefore(now) ?: false }
      .filter { it.dateRange?.upperEndpoint()?.isAfter(nextSunday) ?: false }

    futureItems = futureItems
      .filter { it !in thisWeekItems }
      .filter { it !in ongoingItems }
      .filter { it.dateRange?.upperEndpoint()?.isBefore(nextMonth) ?: false}

    val value = WeeklyItems (
      thisWeekItems = thisWeekItems,
      futureItems = futureItems,
      ongoingItems = ongoingItems
    )

    return value
  }

  data class WeeklyItems(val thisWeekItems: List<Item>, val futureItems: List<Item>, val ongoingItems: List<Item>)
}