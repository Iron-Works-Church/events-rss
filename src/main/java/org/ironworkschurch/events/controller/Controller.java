package org.ironworkschurch.events.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
  @Value("${events-url}")
  private String eventsUrl;

  @RequestMapping(value = "/", produces = "text/html")
  public String toHtml() {
    return "<html><head></head><body>Hello <i>Iron Works</i>. Check out the <a href=\"" + eventsUrl + "\">events feed</a></body></html>";
  }
}
