package org.ironworkschurch.events.config

import com.fasterxml.jackson.databind.ObjectMapper
import dagger.internal.DoubleCheck
import dagger.internal.Factory
import org.ironworkschurch.events.Application
import org.ironworkschurch.events.EventsManager
import org.ironworkschurch.events.service.EventsService
import javax.inject.Provider

class DaggerServiceComponent private constructor() : ServiceComponent {
  private lateinit var provideEventsUrlProvider: Provider<String>
  private lateinit var provideHiddenEventsUrlProvider: Provider<String>
  private lateinit var provideRepeatingEventsUrlProvider: Provider<String>
  private lateinit var provideOngoingEventsUrlProvider: Provider<String>
  private lateinit var provideSermonsUrlProvider: Provider<String>
  private lateinit var getObjectMapperProvider: Provider<ObjectMapper>
  private lateinit var provideEventsServiceProvider: Provider<EventsService>
  private lateinit var eventsManagerProvider: Provider<EventsManager>
  private lateinit var applicationProvider: Provider<Application>

  override fun getEventsManager(): EventsManager = eventsManagerProvider.get()

  override fun getApplication(): Application = applicationProvider.get()

  companion object {
    fun create(): ServiceComponent {
      return DaggerServiceComponent().apply {
        val serviceConfig = ServiceConfig()
        provideEventsUrlProvider = Factory { serviceConfig.provideEventsUrl() }
        provideHiddenEventsUrlProvider = Factory { serviceConfig.provideHiddenEventsUrl() }
        provideRepeatingEventsUrlProvider = Factory { serviceConfig.provideRepeatingEventsUrl() }
        provideOngoingEventsUrlProvider = Factory { serviceConfig.provideOngoingEventsUrl() }
        provideSermonsUrlProvider = Factory { serviceConfig.provideSermonsUrl() }
        getObjectMapperProvider = DoubleCheck.provider({ serviceConfig.objectMapper })
        provideEventsServiceProvider = DoubleCheck.provider(
                {
                  serviceConfig.provideEventsService(
                          provideEventsUrlProvider.get(),
                          provideHiddenEventsUrlProvider.get(),
                          provideRepeatingEventsUrlProvider.get(),
                          provideOngoingEventsUrlProvider.get(),
                          provideSermonsUrlProvider.get(),
                          getObjectMapperProvider.get())
                })

        eventsManagerProvider = Factory { EventsManager(provideEventsServiceProvider.get(), getObjectMapperProvider.get()) }
        applicationProvider = Factory { Application(eventsManagerProvider.get()) }
      }
    }
  }
}
