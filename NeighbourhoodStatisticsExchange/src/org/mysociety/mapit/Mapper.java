package org.mysociety.mapit;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.types.discovery.Area;

import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Mapper {

	private final static String CALL = "http://mapit.mysociety.org/area/%s.geojson";

	private static JsonElement getJson(String arg) throws Exception {
		String full_url = String.format(CALL, arg);

		// Uncomment for testing:
		System.out.println("Calling " + full_url);

		URL callUrl = new URL(full_url);
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

		JsonParser jp = new JsonParser();
		return jp.parse(responseStr);
	}

	public static double[][] getGeometryForArea(Area area)
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, NDE2Exception, Exception {
		JsonElement _root = getJson(area.getDetailed().getExtCode());
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
