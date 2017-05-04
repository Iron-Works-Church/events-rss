package org.ironworkschurch.events

import com.google.inject.AbstractModule
import com.google.inject.Guice
import org.ironworkschurch.events.config.ServiceConfig
import org.ironworkschurch.events.dto.json.Event
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import javax.inject.Inject

open class Application @Inject constructor(val eventsManager: EventsManager) {
  fun run() {
    val templateEngine = templateEngine()

    log.debug("retrieving events")
    val (thisWeekItems, futureItems, ongoingItems) = Triple(listOf<List<Event>>(), listOf<List<Event>>(), listOf<List<Event>>())//eventsManager.getWeeklyItems()

    val context = Context().apply {
      setVariable("thisWeek", thisWeekItems)
      setVariable("upcoming", futureItems)
      setVariable("ongoing", ongoingItems)
    }

    log.debug("processing html template")
    val output = templateEngine.process("view", context)

    log.debug("complete")
    println(output)
  }

  companion object {
    val log = LoggerFactory.getLogger(Application::class.java)
    @JvmStatic
    fun main(args: Array<String>) {
      log.debug("beginning email generation")

      log.debug("acquiring dependency injector")
      val injector = Guice.createInjector(ServiceConfig())

      injector.getInstance(Application::class.java).run()
    }
  }

  private fun templateEngine() = TemplateEngine().apply {
    setTemplateResolver(ClassLoaderTemplateResolver().apply {
      prefix = "templates/"
      suffix = ".html"
    })
  }
}

