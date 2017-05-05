package org.ironworkschurch.events.config;

import dagger.Component;
import org.ironworkschurch.events.Application;
import org.ironworkschurch.events.EventsManager;

import javax.inject.Singleton;

@Component(modules = ServiceConfig.class)
@Singleton
public interface ServiceComponent {
  EventsManager getEventsManager();
  Application getApplication();
}
