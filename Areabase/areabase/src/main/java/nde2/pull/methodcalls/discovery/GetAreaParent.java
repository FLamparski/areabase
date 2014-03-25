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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nde2.errors.NDE2Exception;
import nde2.pull.types.Area;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * <i>Encapsulates the GetAreaComparators() method call and returns the closest
 * comparator.</i>
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
public class GetAreaParent extends DiscoveryMethodCall {
	private final String METHOD_NAME = "GetAreaComparators";

	private Area childArea;

	public GetAreaParent(Area childArea) {
		this.childArea = childArea;
	}

	@Override
	protected XmlPullParser execute(Map<String, String> params)
			throws IOException, XmlPullParserException {
		return doCall(METHOD_NAME, params);
	}

	@Override
	protected Map<String, String> collectParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("AreaId", Integer.toString(childArea.getAreaId()));
		return params;
	}

	/**
	 * 
	 * @return A parent (comparator) area of the supplied {@link Area}. Note
	 *         that due to how NDE works, it may not be the actual
	 *         administrative parent.
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws NDE2Exception
	 */
	public Area execute() throws IOException, XmlPullParserException,
			NDE2Exception {
		XmlPullParser xpp = execute(collectParams());
		Set<Area> comparators = processAreaSet(xpp);
		Area parentCandidate = null;

		for (Area comparator : comparators) {
			if (parentCandidate != null) {
				if (comparator.getLevelTypeId() > parentCandidate
						.getLevelTypeId()) {
					parentCandidate = comparator;
				}
			} else {
				parentCandidate = comparator;
			}
		}

		return parentCandidate;
	}

	@Override
	public String toURLString() {
		return buildURLString(DISCOVERY_ENDPOINT, METHOD_NAME, collectParams());
	}
}
