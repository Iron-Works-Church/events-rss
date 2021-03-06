package org.ironworkschurch.events.config

import com.fasterxml.jackson.databind.ObjectMapper
import dagger.internal.DoubleCheck
import dagger.internal.Factory
import org.ironworkschurch.events.Application
import org.ironworkschurch.events.EventsManager
import org.ironworkschurch.events.SermonManager
import org.ironworkschurch.events.service.EventsService
import org.thymeleaf.TemplateEngine
import javax.inject.Provider

class DaggerServiceComponent private constructor() : ServiceComponent {
  private lateinit var provideEventsUrlProvider: Provider<String>
  private lateinit var provideRepeatingEventsUrlProvider: Provider<String>
  private lateinit var provideOngoingEventsUrlProvider: Provider<String>
  private lateinit var provideSermonsUrlProvider: Provider<String>
  private lateinit var getObjectMapperProvider: Provider<ObjectMapper>
  private lateinit var provideEventsServiceProvider: Provider<EventsService>
  private lateinit var eventsManagerProvider: Provider<EventsManager>
  private lateinit var applicationProvider: Provider<Application>
  private lateinit var templateEngineProvider: Provider<TemplateEngine>
  private lateinit var sermonManagerProvider: Provider<SermonManager>
  private lateinit var provideEmailLookup: Provider<Map<String, String>>
  private lateinit var provideUrlRoot: Provider<String>
  private lateinit var provideSleepTime: Provider<Long>

  override fun getApplication(): Application = applicationProvider.get()

  companion object {
    fun create(): ServiceComponent {
      return DaggerServiceComponent().apply {
        val serviceConfig = ServiceConfig()
        provideEventsUrlProvider = Factory { serviceConfig.provideEventsUrl() }
        provideRepeatingEventsUrlProvider = Factory { serviceConfig.provideRepeatingEventsUrl() }
        provideOngoingEventsUrlProvider = Factory { serviceConfig.provideOngoingEventsUrl() }
        provideSermonsUrlProvider = Factory { serviceConfig.provideSermonsUrl() }
        getObjectMapperProvider = DoubleCheck.provider({ serviceConfig.objectMapper })
        provideSleepTime = DoubleCheck.provider({ serviceConfig.provideSleepTime() })
        provideEventsServiceProvider = Factory { EventsService(
                provideEventsUrlProvider.get(),
                provideOngoingEventsUrlProvider.get(),
                provideRepeatingEventsUrlProvider.get(),
                provideSermonsUrlProvider.get(),
                getObjectMapperProvider.get(),
                provideSleepTime.get())
        }

        templateEngineProvider = DoubleCheck.provider({ serviceConfig.templateEngine })
        provideEmailLookup = Factory { serviceConfig.provideEmailLookup() }
        provideUrlRoot = Factory { serviceConfig.provideIwcUrlRoot() }
        eventsManagerProvider = Factory {
          EventsManager(provideEventsServiceProvider.get(), getObjectMapperProvider.get())
        }
        sermonManagerProvider = Factory {
          SermonManager(provideEventsServiceProvider.get(), getObjectMapperProvider.get(), provideEmailLookup.get(), provideUrlRoot.get())
        }
        applicationProvider = Factory { Application(eventsManagerProvider.get(), sermonManagerProvider.get(), templateEngineProvider.get()) }
      }
    }
  }
}
