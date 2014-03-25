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
import nde2.pull.types.DetailedSubject;
import nde2.pull.types.Subject;

/**
 * <i>Encapsulates the GetSubjectDetail() call to the NDE2 web service.</i>
 * 
 * <p>
 * This operation allows you to obtain the metadata associated with your
 * requested subject. Dataset families are not returned.
 * 
 * @author filip
 * 
 */
public class GetSubjectDetail extends DiscoveryMethodCall {
	private static final String METHOD_NAME = "GetSubjectDetail";

	private Subject subject;

	public GetSubjectDetail(Subject subject) {
		this.subject = subject;
	}

	@Override
	protected XmlPullParser execute(Map<String, String> params)
			throws IOException, XmlPullParserException {
		return doCall(METHOD_NAME, params);
	}

	@Override
	protected Map<String, String> collectParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("SubjectId", Integer.toString(subject.getId()));
		return params;
	}

	/**
	 * 
	 * @return A detailed representation of the supplied {@link Subject}
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws NDE2Exception
	 */
	public DetailedSubject execute() throws IOException,
			XmlPullParserException, NDE2Exception {
		XmlPullParser xpp = execute(collectParams());
		String key = null;
		String value = null;
		int event = xpp.getEventType();
		NDE2Exception error = null;
		DetailedSubject dsubject = null;
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				key = xpp.getName();
				if (key.equals("SubjectDetail")) {
                    dsubject = new DetailedSubject(subject, null, null);
                } else if (key.equals("Error")) {
                    error = new NDE2Exception();
                }
				break;
			case XmlPullParser.TEXT:
				value = xpp.getText();
				if (key.equals("Description")) {
                    dsubject.setDescription(value);
                }
				if (key.equals("OptionalMetaData")) {
                    dsubject.setMoreDescription(value);
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
		return dsubject;
	}

	@Override
	public String toURLString() {
		return buildURLString(DISCOVERY_ENDPOINT, METHOD_NAME, collectParams());
	}
}
