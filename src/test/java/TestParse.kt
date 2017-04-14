import com.google.common.io.Resources
import org.ironworkschurch.events.dto.Channel
import org.ironworkschurch.events.dto.RssRoot
import org.junit.Test
import org.simpleframework.xml.core.Persister
import java.nio.charset.StandardCharsets
import javax.xml.bind.JAXB
import javax.xml.transform.stream.StreamSource

/**
 * Created by raymondrishty on 4/14/17.
 */
class TestParse {
  @Test
  fun testParse() {
    val resource = Resources.getResource("rss.xml")
    val rssStr = Resources.toString(resource, StandardCharsets.UTF_8);

    val serializer = Persister()
    val rss =
      serializer.read(RssRoot::class.java, rssStr)

  }

}