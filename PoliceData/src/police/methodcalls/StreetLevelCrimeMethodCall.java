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

public class StreetLevelCrimeMethodCall extends BaseMethodCall {
	private Date date = null;
	private Double latitude = null;
	private Double longitude = null;
	private Double[][] poly = null;

	private final static String METHOD = "crimes-street";

	public StreetLevelCrimeMethodCall addDate(Date date) {
		this.date = date;
		return this;
	}

	public StreetLevelCrimeMethodCall addPoint(double lat, double lon) {
		this.latitude = lat;
		this.longitude = lon;
		return this;
	}

	public StreetLevelCrimeMethodCall addAreaPolygon(Double[][] poly) {
		this.poly = poly;
		return this;
	}

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
			for (Double[] point : poly) {
				polyRep += point[0].toString() + ":" + point[1].toString()
						+ ",";
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

		Gson gparser = new Gson();
		Type collectionT = new TypeToken<Collection<Crime>>() {
		}.getType();
		crimes = gparser.fromJson(raw_json, collectionT);

		return crimes;
	}

	public Collection<Crime> getStreetLevelCrime() throws IOException,
			APIException {
		return getStreetLevelCrime("all-crime");
	}
}
