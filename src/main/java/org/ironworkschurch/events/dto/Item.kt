package org.ironworkschurch.events.dto

import com.google.common.collect.Range
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.time.LocalDateTime

@Root(strict = false)
class Item {
  constructor(guid: String,
              title: String,
              link: String,
              description: String,
              pubDate: String,
              creator: String? = null,
              enclosure: Enclosure? = null,
              encoded: String? = null,
              thumbnails: List<Thumbnail>? = null,
              dateRange: Range<LocalDateTime>? = null) {
    this.guid = guid
    this.title = title
    this.link = link
    this.description = description
    this.pubDate = pubDate
    this.creator = creator
    this.enclosure = enclosure
    this.encoded = encoded
    this.thumbnails = thumbnails
    this.dateRange = dateRange
  }

  @Suppress("unused")
  private constructor()

  @field:Element
  lateinit var guid: String
    private set

  @field:Element
  lateinit var title: String
    private set

  @field:Element
  lateinit var link: String
    private set

  @field:Element
  lateinit var description: String
    private set

  @field:Element
  lateinit var pubDate: String
    private set

  @field:Element(required = false)
  var creator: String? = null
   private set

  @field:Element(required = false)
  var enclosure: Enclosure? = null
   private set

  @field:Element(required = false)
  var encoded: String? = null
   private set

  @field:ElementList(inline = true, required = false)
  var thumbnails: List<Thumbnail>? = null
   private set

  var dateRange: Range<LocalDateTime>? = null
}
