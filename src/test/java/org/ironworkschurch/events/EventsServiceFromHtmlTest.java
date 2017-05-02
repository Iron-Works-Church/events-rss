package org.ironworkschurch.events;

import com.google.common.collect.Range;
import org.ironworkschurch.events.EventsServiceFromHtml;
import org.junit.Test;

import java.time.LocalDateTime;

public class EventsServiceFromHtmlTest {
  @Test
  public void testGetDateRange() {
    String url = "http://www.google.com/calendar/event?action=TEMPLATE&text=Young+Lives+Baby+Bottle+Fundraiser&dates=20170514T143000Z/20170618T163000Z";
    Range<LocalDateTime> dateRange = new EventsServiceFromHtml().getDateRange(url);
  }
}
