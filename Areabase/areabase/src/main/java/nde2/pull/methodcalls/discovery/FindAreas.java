package nde2.pull.methodcalls.discovery;
/** !license-block 
    This file is part of Areabase.

    Areabase is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Areabase is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Areabase.  If not, see <http://www.gnu.org/licenses/>.

    Areabase (C) 2013-2014 Filip Wieland <filiplamparski@gmail.com>
*/
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nde2.errors.NDE2Exception;
import nde2.pull.types.Area;

/**
 * <i>Encapsulates the FindAreas() call to the NDE2 web service.</i>
 * 
 * <p>
 * Build FindAreas requests declaratively by currying, for instance:
 * 
 * <pre>
 * new FindAreas().forPostcode(POSTCODE).ofLevelType(LEVELTYPE)
 * 		.inHierarchy(HIERARCHY);
 * </pre>
 * 
 * <p>
 * The remote method only gets called when you <code>execute()</code> it.
 * 
 * <p>
 * This will return a set of areas that contain POSTCODE, are of the LEVELTYPE
 * level in hierarchy HIERARCHY. <b>Note:</b> The remote procedure for this is
 * wonky. It may return objects that were not asked for (i.e. an Area of
 * hierarchy 2 if asked for areas of hierarchy 26).
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
	private static final String METHOD_NAME_S_BY_A_BY_POSTCODE = "SearchSByAByPostcode";

	private String postcode;
	private String areaNamePart;
	private String code;
	private int levelTypeId = -1;
	private int hierarchyId = -1;

	/**
	 * 
	 * @param postcode
	 *            Postcode of the area to find
	 * @return Modified {@link FindAreas} for currying
	 */
	public FindAreas forPostcode(String postcode) {
		this.postcode = postcode.replace(" ", "");
		return this;
	}

	/**
	 * 
	 * @param areaNamePart
	 *            Part of the required area's name
	 * @return Modified {@link FindAreas} for currying
	 */
	public FindAreas whoseNameContains(String areaNamePart) {
		this.areaNamePart = areaNamePart;
		return this;
	}

	/**
	 * 
	 * @param code
	 *            SNAC code of the area to find
	 * @return Modified {@link FindAreas} for currying
	 */
	public FindAreas forSNACCode(String code) {
		this.code = code;
		return this;
	}

	/**
	 * 
	 * @param levelTypeId
	 *            Level Type ID to filter results with -- see {@link Area} for
	 *            allowed values.
	 * @return Modified {@link FindAreas} for currying
	 */
	public FindAreas ofLevelType(int levelTypeId) {
		this.levelTypeId = levelTypeId;
		return this;
	}

	/**
	 * 
	 * @param hierarchyId
	 *            Hierarchy ID to filter results with -- see {@link Area} for
	 *            allowed values.
	 * @return Modified {@link FindAreas} for currying
	 */
	public FindAreas inHierarchy(int hierarchyId) {
		this.hierarchyId = hierarchyId;
		return this;
	}

	private boolean useAlternateMethod() {
		boolean no_name = (areaNamePart == null);
		boolean has_postcode = (postcode != null);
		boolean has_leveltype = (levelTypeId > -1);
		boolean has_hierarchy = (hierarchyId > -1);
		return (no_name && has_postcode && has_hierarchy && has_leveltype);
	}

	@Override
	protected XmlPullParser execute(Map<String, String> params)
			throws IOException, XmlPullParserException {
		/*
		 * Special case: There is a bug in the ONS service, wherein upon making
		 * a call to FindAreas(), inquiring about an area containing a postcode,
		 * of a specified level in a specified hierarchy, one receives that
		 * area, as well as another area from a hierarchy that is altogether
		 * different. Hence, in order to avoid such issues, this will check if
		 * the user wants to make that particular query, and switches to a more
		 * predictable call, SearchSByAByPostcode(). Good thing the latter
		 * returns the same datatypes. And also only the hierarchies etc that
		 * are requested.
		 * 
		 * See: http://goo.gl/ezODf8
		 */

		if (useAlternateMethod()) {
			// FindAreas is broken under this configuration, so let's use
			// something that isn't.
			return super.doCall(METHOD_NAME_S_BY_A_BY_POSTCODE, params);
		}
		return super.doCall(METHOD_NAME, params);
	}

	@Override
	protected Map<String, String> collectParams() {
		Map<String, String> params = new HashMap<String, String>();
		if (postcode != null) {
            params.put("Postcode", postcode);
        }
		if (code != null) {
            params.put("Code", code);
        }
		if (areaNamePart != null) {
            params.put("AreaNamePart", areaNamePart);
        }
		if (hierarchyId != -1) {
            params.put("HierarchyId", Integer.toString(hierarchyId));
        }
		if (levelTypeId != -1) {
            params.put("LevelTypeId", Integer.toString(levelTypeId));
        }
		return params;
	}

	/**
	 * This method will take data supplied to this {@link FindAreas} and call
	 * the web service. It will then parse the result, or thrown an Exception if
	 * there is a problem.
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

		Set<Area> areaSet = processAreaSet(xpp);
		return areaSet;
	}

	@Override
	public String toURLString() {
		if (useAlternateMethod()) {
			return buildURLString(DISCOVERY_ENDPOINT,
					METHOD_NAME_S_BY_A_BY_POSTCODE, collectParams());
		}
		return buildURLString(DISCOVERY_ENDPOINT, METHOD_NAME, collectParams());
	}

}
