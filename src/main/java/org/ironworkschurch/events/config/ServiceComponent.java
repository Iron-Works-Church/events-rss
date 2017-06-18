package org.ironworkschurch.events.config;

import javax.inject.Singleton;

import dagger.Component;
import org.ironworkschurch.events.Application;

@Component(modules = ServiceConfig.class)
@Singleton
public interface ServiceComponent {
  Application getApplication();
}
