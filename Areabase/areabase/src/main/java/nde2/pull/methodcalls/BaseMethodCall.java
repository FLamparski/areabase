package nde2.pull.methodcalls;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lamparski.areabase.AreaActivity;
import lamparski.areabase.CacheContentProvider;

/**
 * Contains the most basic methods to fetch XML response documents from ONS and wrap them inside
 * of a parser object. Also handles caching.
 *
 * <p>
 *     Note that I said XML. It is actually a SOAP response document, but the way I'm using it
 *     later in the data flow, it may as well be regular XML. SOAP doesn't make much sense.
 * </p>
 *
 * @author filip
 */
public abstract class BaseMethodCall {
    /**
     * URL-encodes the parameters
     * @param endpoint the endpoint to call
     * @param method the method to call
     * @param params a dictionary of parameter names and their values
     * @return an URL string to call the given method on the given endpoint with the given parameters.
     */
	protected String buildURLString(String endpoint, String method,
			Map<String, String> params) {
		/* Build a URL which for the method call */
		StringBuilder methodCallStrBuilder = new StringBuilder(endpoint);
		methodCallStrBuilder.append(method).append("?");
		Set<Entry<String, String>> paramEntries = params.entrySet();
		for (Entry<String, String> param : paramEntries) {

			methodCallStrBuilder
                    .append(param.getKey())
                    .append("=")
                    .append(param.getValue())
					.append("&");
		}

		/*
		 * Gets rid of the trailing & from the URLs
		 */
		String callUrlStr = methodCallStrBuilder.toString();
		callUrlStr = callUrlStr.substring(0, callUrlStr.length() - 1);
		return callUrlStr;
	}

    /**
     * Performs a GET request on the given url and downloads the response into a String.
     * @param callUrlStr the call url
     * @return a String containing the response document.
     * @throws IOException
     */
	private String sendRequest(String callUrlStr) throws IOException {
		URL callUrl = new URL(callUrlStr);
		HttpURLConnection callConnection = (HttpURLConnection) callUrl
				.openConnection();
		// The ten-second rule:
		// If there's no data in 10s, assume the worst.
		callConnection.setReadTimeout(10000);
		InputStream is = callConnection.getInputStream();
		return IOUtils.toString(is);
	}

    /**
     * Creates an {@link org.xmlpull.v1.XmlPullParser} for the instr.
     * @param instr A String containing the XML document to parse.
     * @return A streaming parser that works with the XML document.
     * @throws XmlPullParserException
     */
	private XmlPullParser makeParser(String instr)
			throws XmlPullParserException {
		XmlPullParserFactory xfac = XmlPullParserFactory.newInstance();
		xfac.setNamespaceAware(true); // Because SOAP loves namespaces.
		XmlPullParser xpp = xfac.newPullParser();
		xpp.setInput(new StringReader(instr));
		return xpp;
	}

    /**
     * This method:
     *
     * <p>1. Takes the call url and sees if there is a cache entry for it</p>
     *
     * <p>2.1. If there is, load it</p>
     *
     * <p>2.2. If there isn't, fetch the data from ONS and save to cache</p>
     *
     * <p>3. Creates an {@link org.xmlpull.v1.XmlPullParser} for the result</p>
     *
     * @param callUrl the URL to get data from
     * @return an {@link org.xmlpull.v1.XmlPullParser} for the data
     * @throws IOException
     * @throws XmlPullParserException
     */
	private XmlPullParser doCall(String callUrl) throws IOException,
			XmlPullParserException {
		// HACK
		ContentResolver resolver = AreaActivity.getAreabaseApplicationContext()
				.getContentResolver();
		String[] selectionArgs = new String[] {
				callUrl,
				Long.toString(System.currentTimeMillis() - 30 * 24 * 60 * 60
						* 1000l) };
		Cursor c = resolver.query(CacheContentProvider.ONS_CACHE_URI,
				new String[] { "*" }, "url = ? AND retrievedOn > ?",
				selectionArgs, "retrievedOn DESC");
		/*
		 Equivalent to the following SQL:
		 SELECT * FROM onsCache
		 WHERE url = @url AND retrievedOn > @30_days_ago
		 ORDER BY retrievedOn DESC
		 */
        if (c == null){
            Log.wtf("BaseMethodCall", "Cannot get a database cursor!");
            return null;
        }
		if (c.moveToFirst()) {
			Log.d("BaseMethodCall", String.format(
                    "A cached instance of %s is available, returning.\n",
                    callUrl));
			String response = c.getString(c.getColumnIndex("cachedObject"));
			c.close();
			return makeParser(response);
		} else {
			Log.d("BaseMethodCall", String.format("Calling: %s\n", callUrl));
			String response = sendRequest(callUrl);
			if (response.contains("<Error>")) {
				c.close();
				return makeParser(response);
			} else {
				c.close();
				updateCacheDb(resolver, callUrl, response);
				return makeParser(response);
			}
		}
	}

    /**
     * Saves the data to the database
     * @param resolver the content resolver to use
     * @param url request url to save
     * @param response the response at the time
     */
	private void updateCacheDb(ContentResolver resolver, String url,
			String response) {
		ContentValues values = new ContentValues();
		values.put("retrievedOn", System.currentTimeMillis());
		values.put("cachedObject", response);
		/*
		 * Tries to update existing rows, and if none exist, create a new
		 * record. That way pruning old records may not even be required.
		 */
		if (resolver.update(CacheContentProvider.ONS_CACHE_URI, values, "url = ?",
				new String[] { url }) == 0) {
			values.put("url", url);
			resolver.insert(CacheContentProvider.ONS_CACHE_URI, values);
		}
	}

	protected XmlPullParser doCall(String endpoint, String method,
			Map<String, String> params) throws IOException,
			XmlPullParserException {
		return doCall(buildURLString(endpoint, method, params));
	}

	abstract protected XmlPullParser doCall(String method,
			Map<String, String> params) throws IOException,
			XmlPullParserException;

	/**
	 * Defines the execution plan of a call. Usually, this means plugging in a
	 * remote method name and the params into the other doCall(), however it can
	 * be used for anything, such as checking some parameters and then deciding
	 * to use a different call altogether, because the other one is utterly
	 * broken for given inputs. (Looking at you, FindAreas)
	 * 
	 * @param params a dictionary of parameter names and their values
	 * @return An {@link XmlPullParser} primed with the response document.
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	abstract protected XmlPullParser execute(Map<String, String> params)
			throws IOException, XmlPullParserException;

    /**
     * Children objects will use this method to collect their parameters
     * for the call.
     * @return A map of the parameters.
     */
	abstract protected Map<String, String> collectParams();

	abstract public String toURLString();
}
