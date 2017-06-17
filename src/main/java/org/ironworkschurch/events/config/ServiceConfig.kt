package org.ironworkschurch.events.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.io.Resources
import dagger.Module
import dagger.Provides
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

@Module
class ServiceConfig {
  private val properties: Properties
  private val emailLookup: Map<String, String>

  init {
    this.properties = getAppProperties()
    this.emailLookup = getEmailLookupMap()
  }

  private fun getAppProperties(): Properties {
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
    return properties
  }

  private fun getEmailLookupMap(): Map<String, String> {
    val emailMap = try {
      Resources.readLines(Resources.getResource("emaillookup.csv"), Charset.defaultCharset()).parseToMap()
    } catch (e: IOException) {
      throw RuntimeException(e)
    }

    val emailLookupFile = File("emaillookup.csv")
    if (emailLookupFile.exists()) {
      val map = emailLookupFile.readLines().parseToMap()
      emailMap.putAll(map)
    }

    return emailMap.toMap()
  }

  private fun List<String>.parseToMap() =
    map { it.split(delimiters = ',', limit = 2) }
    .map { it[0] to it[1] }
    .toMap()
    .toMutableMap()

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
  @Named("org.ironworkschurch.repeating-events-url")
  internal fun provideRepeatingEventsUrl(): String {
    return properties.getProperty("org.ironworkschurch.repeating-events-url")
  }

  @Provides
  @Named("org.ironworkschurch.ongoing-events-url")
  internal fun provideOngoingEventsUrl(): String {
    return properties.getProperty("org.ironworkschurch.ongoing-events-url")
  }

  @Provides
  @Named("org.ironworkschurch.sermons-url")
  internal fun provideSermonsUrl(): String {
    return properties.getProperty("org.ironworkschurch.sermons-url")
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

  @Provides
  fun provideEmailLookup() = emailLookup

  @Provides
  @Named("org.ironworkschurch.url-root")
  fun provideIwcUrlRoot(): String = properties.getProperty("org.ironworkschurch.url-root")
}
