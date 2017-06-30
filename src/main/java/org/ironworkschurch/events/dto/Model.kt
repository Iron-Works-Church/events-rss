package org.ironworkschurch.events.dto

import org.ironworkschurch.events.dto.json.Event

data class DisplaySermon (val title: String, val url: String?, val author: String?, val authorEmail: String?)

data class DisplayEvent (val title: String?, val excerpt: String?, val date: String?)

data class WeeklyItems(val thisWeekItems: List<DisplayEvent>,
                       val futureItems: List<DisplayEvent>,
                       val ongoingItems: List<DisplayEvent>)