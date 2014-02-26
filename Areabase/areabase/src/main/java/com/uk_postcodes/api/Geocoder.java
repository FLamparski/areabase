package com.uk_postcodes.api;

import android.location.Location;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.commons.io.IOUtils;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This is because Google's geocoder doesn't return complete postcodes.
 * Yes, this means the application relies on a non-Google (and thus potentially less
 * reliable) service, but if the Google one is crap, there is no sense in being a fanboi.
 * Created by filip on 24/02/14.
 */
public class Geocoder {
    /**
     * Format: lat, lng
     */
    private final static String CALL = "http://uk-postcodes.com/latlng/%f,%f.json";
    public static String postcodeForLocation(Location location) throws Exception {
        String address = String.format(CALL, location.getLatitude(), location.getLongitude());

        Log.d("Geocoder", "Calling: " + address);

        URL callUrl = new URL(address);
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
        JsonElement response = jp.parse(responseStr);
        return response.getAsJsonObject().get("postcode").getAsString();
    }
}
