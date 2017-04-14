package org.ironworkschurch.events.dto

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict = false)
class Channel {
  @field:ElementList(inline = true, required = false)
  var links: List<Link>? = null
    private set

  @field:ElementList(inline = true)
  lateinit var items: List<Item>
    private set

  @field:Element
  lateinit var title: String
   private set

  @field:Element(required = false)
  var language: String? = null
   private set

  @field:Element(required = false)
  var description: String? = null
   private set

  @field:Element(required = false)
  var lastBuildDate: String? = null
   private set

  @field:Element(required = false)
  var ttl: Int? = null
   private set

  @field:Element(required = false)
  var updatePeriod: String? = null
   private set

  @field:Element(required = false)
  var updateFrequency: Int? = null
   private set

  @field:Element(required = false)
  var generator: String? = null
   private set

  @field:Element(required = false)
  var image: Image? = null
   private set
}