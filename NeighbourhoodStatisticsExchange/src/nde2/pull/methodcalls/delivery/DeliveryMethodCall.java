package nde2.pull.methodcalls.delivery;

import java.io.IOException;
import java.util.Map;

import nde2.pull.methodcalls.BaseMethodCall;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class DeliveryMethodCall extends BaseMethodCall {
	private static final String DELIVERY_ENDPOINT = "http://neighbourhood.statistics.gov.uk/NDE2/Deli/";

	@Override
	protected XmlPullParser doCall(String method, Map<String, String> params)
			throws IOException, XmlPullParserException {
		return super.doCall(DELIVERY_ENDPOINT, method, params);
	}
}
