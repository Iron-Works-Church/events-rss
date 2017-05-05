package org.ironworkschurch.events

import org.ironworkschurch.events.config.DaggerServiceComponent
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
    val fakeWeekly = Triple(listOf<List<Event>>(), listOf<List<Event>>(), listOf<List<Event>>())
    val (thisWeekItems, futureItems, ongoingItems) = eventsManager.getWeeklyItems()

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
    val log: Logger = LoggerFactory.getLogger(Application::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
      log.debug("beginning email generation")

      log.debug("injecting dependencies")
      val application = DaggerServiceComponent.create().application

      application.run()
    }
  }

  private fun templateEngine() = TemplateEngine().apply {
    setTemplateResolver(ClassLoaderTemplateResolver().apply {
      prefix = "templates/"
      suffix = ".html"
    })
  }
}

