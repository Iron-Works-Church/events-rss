package org.ironworkschurch.events

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.thymeleaf.context.Context
import org.thymeleaf.spring4.SpringTemplateEngine
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

@SpringBootApplication
open class Application @Autowired constructor(val eventsManager: EventsManager) : ApplicationRunner {
  override fun run(args: ApplicationArguments?) {
    val templateEngine = springTemplateEngine()

    val (thisWeekItems, futureItems, ongoingItems) = eventsManager.getWeeklyItems()

    val context = Context().apply {
      setVariable("thisWeek", thisWeekItems)
      setVariable("upcoming", futureItems)
      setVariable("ongoing", ongoingItems)
    }

    val output = templateEngine.process("view", context)
    println(output)
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      SpringApplication.run(Application::class.java, *args)
    }
  }

  private fun springTemplateEngine() = SpringTemplateEngine().apply {
    addTemplateResolver(ClassLoaderTemplateResolver().apply {
      prefix = "templates/"
      suffix = ".html"
    })
  }
}

