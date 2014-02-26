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

public abstract class BaseMethodCall {
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

	private XmlPullParser makeParser(String instr)
			throws XmlPullParserException {
		XmlPullParserFactory xfac = XmlPullParserFactory.newInstance();
		xfac.setNamespaceAware(true); // FUCK SOAP
		XmlPullParser xpp = xfac.newPullParser();
		xpp.setInput(new StringReader(instr));
		return xpp;
	}

	private XmlPullParser doCall(String callUrl) throws IOException,
			XmlPullParserException {
		// HACK
		ContentResolver resolver = AreaActivity.getAreabaseApplicationContext()
				.getContentResolver();
		String[] selectionArgs = new String[] {
				callUrl,
				Long.toString(System.currentTimeMillis() - 30 * 24 * 60 * 60
						* 1000l) };
		Cursor c = resolver.query(CacheContentProvider.CACHE_URI,
				new String[] { "*" }, "url = ? AND retrievedOn > ?",
				selectionArgs, "retrievedOn DESC");
		/*
		 * Log.i("BaseMethodCall", String.format(
		 * "SELECT %s FROM onsCache WHERE url = \"%s\" AND retrievedOn > %s",
		 * "*", selectionArgs[0], selectionArgs[1]));
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

	private void updateCacheDb(ContentResolver resolver, String url,
			String response) {
		ContentValues values = new ContentValues();
		values.put("retrievedOn", System.currentTimeMillis());
		values.put("cachedObject", response);
		/*
		 * Tries to update existing rows, and if none exist, create a new
		 * record. That way pruning old records may not even be required.
		 */
		if (resolver.update(CacheContentProvider.CACHE_URI, values, "url = ?",
				new String[] { url }) == 0) {
			values.put("url", url);
			resolver.insert(CacheContentProvider.CACHE_URI, values);
		}
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
	 * broken for given inputs. (Looking at you, FindAreas)
	 * 
	 * @param params
	 * @return An {@link XmlPullParser} primed with the response document.
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	abstract protected XmlPullParser execute(Map<String, String> params)
			throws IOException, XmlPullParserException;

	abstract protected Map<String, String> collectParams();

	abstract public String toURLString();
}
