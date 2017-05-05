package org.ironworkschurch.events.config

import com.fasterxml.jackson.databind.ObjectMapper
import dagger.internal.DoubleCheck
import dagger.internal.Factory
import org.ironworkschurch.events.Application
import org.ironworkschurch.events.EventsManager
import org.ironworkschurch.events.service.EventsService
import org.thymeleaf.TemplateEngine
import javax.inject.Provider

class DaggerServiceComponent private constructor() : ServiceComponent {
  private lateinit var provideEventsUrlProvider: Provider<String>
  private lateinit var provideHiddenEventsUrlProvider: Provider<String>
  private lateinit var provideSermonsUrlProvider: Provider<String>
  private lateinit var getObjectMapperProvider: Provider<ObjectMapper>
  private lateinit var provideEventsServiceProvider: Provider<EventsService>
  private lateinit var eventsManagerProvider: Provider<EventsManager>
  private lateinit var templateEngineProvider: Provider<TemplateEngine>
  private lateinit var applicationProvider: Provider<Application>

  override fun getApplication(): Application = applicationProvider.get()

  companion object {
    fun create(): ServiceComponent {
      return DaggerServiceComponent().apply {
        val serviceConfig = ServiceConfig()
        provideEventsUrlProvider = Factory { serviceConfig.provideEventsUrl() }
        provideHiddenEventsUrlProvider = Factory { serviceConfig.provideHiddenEventsUrl() }
        provideSermonsUrlProvider = Factory { serviceConfig.provideSermonsUrl() }
        getObjectMapperProvider = DoubleCheck.provider({ serviceConfig.objectMapper })
        provideEventsServiceProvider = DoubleCheck.provider(
                {
                  serviceConfig.provideEventsService(
                          provideEventsUrlProvider.get(),
                          provideHiddenEventsUrlProvider.get(),
                          provideSermonsUrlProvider.get(),
                          getObjectMapperProvider.get())
                })

        templateEngineProvider = DoubleCheck.provider({ serviceConfig.templateEngine })
        eventsManagerProvider = Factory { EventsManager(provideEventsServiceProvider.get()) }
        applicationProvider = Factory { Application(eventsManagerProvider.get(), templateEngineProvider.get()) }
      }
    }
  }
}
