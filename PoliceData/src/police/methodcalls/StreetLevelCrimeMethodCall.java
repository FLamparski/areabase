package police.methodcalls;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import police.errors.APIException;
import police.types.Crime;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Represents a call to the Police API which returns a list of crimes within a
 * mile of a given point or within an area specified.
 * 
 * <p>
 * You cannot specify a point and an area at the same time.
 * 
 * @author filip
 * @see <a href="http://data.police.uk/docs/method/crime-street/">The Police API
 *      documentation of this method</a>
 */
public class StreetLevelCrimeMethodCall extends BaseMethodCall {
	protected Date date = null;
	protected Double latitude = null;
	protected Double longitude = null;
	protected double[][] poly = null;

	private final static String METHOD = "crimes-street";

	/**
	 * Only show crimes from the given month
	 * 
	 * @param date
	 *            A month to show crimes from
	 * @return this object for further modification
	 */
	public StreetLevelCrimeMethodCall addDate(Date date) {
		this.date = date;
		return this;
	}

	/**
	 * Specify a point used to look up crimes. The remote method will return
	 * crimes that happened within a mile of this location.
	 * <p>
	 * Awesomely, you can just put GPS coords here :)
	 * 
	 * @param lat
	 *            The latitude
	 * @param lon
	 *            The longitude
	 * @return this object for further modification
	 */
	public StreetLevelCrimeMethodCall addPoint(double lat, double lon) {
		this.latitude = lat;
		this.longitude = lon;
		return this;
	}

	/**
	 * Specify an area in which to look for crimes. Area cannot be larger than
	 * 20 sq km.
	 * 
	 * @param poly
	 *            An array of GPS coordinate pairs [[lon,lat],...] that describe
	 *            the polygon. No need to close this poly as the server will
	 *            just draw a straight line for you from the last point to the
	 *            first one.
	 * @return this object for further modification
	 */
	public StreetLevelCrimeMethodCall addAreaPolygon(double[][] poly) {
		this.poly = poly;
		return this;
	}

	/**
	 * Call the API and retrieve a {@link Collection} of {@link Crime Crimes}
	 * that was returned.
	 * 
	 * @param category
	 *            The category to filter the crimes by, can be obtained via
	 *            CrimeCategoriesMethodCall.
	 * @return A {@link Collection} of {@link Crime}.
	 * @throws IOException
	 *             When a connection could not be created -- see
	 *             {@link BaseMethodCall#doCall(String, java.util.Map)}
	 * @throws APIException
	 *             When the API returned a non-200 code. Typically, there would
	 *             be too many crimes to return (over 10000), or the area
	 *             specified using addPoly() is too large (over 20 sq km).
	 * @see BaseMethodCall
	 */
	public Collection<Crime> getStreetLevelCrime(String category)
			throws IOException, APIException {
		HashMap<String, String> parameters = new HashMap<String, String>();
		if ((latitude == null) && (longitude == null) && (poly == null)) {
			throw new NullPointerException("Must specify a polygon or a point");
		} else if (longitude != null && latitude != null && poly != null) {
			throw new UnsupportedOperationException(
					"Cannot use both poly and point");
		} else if (longitude != null && latitude != null) {
			parameters.put("lat", latitude.toString());
			parameters.put("lng", longitude.toString());
		} else if (poly != null) {
			String polyRep = "";
			for (double[] point : poly) {
				polyRep += Double.toString(point[1]) + ","
						+ Double.toString(point[0]) + ":";
			}
			polyRep = polyRep.substring(0, polyRep.length() - 1); // Remove
																	// trailing
																	// comma
			parameters.put("poly", polyRep);
		}
		if (date != null) {
			parameters
					.put("date", new SimpleDateFormat("yyyy-MM").format(date));
		}

		String raw_json;
		try {
			raw_json = doCall(METHOD + "/" + category, parameters);
		} catch (APIException e) {
			/*
			 * Note that this catch block doesn't really catch the APIException,
			 * but it does add additional data to it, based on the response code
			 * from the server. This can be displayed to user, or used
			 * internally to gracefully handle weird requests.
			 */
			if (e.getHttpCode() == 400) {
				throw new APIException("The area specified is too large", e);
			} else if (e.getHttpCode() == 503) {
				// A very sad exception because it means people shoot each
				// other so much that even the Police API gives up on counting
				// them.
				throw new APIException("There are too many crimes to process",
						e);
			} else {
				throw e;
			}
		}

		Collection<Crime> resultValue;

		resultValue = processCrimes(raw_json);

		return resultValue;
	}

	protected Collection<Crime> processCrimes(String raw_json) {
		Collection<Crime> crimes;

		Type collectionT = new TypeToken<Collection<Crime>>() {
		}.getType();
		crimes = new Gson().fromJson(raw_json, collectionT);

		return crimes;
	}

	public Collection<Crime> getStreetLevelCrime() throws IOException,
			APIException {
		return getStreetLevelCrime("all-crime");
	}
}
