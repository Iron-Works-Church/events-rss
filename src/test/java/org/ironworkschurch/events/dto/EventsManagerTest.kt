package org.ironworkschurch.events.dto

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.io.Resources
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.ironworkschurch.events.EventsManager
import org.ironworkschurch.events.service.EventsService
import org.junit.Test
import java.nio.charset.Charset

class EventsManagerTest {
  @Test
  fun test() {
    val eventsService = mock<EventsService> {
      on { publicEvents } doReturn Resources.toString(Resources.getResource("events.json"), Charset.defaultCharset())
      on { ongoingEvents } doReturn Resources.toString(Resources.getResource("ongoing.json"), Charset.defaultCharset())
      on { repeatingEvents } doReturn Resources.toString(Resources.getResource("repeating.json"), Charset.defaultCharset())
      on { sermons } doReturn Resources.toString(Resources.getResource("sermons.json"), Charset.defaultCharset())
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
}