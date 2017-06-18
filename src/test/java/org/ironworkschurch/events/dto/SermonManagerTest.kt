package org.ironworkschurch.events.dto

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.io.Resources
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions
import org.ironworkschurch.events.SermonManager
import org.ironworkschurch.events.config.ServiceConfig
import org.ironworkschurch.events.service.EventsService
import org.junit.Test
import java.nio.charset.Charset

class SermonManagerTest {


  @Test
  fun testSermon() {

    val eventsService = mock<EventsService> {
      on { sermons } doReturn Resources.toString(Resources.getResource("sermons.json"), Charset.defaultCharset())
    }
    val objectMapper = ObjectMapper()
      .registerModule(KotlinModule())
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    val serviceConfig = ServiceConfig()

    val sermonManager = SermonManager(eventsService, objectMapper, serviceConfig.provideEmailLookup(), serviceConfig.provideIwcUrlRoot())
    val displaySermon = sermonManager.getLastSermon()
    Assertions.assertThat(displaySermon).isNotNull()
    Assertions.assertThat(displaySermon!!.title).isEqualTo("In Truth, We Are Guilty")
    Assertions.assertThat(displaySermon.url).isEqualTo("https://ironworkschurch.org/sermons/2017/6/11/in-truth-we-are-guilty")
    Assertions.assertThat(displaySermon.author).isEqualTo("Rev. Darin Pesnell")
    Assertions.assertThat(displaySermon.authorEmail).isEqualTo("darin@ironworkschurch.org")
  }
}