package org.ironworkschurch.events.service;

import java.time.LocalDateTime;

import com.google.common.collect.Range;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventsServiceFromHtmlTest {
  @Test
  public void testGetDateRange() {
    String url = "http://www.google.com/calendar/event?action=TEMPLATE&text=Young+Lives+Baby+Bottle+Fundraiser&dates=20170514T143000Z/20170618T163000Z";
    Range<LocalDateTime> dateRange = new EventsServiceFromHtml("dummy-url", "dummy-url").getDateRange(url);
    assertThat(dateRange).isNotNull();
    assertThat(dateRange.upperEndpoint()).isAfter(dateRange.lowerEndpoint());
  }
}
