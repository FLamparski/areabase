package com.uk_postcodes.api;

import android.location.Location;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.commons.io.IOUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.annotation.RegEx;

/**
 * Contains static functions for working with postcodes.
 * @author filip
 */
public class Postcode {
    /**
     * I found this pattern in the archives of the Cabinet Office, and it is verified
     * as working here: <a href="http://stackoverflow.com/a/164994">http://stackoverflow.com/a/164994</a>
     */
    @RegEx public static final String POSTCODE_REGEX = "(GIR 0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY])))) [0-9][A-Z-[CIKMOV]]{2})";
    private static Pattern postcodePattern = Pattern.compile(POSTCODE_REGEX);

    /**
     * Format: lat, lng
     */
    private final static String CALL = "http://uk-postcodes.com/latlng/%f,%f.json";

    /**
     * Get the postcode closest to the location.
     * @param location The device's location
     * @return the closest postcode
     * @throws Exception
     */
    public static String forLocation(Location location) throws Exception {
        String address = String.format(CALL, location.getLatitude(), location.getLongitude());

        Log.d("Postcode", "Calling: " + address);

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

    /**
     * Checks the candidate postcode against the Royal Mail approved regex
     * @param postcode the postcode to validate
     * @return true if the postcode is valid
     */
    public static boolean isValid(String postcode){
        return postcodePattern.matcher(postcode.toUpperCase(Locale.UK)).matches();
    }
}
