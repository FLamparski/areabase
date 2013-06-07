import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.methodcalls.discovery.FindAreasMethodCall;
import nde2.types.discovery.Area;

import org.junit.Test;
import org.xml.sax.SAXException;

public class FindAreasTestCase {

	@Test
	public void testFindAreas() {
		List<Area> areas = null;
		try {
			areas = new FindAreasMethodCall().addPostcode("SE6 4UX")
					.findAreas();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			fail("XPath exception");
		} catch (NDE2Exception e) {
			e.printStackTrace();
			fail("Web service error");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			fail("XML parser configuration error");
		} catch (SAXException e) {
			e.printStackTrace();
			fail("XML parser error");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Communication error");
		}
		Area smallest = areas.get(0);
		assertEquals("E00016308", smallest.getName());
		try {
			assertEquals(smallest.getParent().getName(), "Lewisham");
		} catch (XPathExpressionException | ParserConfigurationException
				| SAXException | IOException | NDE2Exception e) {
			e.printStackTrace();
			fail("Exception was thrown.");
		}
	}

}
