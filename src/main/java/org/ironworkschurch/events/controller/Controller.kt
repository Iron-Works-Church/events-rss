package org.ironworkschurch.events.controller

import com.google.common.collect.ImmutableMap
import org.apache.commons.io.IOUtils
import org.ironworkschurch.events.dto.RssRoot
import org.simpleframework.xml.core.Persister
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import java.io.IOException
import java.net.URL
import javax.servlet.http.HttpServletResponse
import javax.xml.bind.JAXBException

@org.springframework.stereotype.Controller
class Controller {
  @Value("\${events-url}")
  private lateinit var eventsUrl: String

  private val serializer: Persister by lazy { Persister() }


  @RequestMapping(value = "/", produces = arrayOf("text/html"))
  @Throws(IOException::class, JAXBException::class)
  fun toHtml(): ModelAndView {
    val url = URL(eventsUrl)

    val rss = url.openStream().use {
      serializer.read(RssRoot::class.java, it)
    }

    return ModelAndView("view", ImmutableMap.of("rss", rss))
  }

  @RequestMapping(value = "/rss", produces = arrayOf("text/xml"))
  fun streamRss(response: HttpServletResponse) {
    val url = URL(eventsUrl)

    response.outputStream.use { outputStream ->
      url.openStream()
              .use {
                inputStream ->
                response.contentType = "application/xml"

                IOUtils.copy(inputStream, outputStream)
                response.flushBuffer()
              }
    }
  }
}
