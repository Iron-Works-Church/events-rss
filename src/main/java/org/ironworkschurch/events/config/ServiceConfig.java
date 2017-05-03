package org.ironworkschurch.events.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import org.ironworkschurch.events.service.EventsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig
{
  @Bean EventsService getEventsService(@Value("${org.ironworkschurch.events-url}") String newEventsUrl,
                                       @Value("${org.ironworkschurch.hidden-events-url}") String hiddenEventsUrl) {
    ObjectMapper objectMapper = new ObjectMapper()
      .registerModule(new KotlinModule())
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return new EventsService(newEventsUrl, hiddenEventsUrl, objectMapper);
  }
}
