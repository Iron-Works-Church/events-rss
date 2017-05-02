package org.ironworkschurch.events.dto

import com.google.common.io.Resources
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister

class TestParse {
  private val serializer: Serializer = Persister()

  @Test
  fun testParse() {
    val parsedRss = Resources.getResource("rss.xml")
      .readText()
      .parse(serializer, RssRoot::class.java)
    assertThat(parsedRss).isNotNull()
  }

  fun String.parse(serializer: Serializer, rootClass: Class<*>) = serializer.read(rootClass, this)
}