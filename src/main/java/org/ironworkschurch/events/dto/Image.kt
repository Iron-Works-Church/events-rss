package org.ironworkschurch.events.dto

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(strict = false)
class Image {
  @field:Element
  lateinit var url: String
    private set

  @field:Element
  lateinit var title: String
    private set

  @field:Element
  lateinit var link: String
    private set
}