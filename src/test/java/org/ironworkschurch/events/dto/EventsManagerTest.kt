package org.ironworkschurch.events.dto

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.io.Resources
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.ironworkschurch.events.EventsManager
import org.ironworkschurch.events.service.EventsService
import org.junit.Test
import java.nio.charset.Charset
import java.time.Clock
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

class EventsManagerTest {
  @Test
  fun test() {
    val eventsService = mock<EventsService> {
      on { publicEvents } doReturn Resources.toString(Resources.getResource("events.json"), Charset.defaultCharset())
      on { ongoingEvents } doReturn Resources.toString(Resources.getResource("ongoing.json"), Charset.defaultCharset())
      on { repeatingEvents } doReturn Resources.toString(Resources.getResource("repeating.json"), Charset.defaultCharset())
    }
    val objectMapper = ObjectMapper()
      .registerModule(KotlinModule())
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    val eventsManager = EventsManager(eventsService, objectMapper)
    val (thisWeekItems, futureItems, ongoingItems) = eventsManager.getWeeklyItems()
    println("this week: " + thisWeekItems.map { "  " + it.title })
    println("upcoming: " + futureItems.map { "  " + it.title })
    println("ongoing: " + ongoingItems.map { "  " + it.title })
  }

  @Test
  fun testGetDaysToAdd() {
    val eventsService = mock<EventsService>()
    val objectMapper = mock<ObjectMapper>()

    val localDateTime = LocalDateTime.of(2017, Month.JUNE, 30, 0, 0, 0)
    val zoneId = ZoneId.systemDefault()
    val currentOffsetForMyZone = zoneId.rules.getOffset(localDateTime)

    val eventsManager = EventsManager(eventsService, objectMapper, Clock.fixed( localDateTime.toInstant(currentOffsetForMyZone), zoneId) )
    assertThat(eventsManager.getDaysToAdd(LocalDateTime.of(2017, Month.JUNE, 30, 1, 0))).isEqualTo(1)
    assertThat(eventsManager.getDaysToAdd(LocalDateTime.of(2017, Month.JULY,  1, 1, 0))).isEqualTo(0)
    assertThat(eventsManager.getDaysToAdd(LocalDateTime.of(2017, Month.JULY,  2, 1, 0))).isEqualTo(6)
    assertThat(eventsManager.getDaysToAdd(LocalDateTime.of(2017, Month.JULY,  3, 1, 0))).isEqualTo(5)
    assertThat(eventsManager.getDaysToAdd(LocalDateTime.of(2017, Month.JULY,  4, 1, 0))).isEqualTo(4)
    assertThat(eventsManager.getDaysToAdd(LocalDateTime.of(2017, Month.JULY,  5, 1, 0))).isEqualTo(3)
    assertThat(eventsManager.getDaysToAdd(LocalDateTime.of(2017, Month.JULY,  6, 1, 0))).isEqualTo(2)
  }
}
