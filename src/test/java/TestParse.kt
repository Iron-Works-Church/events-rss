import com.google.common.io.Resources
import org.ironworkschurch.events.dto.Channel
import org.ironworkschurch.events.dto.RssRoot
import org.junit.Test
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.xml.bind.JAXB
import javax.xml.transform.stream.StreamSource
import kotlin.reflect.KClass

class TestParse {
  private val serializer: Serializer = Persister()

  @Test
  fun testParse() {
    Resources.getResource("rss.xml")
            .readText()
            .parse(serializer, RssRoot::class.java)

  }

  fun String.parse(serializer: Serializer, rootClass: Class<*>) = serializer.read(rootClass, this)
}