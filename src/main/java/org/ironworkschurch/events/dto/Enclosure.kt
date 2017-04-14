package org.ironworkschurch.events.dto

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(strict = false)
class Enclosure {
  @field:Attribute
  lateinit var url: String
    private set

  @field:Attribute
  lateinit var type: String
    private set

  @field:Attribute(required = false)
  var length: Int? = null
    private set
}

