package org.ironworkschurch.events.controller

import org.ironworkschurch.events.EventsManager
import org.ironworkschurch.events.service.EventsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.View
import org.thymeleaf.spring4.view.ThymeleafViewResolver
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletResponse
import javax.xml.bind.JAXBException

@Controller
class Controller @Autowired constructor(var viewResolver: ThymeleafViewResolver,
                                        var eventsService: EventsService,
                                        var eventsManager: EventsManager) {

  private val view: View by lazy { viewResolver.resolveViewName("view", Locale.getDefault()) }

  @RequestMapping(value = "/", produces = arrayOf("text/html"))
  @Throws(IOException::class, JAXBException::class)
  fun toHtml(): ModelAndView {
    val (thisWeekItems, futureItems, ongoingItems) = eventsManager.getWeeklyItems()

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
