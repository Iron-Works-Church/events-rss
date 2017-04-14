package org.ironworkschurch.events.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
  @RequestMapping(value = "/", produces = "text/html")
  public String toHtml() {
    return "<html><head></head><body>Hello <i>Iron Works</i></body></html>";
  }
}
