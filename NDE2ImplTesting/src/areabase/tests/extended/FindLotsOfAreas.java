package areabase.tests.extended;

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

import areabase.tests.Repr;
import areabase.tests.basic.FindAreasTestCase;

/**
 * A comprehensive test of the FindAreas functionality. Unlike
 * {@link FindAreasTestCase}, this test comprises of multiple calls to
 * FindArea(), using some of the more dodgy data, such as Scotland.
 * 
 * @author filip
 * 
 */
public class FindLotsOfAreas {

	/**
	 * Tests for a smooth retrieval of a list of areas for a valid postcode.
	 * 
	 * @throws XPathExpressionException
	 * @throws NDE2Exception
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws ValueNotAvailable
	 */
	@Test
	public void findAreasByPostcode_1() throws XPathExpressionException,
			NDE2Exception, ParserConfigurationException, SAXException,
			IOException, IllegalArgumentException, IllegalAccessException,
			ValueNotAvailable {
		String postcode = "SE6 4UX";
		List<Area> foundAreas = new FindAreasMethodCall().addPostcode(postcode)
				.findAreas();
		System.out.printf(
				"[postcode_1] Found %d areas for %s; closest match: %s\n",
				foundAreas.size(), postcode, foundAreas.get(0).getName());
		Repr.repr(foundAreas.get(0));
	}

	/**
	 * Tests for behaviour when the supplied postcode is invalid and a server
	 * error has occurred.
	 * 
	 * @throws XPathExpressionException
	 * @throws NDE2Exception
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ValueNotAvailable
	 */
	@Test(expected = NDE2Exception.class)
	public void findAreasByPostcode_2() throws XPathExpressionException,
			NDE2Exception, ParserConfigurationException, SAXException,
			IOException, ValueNotAvailable {
		String postcode = "INVALID";
		List<Area> foundAreas = new FindAreasMethodCall().addPostcode(postcode)
				.addLevelTypeId(Area.LEVELTYPE_OA).findAreas();
		System.out
				.printf("[postcode_2] Found %d areas for %s (leveltype 15); closest match: %s\n",
						foundAreas.size(), postcode, foundAreas.get(0)
								.getName());
	}

	/**
	 * Tests for a broad retrieval of areas containing the name "Greenwich"
	 * 
	 * @throws XPathExpressionException
	 * @throws NDE2Exception
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ValueNotAvailable
	 */
	@Test
	public void findAreasByName_1() throws XPathExpressionException,
			NDE2Exception, ParserConfigurationException, SAXException,
			IOException, ValueNotAvailable {
		String name = "Greenwich";
		List<Area> foundAreas = new FindAreasMethodCall().addAreaNamePart(name)
				.findAreas();
		System.out.printf("[name_1] Found %d areas for %s; first match: %s\n",
				foundAreas.size(), name, foundAreas.get(0).getName());
	}

	/**
	 * Tests for a retrieval of areas containing the name "Greenwich" that are
	 * in the 2011 Administrative Hierarchy (Area.HIERARCHY_2011_ADMINISTRATIVE)
	 * 
	 * @throws XPathExpressionException
	 * @throws NDE2Exception
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ValueNotAvailable
	 */
	@Test
	public void findAreasByName_2() throws XPathExpressionException,
			NDE2Exception, ParserConfigurationException, SAXException,
			IOException, ValueNotAvailable {
		String name = "Greenwich";
		List<Area> foundAreas = new FindAreasMethodCall().addAreaNamePart(name)
				.addHierarchyId(Area.HIERARCHY_2011_ADMINISTRATIVE).findAreas();
		System.out
				.printf("[name_2] Found %d areas for %s (hierarchy 27); first match: %s\n",
						foundAreas.size(), name, foundAreas.get(0).getName());
	}

	/**
	 * Tests for behaviour when the postcode is in Scotland. NDE, in its
	 * infinite wisdom, returns an empty element instead of signaling an error,
	 * so client code will have to check for Scotland when it receives a
	 * {@link ValueNotAvailable}.
	 * 
	 * @throws XPathExpressionException
	 * @throws NDE2Exception
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ValueNotAvailable
	 */
	@Test(expected = ValueNotAvailable.class)
	public void findAreasByPostcode_Scotland() throws XPathExpressionException,
			NDE2Exception, ParserConfigurationException, SAXException,
			IOException, ValueNotAvailable {
		String postcode = "EH16 5AY";
		List<Area> foundAreas = new FindAreasMethodCall().addPostcode(postcode)
				.findAreas();
		System.out
				.printf("[postcode_scotland] Found %d areas for %s (hierarchy 27); first match: %s\n",
						foundAreas.size(), postcode, foundAreas.get(0)
								.getName());
	}

	/**
	 * Tests for behaviour when the postcode is in Wales.
	 * 
	 * @throws XPathExpressionException
	 * @throws NDE2Exception
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ValueNotAvailable
	 */
	@Test
	public void findAreasByPostcode_Wales() throws XPathExpressionException,
			NDE2Exception, ParserConfigurationException, SAXException,
			IOException, ValueNotAvailable {
		String postcode = "CF24 4AB";
		List<Area> foundAreas = new FindAreasMethodCall().addPostcode(postcode)
				.findAreas();
		System.out
				.printf("[postcode_wales] Found %d areas for %s (hierarchy 27); first match: %s\n",
						foundAreas.size(), postcode, foundAreas.get(0)
								.getName());
	}

	/**
	 * Tests for behaviour when the postcode is in N. Ireland. NDE, in its
	 * infinite wisdom, returns an empty element instead of signaling an error,
	 * so client code will have to check for Scotland when it receives a
	 * {@link ValueNotAvailable}.
	 * 
	 * @throws XPathExpressionException
	 * @throws NDE2Exception
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ValueNotAvailable
	 */
	@Test(expected = ValueNotAvailable.class)
	public void findAreasByPostcode_NIreland() throws XPathExpressionException,
			NDE2Exception, ParserConfigurationException, SAXException,
			IOException, ValueNotAvailable {
		String postcode = "BT20 5ED";
		List<Area> foundAreas = new FindAreasMethodCall().addPostcode(postcode)
				.findAreas();
		System.out
				.printf("[postcode_nireland] Found %d areas for %s (hierarchy 27); first match: %s\n",
						foundAreas.size(), postcode, foundAreas.get(0)
								.getName());
	}

}
