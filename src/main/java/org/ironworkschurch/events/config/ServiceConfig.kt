package org.ironworkschurch.events.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.io.Resources
import dagger.Module
import dagger.Provides
import org.ironworkschurch.events.service.EventsService

import javax.inject.Named
import javax.inject.Singleton
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Properties

@Module
class ServiceConfig {
  private val properties: Properties

  init {
    val properties = Properties()
    try {
      Resources.getResource("application.properties").openStream().use { stream -> properties.load(stream) }
    } catch (e: IOException) {
      throw RuntimeException(e)
    }

    val file = File("application.properties")
    if (file.exists()) {
      try {
        FileInputStream(file).use { fs -> properties.load(fs) }
      } catch (e: IOException) {
        throw RuntimeException(e)
      }

    }

    this.properties = properties
  }

  @Provides
  @Named("org.ironworkschurch.events-url")
  internal fun provideEventsUrl(): String {
    return properties.getProperty("org.ironworkschurch.events-url")
  }

  @Provides
  @Named("org.ironworkschurch.hidden-events-url")
  internal fun provideHiddenEventsUrl(): String {
    return properties.getProperty("org.ironworkschurch.hidden-events-url")
  }

  @Provides
  @Singleton
  internal fun provideEventsService(@Named("org.ironworkschurch.events-url") newEventsUrl: String,
                                    @Named("org.ironworkschurch.hidden-events-url") hiddenEventsUrl: String,
                                    objectMapper: ObjectMapper): EventsService {
    return EventsService(newEventsUrl, hiddenEventsUrl, objectMapper)
  }


  val objectMapper: ObjectMapper
    @Provides
    @Singleton
    get() {
      val objectMapper = ObjectMapper()
              .registerModule(KotlinModule())
              .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      return objectMapper
    }
}
