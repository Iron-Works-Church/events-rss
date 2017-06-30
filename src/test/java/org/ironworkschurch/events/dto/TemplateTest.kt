package org.ironworkschurch.events.dto

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.io.Resources
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.ironworkschurch.events.Application
import org.ironworkschurch.events.EventsManager
import org.ironworkschurch.events.SermonManager
import org.ironworkschurch.events.config.ServiceConfig
import org.ironworkschurch.events.service.EventsService
import org.junit.Test
import java.nio.charset.Charset
import java.time.*
import java.time.zone.ZoneRulesProvider.getRules
import java.time.ZoneOffset
import java.time.ZoneId



class TemplateTest {
  @Test
  fun test() {

    val eventsService = mock<EventsService> {
      on { publicEvents } doReturn Resources.toString(Resources.getResource("events.json"), Charset.defaultCharset())
      on { ongoingEvents } doReturn Resources.toString(Resources.getResource("ongoing.json"), Charset.defaultCharset())
      on { repeatingEvents } doReturn Resources.toString(Resources.getResource("repeating.json"), Charset.defaultCharset())
      on { sermons } doReturn Resources.toString(Resources.getResource("sermons.json"), Charset.defaultCharset())
    }

    val serviceConfig = ServiceConfig()
    val objectMapper = serviceConfig.objectMapper

    val localDateTime = LocalDateTime.of(2017, Month.JUNE, 30, 0, 0, 0)
    val zoneId = ZoneId.systemDefault()
    val currentOffsetForMyZone = zoneId.rules.getOffset(localDateTime)

    val eventsManager = EventsManager(eventsService, objectMapper, Clock.fixed( localDateTime.toInstant(currentOffsetForMyZone), zoneId) )
    val sermonManager = SermonManager(eventsService, objectMapper, serviceConfig.provideEmailLookup(), serviceConfig.provideIwcUrlRoot())
    //val weeklyItems = eventsManager.getWeeklyItems()
    //val displaySermon = sermonManager.getLastSermon()
    val application = Application(eventsManager, sermonManager, serviceConfig.templateEngine)
    application.run()
    //val renderOutput = application.renderOutput(weeklyItems, displaySermon, application.templateEngine())
    //println(renderOutput)
  }
}