package org.mysociety.mapit;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.annotation.Nullable;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import lamparski.areabase.AreaActivity;
import lamparski.areabase.CacheContentProvider;
import lamparski.areabase.CacheDbOpenHelper;
import nde2.errors.NDE2Exception;
import nde2.pull.methodcalls.discovery.GetAreaDetail;

/**
 * MapIt is a service that maps UK postcodes and geographical points to administrative areas.
 * It’s useful for anyone who has a postcode or co-ordinates of a point in the UK,
 * and needs to find out what region, constituency, or council it lies within. It’s also great for
 * looking up the shapes of all those boundaries.
 *
 * <p>Areabase uses Mapit to get the boundary polygons (shapes) of the areas examined.</p>
 *
 * <p><a href="http://mapit.mysociety.org/">See original docs</a></p>
 */
public class Mapper {

	private final static String CALL = "http://mapit.mysociety.org/area/%s.geojson";

    /**
     * Tries to get the geometry from database, and returns null if it can't.
     * @param url The query url
     * @return a JsonElement that holds the geometry, or null if there's no cache entry for it
     * @throws Exception
     */
    private static @Nullable JsonElement getJsonFromDatabase(String url) throws Exception {
        ContentResolver contentResolver = AreaActivity
                .getAreabaseApplicationContext().getContentResolver();

        String[] selectionArgs = { url,
                Long.toString(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000l) };
        Cursor c = contentResolver.query(CacheContentProvider.MAPIT_CACHE_URI, new String[]{ "*" }, "url = ? AND retrievedOn > ?",
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
        Log.d("Mapper", "A cached instance of " + url + " is available, returning.");

        return response != null ? new JsonParser().parse(response) : null;
    }

    /**
     * Actually get this from Mapit
     * @param url the url to get
     * @return a JsonElement that holds the geometry
     * @throws Exception
     */
    private static JsonElement getJsonFromMapit(String url) throws Exception {
        // Uncomment for testing:
        Log.d("Mapper", "Calling " + url);

        URL callUrl = new URL(url);
        HttpURLConnection callConnection = (HttpURLConnection) callUrl
                .openConnection();
        // The ten-second rule:
        // If there's no data in 10s (or TIMEOUT), assume the worst.
        callConnection.setReadTimeout(10000);
        // Set the request method to GET.
        callConnection.setRequestMethod("GET");
        int code = callConnection.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new Exception("A non-200 code was returned: " + code);
        }

        String responseStr;
        responseStr = IOUtils.toString(callConnection.getInputStream());
        callConnection.disconnect();

        ContentResolver contentResolver = AreaActivity
                .getAreabaseApplicationContext().getContentResolver();
        ContentValues cacheValues = new ContentValues();
        cacheValues.put(CacheDbOpenHelper.BaseCacheTable.FIELD_URL, url);
        cacheValues.put(CacheDbOpenHelper.BaseCacheTable.FIELD_RETRIEVED_ON, System.currentTimeMillis());
        cacheValues.put(CacheDbOpenHelper.BaseCacheTable.FIELD_CACHED_OBJECT, responseStr);
        contentResolver.insert(CacheContentProvider.MAPIT_CACHE_URI, cacheValues);

        JsonParser jp = new JsonParser();
        return jp.parse(responseStr);
    }

    /**
     * Try to return the json element from cache, then from the web service if that fails.
     * @param arg ONS area code (NOT {@link nde2.pull.types.Area#getAreaId()})
     * @return a JsonElement that holds the geometry
     * @throws Exception
     */
	private static JsonElement getJson(String arg) throws Exception {
		String full_url = String.format(CALL, arg);

        JsonElement jsonElement = getJsonFromDatabase(full_url);
        if(jsonElement == null){
            jsonElement = getJsonFromMapit(full_url);
        }
        return jsonElement;
	}

    /**
     * Gets the geometry for the specified area.
     * @param area The area to find the boundary of.
     * @return Array of coordinates ((lon, lat), ...) that make up the shape of the boundary.
     * @throws Exception
     */
	public static double[][] getGeometryForArea(nde2.pull.types.Area area)
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, NDE2Exception, Exception {
		JsonElement _root = getJson(new GetAreaDetail(area).execute()
				.getExtCode());
		JsonObject geojson = _root.getAsJsonObject();
		JsonArray coord_ja = geojson.get("coordinates").getAsJsonArray().get(0)
				.getAsJsonArray();
		double[][] coords = new double[coord_ja.size()][2];
		for (int i = 0; i < coord_ja.size(); i++) {
			JsonArray jsonCoordPair = coord_ja.get(i).getAsJsonArray();
			coords[i][0] = jsonCoordPair.get(0).getAsDouble();
			coords[i][1] = jsonCoordPair.get(1).getAsDouble();
		}
		return coords;
	}
}
