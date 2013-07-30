package areabase.tests.extended;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.types.discovery.Area;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Tests for behaviour when getting comparators for "top-level" areas. Note that
 * one of such "top-level" areas is Wales, which <b>should</b> return Great
 * Britain or England & Wales, but ONS doesn't think so, and I just have to roll
 * with it.
 * 
 * @author filip
 * 
 */
public class ExtremeAreaComparators {

	private Area greatBritainArea;

	private Area walesArea;

	@Before
	public void setUp() throws Exception {
		walesArea = new Area("Wales", 6274992l, 10, 26);
		greatBritainArea = new Area("Great Britain", 6274989l, 8, 26);
	}

	@SuppressWarnings("unused")
	@Test(expected = ValueNotAvailable.class)
	public void test_wales() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, IllegalArgumentException, IllegalAccessException,
			ValueNotAvailable {
		Area foundArea = walesArea.getParent();
	}

	@SuppressWarnings("unused")
	@Test(expected = ValueNotAvailable.class)
	public void test_gb() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, IllegalArgumentException, IllegalAccessException,
			ValueNotAvailable {
		Area foundArea = greatBritainArea.getParent();
	}

}
