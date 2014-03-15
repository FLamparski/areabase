package nde2.pull.methodcalls.delivery;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Map;

import nde2.pull.methodcalls.BaseMethodCall;

/**
 * A base class from which all Delivery method calls inherit.
 */
public abstract class DeliveryMethodCall extends BaseMethodCall {
	protected static final String DELIVERY_ENDPOINT = "http://neighbourhood.statistics.gov.uk/NDE2/Deli/";

    /**
     * Fills in the Endpoint of {@link nde2.pull.methodcalls.BaseMethodCall#doCall(String, String, java.util.Map)}.
     * @param method which method to call
     * @param params the parameters
     * @return an {@link org.xmlpull.v1.XmlPullParser}.
     * @throws IOException
     * @throws XmlPullParserException
     */
	@Override
	protected XmlPullParser doCall(String method, Map<String, String> params)
			throws IOException, XmlPullParserException {
		return super.doCall(DELIVERY_ENDPOINT, method, params);
	}
}
