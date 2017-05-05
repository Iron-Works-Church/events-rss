package org.ironworkschurch.events.dto

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.io.Resources
import org.assertj.core.api.Assertions.assertThat
import org.ironworkschurch.events.dto.json.Events
import org.junit.Test

class TestParse {

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
}