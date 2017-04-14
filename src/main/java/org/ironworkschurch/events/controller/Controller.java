package org.ironworkschurch.events.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

@RestController
public class Controller {
  @Value("${events-url}")
  private String eventsUrl;

  @RequestMapping(value = "/", produces = "text/html")
  public void toHtml(HttpServletResponse response) throws IOException {
    URL url = new URL(eventsUrl);
    try (InputStream inputStream = url.openStream();
         OutputStream outputStream = response.getOutputStream();) {

      response.setContentType("application/xml");

      IOUtils.copy(inputStream, outputStream);
      response.flushBuffer();
    }
  }
}
