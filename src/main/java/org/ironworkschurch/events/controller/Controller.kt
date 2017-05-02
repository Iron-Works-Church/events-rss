package org.ironworkschurch.events.controller

import org.ironworkschurch.events.EventsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.View
import org.thymeleaf.spring4.view.ThymeleafViewResolver
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
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
    val items = eventsService.rss

    val now = LocalDateTime.now()
    val nextSunday = now.plusWeeks(1).plusDays(1)
    val nextMonth = now.plus(1, ChronoUnit.MONTHS).plusDays(1)

    var futureItems = items
            .filter { it.dateRange?.upperEndpoint()?.isAfter(now) ?: false }

    val thisWeekItems = futureItems
            .filter { it.dateRange?.lowerEndpoint()?.isAfter(now) ?: false }
            .filter { it.dateRange?.lowerEndpoint()?.isBefore(nextSunday) ?: false }

    val ongoingItems = futureItems
            .filter { it.dateRange?.lowerEndpoint()?.isBefore(now) ?: false }
            .filter { it.dateRange?.upperEndpoint()?.isAfter(nextSunday) ?: false }

    futureItems = futureItems
            .filter { it !in thisWeekItems }
            .filter { it !in ongoingItems }
            .filter { it.dateRange?.upperEndpoint()?.isBefore(nextMonth) ?: false}

    val modelMap = mapOf("thisWeek" to thisWeekItems,
            "upcoming" to futureItems,
            "ongoing" to ongoingItems)

    return ModelAndView(view, modelMap)
  }

  @RequestMapping(value = "/rss", produces = arrayOf("text/xml"))
  fun streamRss(response: HttpServletResponse) {
    response.contentType = "application/xml"
    eventsService.publicEvents
            .byteInputStream()
            .use { inputStream ->
              response.outputStream.use {
                inputStream.copyTo(it)
              }
            }
  }
}
