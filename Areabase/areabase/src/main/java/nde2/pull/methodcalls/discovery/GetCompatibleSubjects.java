package nde2.pull.methodcalls.discovery;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nde2.errors.NDE2Exception;
import nde2.pull.types.Area;
import nde2.pull.types.Subject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * <i>Encapsulates the GetCompatibleSubjects() call to the NDE2 web service.</i>
 * <p>
 * This operation retrieves the available subjects, without dataset families.
 * With each subject, however, a count is returned of dataset families that are
 * compatible with the supplied area.
 * 
 * @author filip
 * 
 */
public class GetCompatibleSubjects extends DiscoveryMethodCall {
	private static final String METHOD_NAME = "GetCompatibleSubjects";
	private Area area;

	public GetCompatibleSubjects(Area area) {
		this.area = area;
	}

	@Override
	protected XmlPullParser execute(Map<String, String> params)
			throws IOException, XmlPullParserException {
		return super.doCall(METHOD_NAME, params);
	}

	@Override
	protected Map<String, String> collectParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("AreaId", Integer.toString(area.getAreaId()));
		return params;
	}

	public Map<Subject, Integer> execute() throws IOException,
			XmlPullParserException, NDE2Exception {
		XmlPullParser xpp = execute(collectParams());
		Map<Subject, Integer> subjects = new HashMap<Subject, Integer>();

		String key = null;
		String value = null;
		Subject subject = null;
		int count = 0;
		NDE2Exception error = null;
		int event = xpp.getEventType();

		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				key = xpp.getName();
				if (key.equals("Error"))
					error = new NDE2Exception();
				if (key.equals("Subject"))
					subject = new Subject();
				break;
			case XmlPullParser.TEXT:
				value = xpp.getText();
				if (key.equals("SubjectId"))
					subject.setId(Integer.parseInt(value));
				if (key.equals("Name"))
					subject.setName(value);
				if (key.equals("Count"))
					count = Integer.parseInt(value);
				if (key.equals("message"))
					error.setNessMessage(value);
				if (key.equals("detail"))
					error.setNessDetail(value);
				break;
			case XmlPullParser.END_TAG:
				if (xpp.getName().equals("SubjectWithCount")) {
					subjects.put(subject, count);
				}
				break;
			}
			event = xpp.next();
		}
		if (error != null)
			throw error;
		return subjects;
	}

	@Override
	public String toURLString() {
		return buildURLString(DISCOVERY_ENDPOINT, METHOD_NAME, collectParams());
	}
}
