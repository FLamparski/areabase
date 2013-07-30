package areabase.tests.basic;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.discovery.FindAreasMethodCall;
import nde2.methodcalls.discovery.GetAreaDetailMethodCall;
import nde2.types.discovery.Area;
import nde2.types.discovery.DetailedArea;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class GetAreaDetailTestCase {

	Area baseArea;

	@Before
	public void setUp() throws Exception, ValueNotAvailable {
		baseArea = new FindAreasMethodCall().addPostcode("SE6 4UX").findAreas()
				.get(0);
	}

	@Test
	public void test() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception {
		DetailedArea myArea = new GetAreaDetailMethodCall().addArea(baseArea)
				.getAreaDetail();
		assertEquals("536344:173279:536742:173513", myArea.getEnvelope());
	}

}
