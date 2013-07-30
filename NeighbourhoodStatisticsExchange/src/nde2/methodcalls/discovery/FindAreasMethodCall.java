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
 * <i>Encapsulates the FindAreas() call to the NDE2 web service. Creation
 * follows the Builder pattern.</i>
 * 
 * <p>
 * This operation is a composite search operation which combines all the
 * existing area searches. The postcode, area string and SNAC code are separate
 * parameters to avoid parsing complexities. A setting of HierarchyId= 0 tells
 * the service to use “Stats by Area” rules to pick hierarchy for level type.
 * One of postcode, area name part and (SNAC) Code must be supplied. All other
 * parameters optional.
 * 
 * <p>
 * Current area search operations:
 * <ul>
 * <li>SearchAreaByCode(code[, hierarchyId, leveltypeid])
 * <li>SearchAreaByNameHierarchy(name[, hierarchyid])
 * <li>SearchAreaByNameLevelType(name, leveltypeid)
 * <li>SearchAreaByPostcodeHierarchy(postcode[, hierarchyid])
 * <li>SearchAreaByPostcodeLevelType(postcode, leveltypeid)
 * <li>SearchSByAByName(name, leveltypeid)
 * <li>SearchSByAByPostcode(postcode, leveltypeid)
 * </ul>
 * 
 * @author filip
 * 
 */
public class FindAreasMethodCall extends BaseMethodCall {

	private static final String METHOD_NAME = "FindAreas";
	private String postcode;
	private String areaNamePart;
	private String code;
	private int levelTypeId;
	private int hierarchyId;

	public FindAreasMethodCall() {
		hierarchyId = Area.HIERARCHY_NULL;
		levelTypeId = Area.LEVELTYPE_NULL;
	}

	/**
	 * 
	 * @param postcode
	 *            Postcode of the area to find
	 * @return Modified {@link FindAreasMethodCall} for currying
	 */
	public FindAreasMethodCall addPostcode(String postcode) {
		this.postcode = postcode.replace(" ", "");
		return this;
	}

	/**
	 * 
	 * @param areaNamePart
	 *            Part of the required area's name
	 * @return Modified {@link FindAreasMethodCall} for currying
	 */
	public FindAreasMethodCall addAreaNamePart(String areaNamePart) {
		this.areaNamePart = areaNamePart;
		return this;
	}

	/**
	 * 
	 * @param code
	 *            SNAC code of the area to find
	 * @return Modified {@link FindAreasMethodCall} for currying
	 */
	public FindAreasMethodCall addCode(String code) {
		this.code = code;
		return this;
	}

	/**
	 * 
	 * @param levelTypeId
	 *            Level Type ID to filter results with -- see {@link Area} for
	 *            allowed values.
	 * @return Modified {@link FindAreasMethodCall} for currying
	 */
	public FindAreasMethodCall addLevelTypeId(int levelTypeId) {
		this.levelTypeId = levelTypeId;
		return this;
	}

	/**
	 * 
	 * @param hierarchyId
	 *            Hierarchy ID to filter results with -- see {@link Area} for
	 *            allowed values.
	 * @return Modified {@link FindAreasMethodCall} for currying
	 */
	public FindAreasMethodCall addHierarchyId(int hierarchyId) {
		this.hierarchyId = hierarchyId;
		return this;
	}

	/**
	 * This method will take data supplied to this {@link FindAreasMethodCall}
	 * and call the web service. It will then parse the result, or thrown an
	 * Exception if there is a problem.
	 * 
	 * @return A list of areas found. This can be null.
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws NDE2Exception
	 * @throws XPathExpressionException
	 *             Thrown when the XPath expressions fail to evaluate.
	 * @throws ValueNotAvailable
	 *             Thrown when there are no areas to return -- will happen in
	 *             Scotland and Northern Ireland.
	 * @see {@link BaseMethodCall} for more information about the exceptions
	 *      thrown.
	 */
	public List<Area> findAreas() throws NDE2Exception,
			ParserConfigurationException, SAXException, IOException,
			XPathExpressionException, ValueNotAvailable {
		/*
		 * First, create a Dictionary containing parameters to call the remote
		 * method with.
		 */
		Map<String, String> params = new Hashtable<String, String>();
		if (postcode != null)
			params.put("Postcode", postcode);
		if (areaNamePart != null)
			params.put("AreaNamePart", areaNamePart);
		if (code != null)
			params.put("Code", code);
		if (levelTypeId != Area.HIERARCHY_NULL)
			params.put("LevelTypeId", Integer.toString(levelTypeId));
		if (hierarchyId != Area.LEVELTYPE_NULL)
			params.put("HierarchyId", Integer.toString(hierarchyId));

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

		NodeList areaNames = (NodeList) xpath
				.evaluate(
						"//*[local-name() = 'AreaFallsWithin']/*[local-name() = 'Area']/*[local-name() = 'Name']",
						nessResponse, XPathConstants.NODESET);
		NodeList areaIds = (NodeList) xpath
				.evaluate(
						"//*[local-name() = 'AreaFallsWithin']/*[local-name() = 'Area']/*[local-name() = 'AreaId']",
						nessResponse, XPathConstants.NODESET);
		NodeList areaLevelTypeIds = (NodeList) xpath
				.evaluate(
						"//*[local-name() = 'AreaFallsWithin']/*[local-name() = 'Area']/*[local-name() = 'LevelTypeId']",
						nessResponse, XPathConstants.NODESET);
		NodeList areaHierarchyIds = (NodeList) xpath
				.evaluate(
						"//*[local-name() = 'AreaFallsWithin']/*[local-name() = 'Area']/*[local-name() = 'HierarchyId']",
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

		if (results.isEmpty())
			throw new ValueNotAvailable(
					"No areas could be found for this query.");

		return results;
	}

}
