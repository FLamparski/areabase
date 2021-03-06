package police.methodcalls;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import police.errors.APIException;

/**
 * Base class from which all MethodCalls inherit. Contains
 * {@link BaseMethodCall#doCall(String, Map)}, which encodes the request into an
 * URL and fetches the JSON response from the Police API.
 * 
 * @author filip
 * 
 */
public abstract class BaseMethodCall {
	protected final static String ENDPOINT = "http://data.police.uk/api/";
	protected final static int TIMEOUT = 10000;

	/**
	 * Gets the response from the API
	 * 
	 * @param method
	 *            The method to call (URL after /api/)
	 * @param params
	 *            A map containing the GET parameters (query for the API)
	 * @return A String, which containst the JSON response.
	 * @throws SocketTimeoutException
	 *             Thrown if the time spent waiting for the response exceeds
	 *             TIMEOUT (10s).
	 * @throws IOException
	 *             Thrown if the connection fails
	 * @throws APIException
	 *             Thrown if the server returns a non-200 HTTP code.
	 */
	protected String doCall(String method, Map<String, String> params)
			throws SocketTimeoutException, IOException, APIException {
		StringBuilder paramsBuilder = new StringBuilder(ENDPOINT)
				.append(method);
		if (params != null) {
			paramsBuilder.append("?");
			Set<Entry<String, String>> paramsSet = params.entrySet();
			for (Entry<String, String> param : paramsSet) {
				paramsBuilder.append(param.getKey()).append("=")
						.append(param.getValue()).append("&");
			}
		}
		/*
		 * Remove the trailing &
		 */
		String paramString = paramsBuilder.toString();
		if (paramString.endsWith("&")) {
			paramString = paramString.substring(0, paramString.length() - 1);
		}

		// Uncomment for testing:
		System.out.println("Calling " + paramString);

		URL callUrl = new URL(paramString);
		HttpURLConnection callConnection = (HttpURLConnection) callUrl
				.openConnection();
		// The ten-second rule:
		// If there's no data in 10s (or TIMEOUT), assume the worst.
		callConnection.setReadTimeout(TIMEOUT);
		// Set the request method to GET.
		callConnection.setRequestMethod("GET");
		int code = callConnection.getResponseCode();
		if (code != HttpURLConnection.HTTP_OK) {
			throw new APIException("A non-200 code was returned", code);
		}

		String responseStr;
		responseStr = IOUtils.toString(callConnection.getInputStream());
		callConnection.disconnect();

		return responseStr;
	}
}
