package org.ironworkschurch.events

import org.ironworkschurch.events.config.DaggerServiceComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import javax.inject.Inject

open class Application @Inject constructor(val eventsManager: EventsManager,
                                           val templateEngine: TemplateEngine) {
  fun run() {
    log.debug("retrieving events")
    /*val fakeWeekly = Triple(listOf<List<Event>>(), listOf<List<Event>>(), listOf<List<Event>>())
    val (thisWeekItems, futureItems, ongoingItems) = fakeWeekly*/
    val (thisWeekItems, futureItems, ongoingItems, lastSermon) = eventsManager.getWeeklyItems()

    val context = Context().apply {
      setVariable("thisWeek", thisWeekItems)
      setVariable("upcoming", futureItems)
      setVariable("ongoing", ongoingItems)
      setVariable("lastSermon", lastSermon)
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
}

