package org.ironworkschurch.events.dto

import org.ironworkschurch.events.dto.json.Event

data class DisplaySermon (val title: String, val url: String?, val author: String?, val authorEmail: String?)

data class WeeklyItems(val thisWeekItems: List<Event>,
                       val futureItems: List<Event>,
                       val ongoingItems: List<Event>)