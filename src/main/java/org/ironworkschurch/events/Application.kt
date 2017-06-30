package org.ironworkschurch.events

import mu.KotlinLogging
import org.ironworkschurch.events.config.DaggerServiceComponent
import org.ironworkschurch.events.dto.DisplaySermon
import org.ironworkschurch.events.dto.WeeklyItems
import org.ironworkschurch.events.dto.json.Event
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import javax.inject.Inject

open class Application @Inject constructor(val eventsManager: EventsManager,
                                           val sermonManager: SermonManager,
                                           val templateEngine: TemplateEngine) {
  fun run() {
    log.debug("retrieving events")
    //val fakeWeekly = Triple(listOf<List<Event>>(), listOf<List<Event>>(), listOf<List<Event>>())
    val weeklyItems = eventsManager.getWeeklyItems()
    val lastSermon = sermonManager.getLastSermon()

    log.debug("retrieved events")

    val output = renderOutput(weeklyItems, lastSermon, templateEngine)
    println(output)
  }

  fun renderOutput(weeklyItems: WeeklyItems, lastSermon: DisplaySermon?, templateEngine: TemplateEngine): String? {
    val context = Context().apply {
      setVariable("thisWeek", weeklyItems.thisWeekItems.map { it.stripClosingP() })
      setVariable("upcoming", weeklyItems.futureItems.map { it.stripClosingP() })
      setVariable("ongoing", weeklyItems.ongoingItems.map { it.stripClosingP() })
      setVariable("lastSermon", lastSermon)
    }

    log.debug("processing html template")
    val output = templateEngine.process("view", context)

    log.info("complete")
    return output
  }

  companion object {
    private val log = KotlinLogging.logger {}

    @JvmStatic
    fun main(args: Array<String>) {
      log.info("beginning email generation")

      log.debug("injecting dependencies")
      val application = DaggerServiceComponent.create().application

      application.run()
    }
  }

  fun templateEngine() = TemplateEngine().apply {
    setTemplateResolver(ClassLoaderTemplateResolver().apply {
      prefix = "templates/"
      suffix = ".html"
    })
  }
}

private fun Event.stripClosingP() = copy(excerpt = this.excerpt?.removeSurrounding("<p>", "</p>"))

