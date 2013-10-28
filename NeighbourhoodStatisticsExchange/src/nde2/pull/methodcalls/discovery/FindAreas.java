package nde2.pull.methodcalls.discovery;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nde2.errors.NDE2Exception;
import nde2.methodcalls.discovery.FindAreasMethodCall;
import nde2.pull.types.Area;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * <i>Encapsulates the FindAreas() call to the NDE2 web service. Creation
 * follows the Builder pattern. This uses pull parsing.</i>
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
public class FindAreas extends DiscoveryMethodCall {
	private static final String METHOD_NAME = "FindAreas";
	private String postcode;
	private String areaNamePart;
	private String code;
	private int levelTypeId = -1;
	private int hierarchyId = -1;

	/**
	 * 
	 * @param postcode
	 *            Postcode of the area to find
	 * @return Modified {@link FindAreasMethodCall} for currying
	 */
	public FindAreas addPostcode(String postcode) {
		this.postcode = postcode.replace(" ", "");
		return this;
	}

	/**
	 * 
	 * @param areaNamePart
	 *            Part of the required area's name
	 * @return Modified {@link FindAreasMethodCall} for currying
	 */
	public FindAreas addAreaNamePart(String areaNamePart) {
		this.areaNamePart = areaNamePart;
		return this;
	}

	/**
	 * 
	 * @param code
	 *            SNAC code of the area to find
	 * @return Modified {@link FindAreasMethodCall} for currying
	 */
	public FindAreas addCode(String code) {
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
	public FindAreas addLevelTypeId(int levelTypeId) {
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
	public FindAreas addHierarchyId(int hierarchyId) {
		this.hierarchyId = hierarchyId;
		return this;
	}

	@Override
	protected XmlPullParser execute(Map<String, String> params)
			throws IOException, XmlPullParserException {
		return super.doCall(METHOD_NAME, params);
	}

	@Override
	protected Map<String, String> collectParams() {
		Map<String, String> params = new HashMap<String, String>();
		if (postcode != null)
			params.put("Postcode", postcode);
		if (code != null)
			params.put("Code", code);
		if (areaNamePart != null)
			params.put("AreaNamePart", areaNamePart);
		if (hierarchyId != -1)
			params.put("HierarchyId", Integer.toString(hierarchyId));
		if (levelTypeId != -1)
			params.put("LevelTypeId", Integer.toString(levelTypeId));
		return params;
	}

	/**
	 * This method will take data supplied to this {@link FindAreasMethodCall}
	 * and call the web service. It will then parse the result, or thrown an
	 * Exception if there is a problem.
	 * 
	 * @return A set of areas returned by the service.
	 * @throws IOException
	 *             Usually occurs when a connection could not be established, or
	 *             the URL called is wrong for some reason.
	 * @throws XmlPullParserException
	 *             May occur if the server returns some WTF-worthy XML.
	 * @throws NDE2Exception
	 *             Encapsulates an error response returned by the service
	 *             itself.
	 */
	public Set<Area> execute() throws IOException, XmlPullParserException,
			NDE2Exception {
		XmlPullParser xpp = execute(collectParams());

		String key = null;
		String value = null;
		Set<Area> areaSet = new HashSet<Area>();
		Area area = null;
		NDE2Exception error = null;
		int event = xpp.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				key = xpp.getName();
				if (key.equals("Error"))
					error = new NDE2Exception();
				if (key.equals("Area"))
					area = new Area();
				break;
			case XmlPullParser.TEXT:
				value = xpp.getText();
				if (key.equals("AreaId"))
					area.setAreaId(Integer.parseInt(value));
				if (key.equals("Name"))
					area.setName(value);
				if (key.equals("HierarchyId"))
					area.setHierarchyId(Integer.parseInt(value));
				if (key.equals("LevelTypeId"))
					area.setLevelTypeId(Integer.parseInt(value));
				if (key.equals("message"))
					error.setNessMessage(value);
				if (key.equals("detail"))
					error.setNessDetail(value);
				break;
			case XmlPullParser.END_TAG:
				if (xpp.getName().equals("Area"))
					areaSet.add(area);
				break;
			}
			event = xpp.next();
		}

		if (error != null)
			throw error;
		return areaSet;
	}
}
