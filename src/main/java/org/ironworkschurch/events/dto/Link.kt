package org.ironworkschurch.events.dto

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text

@Root(strict = false)
class Link {
  @field:Attribute(required = false)
  var href: String? = null
    private set

  @field:Attribute(required = false)
  var rel: String? = null
    private set

  @field:Attribute(required = false)
  var type: String? = null
    private set

  @field:Attribute(required = false)
  var title: String? = null
    private set

  @field:Text(required = false)
  var link: String? = null
    private set
}