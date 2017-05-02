package org.ironworkschurch.events.dto

import com.google.common.io.Resources
import org.junit.Test
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister

class TestParse {
  private val serializer: Serializer = Persister()

  @Test
  fun testParse() {
    Resources.getResource("rss.xml")
            .readText()
            .parse(serializer, RssRoot::class.java)

  }

  fun String.parse(serializer: Serializer, rootClass: Class<*>) = serializer.read(rootClass, this)
}