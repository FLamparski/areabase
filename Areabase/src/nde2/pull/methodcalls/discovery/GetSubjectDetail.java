package nde2.pull.methodcalls.discovery;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nde2.errors.NDE2Exception;
import nde2.pull.types.DetailedSubject;
import nde2.pull.types.Subject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

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
				if (key.equals("SubjectDetail"))
					dsubject = new DetailedSubject(subject, null, null);
				else if (key.equals("Error"))
					error = new NDE2Exception();
				break;
			case XmlPullParser.TEXT:
				value = xpp.getText();
				if (key.equals("Description"))
					dsubject.setDescription(value);
				if (key.equals("OptionalMetaData"))
					dsubject.setMoreDescription(value);
				if (key.equals("message"))
					error.setNessMessage(value);
				if (key.equals("detail"))
					error.setNessDetail(value);
				break;
			}
			event = xpp.next();
		}

		if (error != null)
			throw error;
		return dsubject;
	}

	@Override
	public String toURLString() {
		return buildURLString(DISCOVERY_ENDPOINT, METHOD_NAME, collectParams());
	}
}
