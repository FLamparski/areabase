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

import nde2.errors.NDE2Exception;
import nde2.pull.types.Area;
import nde2.pull.types.DetailedArea;

/**
 * <i>Encapsulates the GetAreaDetail() call to the NDE2 web service. Creation
 * follows the Builder pattern.</i>
 * 
 * <p>
 * This is a simple operation which gives you extra information about an area,
 * by supplying its AreaId. This AreaId is internal to the NeSS datastore and
 * must have been previously obtained via other discovery calls.
 * <p>
 * In the returned information, the Envelope can be used in a GIS system – it is
 * the four corners of the map of the area MinX:MinY:MaxX:MaxY - these numbers
 * being Ordnance Survey Eastings and Northings. The ExtCode is the standard
 * SNAC code for the area. There may be metadata for the area or nil elements.
 * 
 * @author filip
 * 
 */
public class GetAreaDetail extends DiscoveryMethodCall {
	private static final String METHOD_NAME = "GetAreaDetail";

	private Area basicArea;

	public GetAreaDetail(Area basicArea) {
		this.basicArea = basicArea;
	}

	@Override
	protected XmlPullParser execute(Map<String, String> params)
			throws IOException, XmlPullParserException {
		return doCall(METHOD_NAME, params);
	}

	@Override
	protected Map<String, String> collectParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("AreaId", Integer.toString(basicArea.getAreaId()));
		return params;
	}

	/**
	 * A {@link DetailedArea} corresponding to the supplied Area.
	 * 
	 * @return the detailed area
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws NDE2Exception
	 */
	public DetailedArea execute() throws IOException, XmlPullParserException,
			NDE2Exception {
		XmlPullParser xpp = execute(collectParams());
		DetailedArea detailedArea = null;

		String key = null;
		String value = null;
		NDE2Exception error = null;
		int event = xpp.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				key = xpp.getName();
				if (key.equals("Error")) {
                    error = new NDE2Exception();
                }
				if (key.equals("AreaDetail")) {
                    detailedArea = new DetailedArea(basicArea, null, null,
                            null, null);
                }
				break;
			case XmlPullParser.TEXT:
				value = xpp.getText();
				if (key.equals("ExtCode")) {
                    detailedArea.setExtCode(value);
                }
				if (key.equals("OptionalMetaData")) {
                    detailedArea.setOptionalMetadata(value);
                }
				if (key.equals("MandatoryMetaData")) {
                    detailedArea.setMandatoryMetadata(value);
                }
				if (key.equals("Envelope")) {
                    detailedArea.setEnvelope(value);
                }
				if (key.equals("message")) {
                    error.setNessMessage(value);
                }
				if (key.equals("detail")) {
                    error.setNessDetail(value);
                }
				break;
			}
			event = xpp.next();
		}

		if (error != null) {
            throw error;
        }

		return detailedArea;
	}

	@Override
	public String toURLString() {
		return buildURLString(DISCOVERY_ENDPOINT, METHOD_NAME, collectParams());
	}
}
