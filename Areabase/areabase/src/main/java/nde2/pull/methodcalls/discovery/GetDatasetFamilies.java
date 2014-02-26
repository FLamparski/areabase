package nde2.pull.methodcalls.discovery;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nde2.errors.NDE2Exception;
import nde2.helpers.DateFormat;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.DateRange;
import nde2.pull.types.Subject;

public class GetDatasetFamilies extends DiscoveryMethodCall {
	private static final String METHOD_NAME = "GetDatasets";

	private Area area;
	private Subject subject;

	public GetDatasetFamilies(Subject subject) {
		this.subject = subject;
		this.area = null;
	}

	public GetDatasetFamilies forArea(Area area) {
		this.area = area;
		return this;
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
		if (area != null) {
			params.put("AreaId", Integer.toString(area.getAreaId()));
		}
		return params;
	}

	public List<DataSetFamily> execute() throws IOException,
			XmlPullParserException, NDE2Exception {
		XmlPullParser xpp = execute(collectParams());
		String key = null;
		String value = null;
		int event = xpp.getEventType();
		ArrayList<DataSetFamily> dataSetFamilies = new ArrayList<DataSetFamily>();
		NDE2Exception error = null;
		DataSetFamily dataSetFamily = null;
		DateRange dateRange = null;
		List<DateRange> dateRanges = null;
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				key = xpp.getName();
				if (key.equals("DSFamily")) {
					dataSetFamily = new DataSetFamily();
					dateRanges = new ArrayList<DateRange>(5);
				}
				if (key.equals("DateRange")) {
					dateRange = new DateRange();
				}
				if (key.equals("Error")) {
					error = new NDE2Exception();
				}
				break;
			case XmlPullParser.TEXT:
				value = xpp.getText();
				// DateRange
				if (key.equals("StartDate")) {
                    dateRange.setStartDate(DateFormat.fromNDEDateTime(value));
                }
				if (key.equals("EndDate")) {
                    dateRange.setEndDate(DateFormat.fromNDEDateTime(value));
                }
				// DSFamily
				if (key.equals("DSFamilyId")) {
                    dataSetFamily.setFamilyId(Integer.parseInt(value));
                }
				if (key.equals("Name")) {
                    dataSetFamily.setName(value);
                }
				// Error
				if (key.equals("message")) {
                    error.setNessMessage(value);
                }
				if (key.equals("detail")) {
                    error.setNessDetail(value);
                }
				break;
			case XmlPullParser.END_TAG:
				String t = xpp.getName();
				if (t.equals("DateRange")) {
                    dateRanges.add(dateRange);
                }
				if (t.equals("DSFamily")) {
					dataSetFamily.setDateRanges(dateRanges
							.toArray(new DateRange[dateRanges.size()]));
					dataSetFamilies.add(dataSetFamily);
				}
			}
			event = xpp.next();
		}

		if (error != null) {
            throw error;
        }
		/*
		 * Let's not waste memory. On the other hand, when this is trimmed, it
		 * could cause a performance hit depending on the GC (looking at you,
		 * Android).
		 */
		dataSetFamilies.trimToSize();
		return dataSetFamilies;
	}

	@Override
	public String toURLString() {
		return buildURLString(DISCOVERY_ENDPOINT, METHOD_NAME, collectParams());
	}
}
