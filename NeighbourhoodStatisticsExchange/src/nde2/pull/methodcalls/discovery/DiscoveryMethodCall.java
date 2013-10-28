package nde2.pull.methodcalls.discovery;

import java.io.IOException;
import java.util.Map;

import nde2.pull.methodcalls.BaseMethodCall;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class DiscoveryMethodCall extends BaseMethodCall {
	private static final String DISCOVERY_ENDPOINT = "http://neighbourhood.statistics.gov.uk/NDE2/Disco/";

	@Override
	protected XmlPullParser doCall(String method, Map<String, String> params)
			throws IOException, XmlPullParserException {
		return super.doCall(DISCOVERY_ENDPOINT, method, params);
	}

}
