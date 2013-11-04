package nde2.pull.methodcalls;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public abstract class BaseMethodCall {
	private String buildURLString(String endpoint, String method,
			Map<String, String> params) {
		/* Build a URL which for the method call */
		StringBuilder methodCallStrBuilder = new StringBuilder(endpoint);
		methodCallStrBuilder.append(method).append("?");
		Set<Entry<String, String>> paramEntries = params.entrySet();
		for (Entry<String, String> param : paramEntries) {

			methodCallStrBuilder
					.append(param.getKey() + "=" + param.getValue())
					.append("&");
		}

		/*
		 * Gets rid of the trailing & from the URLs
		 */
		String callUrlStr = methodCallStrBuilder.toString();
		callUrlStr = callUrlStr.substring(0, callUrlStr.length() - 1);
		return callUrlStr;
	}

	private InputStream sendRequest(String callUrlStr) throws IOException {
		URL callUrl = new URL(callUrlStr);
		HttpURLConnection callConnection = (HttpURLConnection) callUrl
				.openConnection();
		// The ten-second rule:
		// If there's no data in 10s, assume the worst.
		callConnection.setReadTimeout(10000);
		InputStream is = callConnection.getInputStream();
		return is;
	}

	private XmlPullParser makeParser(InputStream istream)
			throws XmlPullParserException {
		XmlPullParserFactory xfac = XmlPullParserFactory.newInstance();
		xfac.setNamespaceAware(true); // FUCK SOAP
		XmlPullParser xpp = xfac.newPullParser();
		xpp.setInput(istream, null);
		return xpp;
	}

	private XmlPullParser doCall(String callUrl) throws IOException,
			XmlPullParserException {
		System.out.printf("Calling: %s\n", callUrl);
		return makeParser(sendRequest(callUrl));
	}

	protected XmlPullParser doCall(String endpoint, String method,
			Map<String, String> params) throws IOException,
			XmlPullParserException {
		return doCall(buildURLString(endpoint, method, params));
	}

	abstract protected XmlPullParser doCall(String methid,
			Map<String, String> params) throws IOException,
			XmlPullParserException;

	/**
	 * Defines the execution plan of a call. Usually, this means plugging in a
	 * remote method name and the params into the other doCall(), however it can
	 * be used for anything, such as checking some parameters and then deciding
	 * to use a different call altogether, because the other one is utterly
	 * broken for given inputs.
	 * 
	 * @param params
	 * @return An {@link XmlPullParser} primed with the response document.
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	abstract protected XmlPullParser execute(Map<String, String> params)
			throws IOException, XmlPullParserException;

	abstract protected Map<String, String> collectParams();
}
