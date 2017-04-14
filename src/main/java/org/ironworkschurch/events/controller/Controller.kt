package org.ironworkschurch.events.controller

import com.google.common.collect.ImmutableMap
import org.ironworkschurch.events.EventsServiceImpl
import org.ironworkschurch.events.EventsService
import org.simpleframework.xml.core.Persister
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.View
import org.thymeleaf.spring4.view.ThymeleafViewResolver
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletResponse
import javax.xml.bind.JAXBException

@org.springframework.stereotype.Controller
class Controller {
  @Autowired lateinit var viewResolver: ThymeleafViewResolver
  @Autowired lateinit var eventsService: EventsService

  private val view: View by lazy { viewResolver.resolveViewName("view", Locale.getDefault()) }

  @RequestMapping(value = "/", produces = arrayOf("text/html"))
  @Throws(IOException::class, JAXBException::class)
  fun toHtml(): ModelAndView {
    val rss = eventsService.rss

    return ModelAndView(view, ImmutableMap.of("rss", rss))
  }

  @RequestMapping(value = "/rss", produces = arrayOf("text/xml"))
  fun streamRss(response: HttpServletResponse) {
    response.contentType = "application/xml"
    eventsService.contents
            .byteInputStream()
            .use { inputStream ->
              response.outputStream.use {
                inputStream.copyTo(it)
              }
            }
  }
}
