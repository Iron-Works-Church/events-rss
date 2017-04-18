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
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.WeekFields
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

    val weekFields = WeekFields.of(DayOfWeek.SUNDAY, 7)
    val weekOfYear = weekFields.weekOfYear()

    val now = LocalDateTime.now()
    val thisWeek = now.plusDays(1)[weekOfYear]

    var futureItems = rss.channel
            .items
            .filter { it.dateRange?.lowerEndpoint()?.isAfter(now) ?: false }

    val thisWeekItems = futureItems
            .filter { it.dateRange?.lowerEndpoint()?.get(weekOfYear) == thisWeek }

    futureItems = futureItems.filter { it !in thisWeekItems }

    val modelMap = mapOf("thisWeek" to thisWeekItems,
            "upcoming" to futureItems)

    return ModelAndView(view, modelMap)
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
