package areabase.tests.basic;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.discovery.FindAreasMethodCall;
import nde2.types.discovery.Area;

import org.junit.Test;
import org.xml.sax.SAXException;

public class FindAreasTestCase {

	@Test
	public void testFindAreas() throws XPathExpressionException, NDE2Exception,
			ParserConfigurationException, SAXException, IOException,
			ValueNotAvailable {
		List<Area> areas = null;

		areas = new FindAreasMethodCall().addPostcode("SE6 4UX").findAreas();

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
