package police.methodcalls;
/** !license-block 
    This file is part of Areabase.

    Areabase is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Areabase is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Areabase.  If not, see <http://www.gnu.org/licenses/>.

    Areabase (C) 2013-2014 Filip Wieland <filiplamparski@gmail.com>
*/
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import lamparski.areabase.AreaActivity;
import lamparski.areabase.CacheContentProvider;
import lamparski.areabase.CacheDbOpenHelper;
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
     * This method will actually pull new data from the web service
     * @param url the url to fetch data from
     * @return response returned by the server -- a JSON document
     * @throws Exception
     */
    private String doCallToAPI(String url) throws Exception{
        URL callUrl = new URL(url);
        HttpURLConnection callConnection = (HttpURLConnection) callUrl
                .openConnection();
        // The ten-second rule:
        // If there's no data in 5s (or TIMEOUT), assume the worst.
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

	/**
	 * Gets the response from the API or the database.
	 * 
	 * @param method
	 *            The method to call (URL after /api/)
	 * @param params
	 *            A map containing the GET parameters (query for the API)
	 * @return A String, which contains the JSON response.
	 */
	protected String doCall(String method, Map<String, String> params)
            throws Exception {
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

        Log.d("BaseMethodCall", "Calling " + paramString);

		String responseStr = doCallToDB(paramString);
        if(responseStr == null){
            responseStr = doCallToAPI(paramString);

            ContentResolver contentResolver = AreaActivity
                    .getAreabaseApplicationContext().getContentResolver();
            ContentValues cacheValues = new ContentValues();

            cacheValues.put(CacheDbOpenHelper.BaseCacheTable.FIELD_RETRIEVED_ON, System.currentTimeMillis());
            cacheValues.put(CacheDbOpenHelper.BaseCacheTable.FIELD_CACHED_OBJECT, responseStr);
            // Update existing entry...
            int rowsUpdated = contentResolver.update(CacheContentProvider.POLICE_CACHE_URI, cacheValues, "url = ?", new String[] {paramString});
            // ...or create one.
            if(rowsUpdated == 0){
                cacheValues.put(CacheDbOpenHelper.BaseCacheTable.FIELD_URL, paramString);
                contentResolver.insert(CacheContentProvider.POLICE_CACHE_URI, cacheValues);
            }
        }

		return responseStr;
	}

    /**
     * Tries to fetch a cached instance of the crime dataset
     * @param url call url to check data for
     * @return a cached JSON document, or null if there isn't any in the database
     */
    private @Nullable String doCallToDB(String url) {
        ContentResolver contentResolver = AreaActivity
                .getAreabaseApplicationContext().getContentResolver();

        String[] selectionArgs = { url,
                Long.toString(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000l) };
        /*
        The query will be equivalent to the following SQL:
        SELECT * FROM policeCache
        WHERE url = @url AND retrievedOn > @30_days_ago
        ORDER BY retrievedOn DESC
         */
        Cursor c = contentResolver.query(CacheContentProvider.POLICE_CACHE_URI, new String[]{ "*" }, "url = ? AND retrievedOn > ?",
                selectionArgs, "retrievedOn DESC");

        if(c == null) { return null; }

        String response;
        if(c.moveToFirst()){
            Log.d("BaseMethodCall", String.format(
                    "A cached instance of %s is available, returning.\n",
                    url));
            response = c.getString(c.getColumnIndex("cachedObject"));
        } else {
            response = null;
        }
        c.close();

        return response;
    }
}
