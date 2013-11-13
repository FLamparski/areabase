package nde2.methodcalls.discovery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
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
 * <i>Encapsulates the GetAreaChildren() call to the NDE2 web service. Creation
 * follows the Builder pattern.</i>
 * 
 * <p>
 * This operation allows you to get the lower level areas for a supplied area,
 * regardless of level type. Only those areas immediately below the supplied
 * area are returned. For example, the child areas of a region would be the
 * counties and unitary authorities within that region. Repeatedly calling this
 * operation allows you drill down through area hierarchies.
 * 
 * @author filip
 * 
 */
@Deprecated
public class GetAreaChildrenMethodCall extends BaseMethodCall {

	private static final String METHOD_NAME = "GetAreaChildren";

	private Area parentArea;

	public GetAreaChildrenMethodCall() {
		parentArea = null;
	}

	/**
	 * 
	 * @param area
	 *            {@link Area} to find children for
	 * @return A modified {@link GetAreaChildrenMethodCall} for currying.
	 */
	public GetAreaChildrenMethodCall addArea(Area area) {
		this.parentArea = area;
		return this;
	}

	/**
	 * This method will take data supplied to this
	 * {@link GetAreaChildrenMethodCall} and call the web service. It will then
	 * parse the result, or thrown an Exception if there is a problem.
	 * 
	 * @return A list of children {@link Area}s for the parent {@link Area}
	 *         supplied.
	 * @throws XPathExpressionException
	 *             Thrown when the XPath expressions used fail to evaluate.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws NDE2Exception
	 * @throws ValueNotAvailable
	 * @see {@link BaseMethodCall} for more information about the exceptions
	 *      thrown.
	 */
	public List<Area> getAreaChildren() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, ValueNotAvailable {
		/*
		 * First, create a Dictionary containing parameters to call the remote
		 * method with.
		 */
		Map<String, String> params = new Hashtable<String, String>();
		params.put("AreaId", Long.toString(parentArea.getAreaId()));

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
		NodeList areaNames = (NodeList) xpath
				.evaluate(
						"//*[local-name() = 'Areas']/*[local-name() = 'Area']/*[local-name() = 'Name']",
						nessResponse, XPathConstants.NODESET);
		NodeList areaIds = (NodeList) xpath
				.evaluate(
						"//*[local-name() = 'Areas']/*[local-name() = 'Area']/*[local-name() = 'AreaId']",
						nessResponse, XPathConstants.NODESET);
		NodeList areaLevelTypeIds = (NodeList) xpath
				.evaluate(
						"//*[local-name() = 'Areas']/*[local-name() = 'Area']/*[local-name() = 'LevelTypeId']",
						nessResponse, XPathConstants.NODESET);
		NodeList areaHierarchyIds = (NodeList) xpath
				.evaluate(
						"//*[local-name() = 'Areas']/*[local-name() = 'Area']/*[local-name() = 'HierarchyId']",
						nessResponse, XPathConstants.NODESET);
		ArrayList<Area> children = new ArrayList<Area>();
		for (int i = 0; i < areaNames.getLength(); i++) {

			String areaName = areaNames.item(i).getTextContent();
			long areaId = Long.parseLong(areaIds.item(i).getTextContent());
			int levelTypeId = Integer.parseInt(areaLevelTypeIds.item(i)
					.getTextContent());
			int hierarchyId = Integer.parseInt(areaHierarchyIds.item(i)
					.getTextContent());

			Area area = new Area(areaName, areaId, levelTypeId, hierarchyId);
			area.setParent(parentArea);
			children.add(area);
		}
		if (!(children.isEmpty()))
			return children;
		else
			throw new ValueNotAvailable("This area has no children.");
	}

}
