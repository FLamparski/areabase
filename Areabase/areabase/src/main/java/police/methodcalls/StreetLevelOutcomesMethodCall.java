package police.methodcalls;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;

import police.errors.APIException;
import police.types.Outcome;

public class StreetLevelOutcomesMethodCall extends StreetLevelCrimeMethodCall {
	private static final String METHOD = "outcomes-at-location";

	private Integer location_id = null;

	public StreetLevelOutcomesMethodCall addLocationId(Integer lid) {
		this.location_id = lid;
		return this;
	}

	public Collection<Outcome> getOutcomesAtLocation()
            throws Exception {
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
		if (location_id != null) {
			parameters.put("location", location_id.toString());
		}

		String raw_json;
		try {
			raw_json = doCall(METHOD, parameters);
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

		Collection<Outcome> outcomes = processOutcomes(raw_json);

		return outcomes;
	}

	protected Collection<Outcome> processOutcomes(String raw_json) {
		Gson gson = new Gson();
		Type collectionT = new TypeToken<Collection<Outcome>>() {
		}.getType();
		Collection<Outcome> outcomes = gson.fromJson(raw_json, collectionT);
		return outcomes;
	}
}
