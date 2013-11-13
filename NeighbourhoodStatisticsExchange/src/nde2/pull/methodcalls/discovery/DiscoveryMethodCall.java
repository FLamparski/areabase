package nde2.pull.methodcalls.discovery;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nde2.errors.NDE2Exception;
import nde2.pull.methodcalls.BaseMethodCall;
import nde2.pull.types.Area;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class DiscoveryMethodCall extends BaseMethodCall {
	private static final String DISCOVERY_ENDPOINT = "http://neighbourhood.statistics.gov.uk/NDE2/Disco/";

	@Override
	protected XmlPullParser doCall(String method, Map<String, String> params)
			throws IOException, XmlPullParserException {
		return super.doCall(DISCOVERY_ENDPOINT, method, params);
	}

	/**
	 * So many methods return lists of areas.
	 * 
	 * <p>
	 * This method returns a {@link Set} of areas. That is because the remote
	 * call for {@link FindAreas} returns a slightly more complex structure than
	 * a simple list (as it also attempts some hierarchy in certain cases), and
	 * if a simple {@link List} was employed, areas would repeat. {@link Set}
	 * makes it impossible for equal objects to repeat, which is ideal in this
	 * scenario.
	 * 
	 * @param xpp
	 *            The {@link XmlPullParser} pointed at a method call that
	 *            returns a list of Area elements.
	 * @return A {@link Set} of {@link Area Areas}.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws NDE2Exception
	 */
	protected Set<Area> processAreaSet(XmlPullParser xpp)
			throws XmlPullParserException, IOException, NDE2Exception {
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
