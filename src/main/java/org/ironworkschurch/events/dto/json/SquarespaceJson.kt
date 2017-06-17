package org.ironworkschurch.events.dto.json

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.common.collect.Range
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class Events(
  val upcoming: List<Event> = listOf(),
  val past: List<Event> = listOf()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Event(
  val id: String,
  val collectionId: String,
  val recordType: Int,
  val addedOn: Long,
  val updatedOn: Long,
  val tags: List<String> = listOf(),
  val categories: List<String> = listOf(),
  val workflowState: Int,
  val publishOn: Long,
  val author: Author?,
  val title: String?,
  val sourceUrl: String?,
  val body: String?,
  val excerpt: String?,
  val fullUrl: String?,
  val startDate: Long,
  val endDate: Long
) {

  private fun EpochSeconds.localDateTime() = java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(this), java.time.ZoneId.systemDefault())
  val dateRange: Range<LocalDateTime> by lazy { Range.closedOpen(startDate.localDateTime(), endDate.localDateTime()) }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Author(val displayName: String)

typealias EpochSeconds = Long

data class Sermons(
  val items: List<Sermon> = listOf()
)

data class Sermon(
  val id: String,
  val collectionId: String,
  val title: String,
  val publishOn: Long,
  val addedOn: Long,
  val updatedOn: Long,
  val recordType: Int,
  val fullUrl: String?,
  val tags: List<String> = listOf()
)
