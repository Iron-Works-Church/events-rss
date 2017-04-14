package org.ironworkschurch.events.dto

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(strict = false)
class Thumbnail {
  @field:Attribute
  lateinit var url: String
    private set
}