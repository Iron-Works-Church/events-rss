package org.ironworkschurch.events.dto

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict = false)
class Item {
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

}
