package nde2.methodcalls.discovery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.types.discovery.Area;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <i>Encapsulates the GetAreaComparators() method call and returns the closest
 * comparator. Creation follows the Builder pattern, sort of.</i>
 * 
 * <p>
 * This operation gives you the higher level areas which contain your chosen
 * area, which are considered useful for comparison purposes. For example, the
 * comparator areas of an Output Area might be the local authority, the region
 * (if within England) and the country.
 * 
 * @author filip
 * 
 */
public class GetAreaParentMethodCall extends BaseMethodCall {
	private Area area;
	private final String METHOD_NAME = "GetAreaComparators";

	public GetAreaParentMethodCall() {
		area = null;
	}

	public GetAreaParentMethodCall addArea(Area area) {
		this.area = area;
		return this;
	}

	/**
	 * 
	 * @return A parent (comparator) area of the supplied {@link Area}. Note
	 *         that due to how NDE works, it may not be the actual
	 *         administrative parent.
	 * @throws XPathExpressionException
	 *             Thrown when the XPath expressions fail to evaluate.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws NDE2Exception
	 * @throws ValueNotAvailable
	 * @see {@link BaseMethodCall} for more information about the exceptions
	 *      thrown.
	 */
	public Area getAreaParent() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, ValueNotAvailable {
		/*
		 * First, create a Dictionary containing parameters to call the remote
		 * method with.
		 */
		Map<String, String> params = new Hashtable<String, String>();
		params.put("AreaId", Long.toString(area.getAreaId()));

		/*
		 * Second, call the remote method. Store the response in a Document.
		 * Create an XPath object as well. Note that this Document should be
		 * what is actually expected, as service errors are detected by
		 * doCall_base(), and thrown as Exceptions.
		 */
		Document nessResponse = doCall_base(METHOD_NAME, params);
		XPath xpath = XPathFactory.newInstance().newXPath();

		/*
		 * Then, find the leaf areas. Each should be the <Area> in the first
		 * <AreaFallsWithin> in the <AreaFallsWithins> list returned by NDE.
		 */
		ArrayList<Area> results = new ArrayList<Area>();

		NodeList areaNames = (NodeList) xpath.evaluate(
				"//*[local-name() = 'Area']/*[local-name() = 'Name']",
				nessResponse, XPathConstants.NODESET);
		NodeList areaIds = (NodeList) xpath.evaluate(
				"//*[local-name() = 'Area']/*[local-name() = 'AreaId']",
				nessResponse, XPathConstants.NODESET);
		NodeList areaLevelTypeIds = (NodeList) xpath.evaluate(
				"//*[local-name() = 'Area']/*[local-name() = 'LevelTypeId']",
				nessResponse, XPathConstants.NODESET);
		NodeList areaHierarchyIds = (NodeList) xpath.evaluate(
				"//*[local-name() = 'Area']/*[local-name() = 'HierarchyId']",
				nessResponse, XPathConstants.NODESET);

		for (int i = 0; i < areaNames.getLength(); i++) {

			String areaName = areaNames.item(i).getTextContent();
			long areaId = Long.parseLong(areaIds.item(i).getTextContent());
			int levelTypeId = Integer.parseInt(areaLevelTypeIds.item(i)
					.getTextContent());
			int hierarchyId = Integer.parseInt(areaHierarchyIds.item(i)
					.getTextContent());

			Area area = new Area(areaName, areaId, levelTypeId, hierarchyId);

			results.add(area);
		}

		if (!(results.isEmpty())) {
			return results.get(0);
		} else {
			throw new ValueNotAvailable(
					"There are no valid comparators for this area in the ONS database.");
		}
	}
}
