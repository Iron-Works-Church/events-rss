package org.ironworkschurch.events.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.ironworkschurch.events.service.EventsService;

import javax.inject.Named;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServiceConfig extends AbstractModule
{
  @Override
  protected void configure() {
    Properties props = new Properties();
    try (InputStream stream = Resources.getResource("application.properties").openStream()) {
      props.load(stream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    File file = new File("application.properties");
    if (file.exists()) {
      try (FileInputStream fs = new FileInputStream(file)) {
        props.load(fs);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    Names.bindProperties(binder(), props);
  }

  @Provides
  EventsService getEventsService(@Named("org.ironworkschurch.events-url") String newEventsUrl,
                                 @Named("org.ironworkschurch.hidden-events-url") String hiddenEventsUrl,
                                 ObjectMapper objectMapper) {
    return new EventsService(newEventsUrl, hiddenEventsUrl, objectMapper);
  }


  @Provides
  @Singleton
  public ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new KotlinModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }
}
