package org.ironworkschurch.events.dto

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.io.Resources
import org.assertj.core.api.Assertions.assertThat
import org.ironworkschurch.events.dto.json.Events
import org.junit.Test
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister

class TestParse {
  private val serializer: Serializer = Persister()

  @Test
  fun testParseRss() {
    val parsedRss = Resources.getResource("rss.xml")
      .readText()
      .parse(serializer, RssRoot::class.java)
    assertThat(parsedRss).isNotNull()
  }


  @Test
  fun testParseJson() {
    val json = Resources.getResource("new-events.json")
      .readText()
    val objectMapper = ObjectMapper()
      .registerModule(KotlinModule())
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    val events = objectMapper.readValue(json, Events::class.java)
    assertThat(events).isNotNull()
  }

  fun String.parse(serializer: Serializer, rootClass: Class<*>) = serializer.read(rootClass, this)
}