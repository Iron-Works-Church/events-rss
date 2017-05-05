package org.ironworkschurch.events.config;

import dagger.Component;
import org.ironworkschurch.events.Application;

import javax.inject.Singleton;

@Component(modules = ServiceConfig.class)
@Singleton
public interface ServiceComponent {
  Application getApplication();
}
