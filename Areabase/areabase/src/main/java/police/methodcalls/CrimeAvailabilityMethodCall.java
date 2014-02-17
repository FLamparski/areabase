package police.methodcalls;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import police.errors.APIException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Encapsulates the following Police API methods:
 * <ul>
 * <li><b>crimes-street-dates</b>: Lists dates for which data is available.
 * <li><b>crime-last-updated</b>: Retrieves the last date on which the crime
 * information was updated.
 * </ul>
 * 
 * @author filip
 * @see <a
 *      href="http://data.police.uk/docs/method/crimes-street-dates/">crimes-street-dates</a>
 *      , <a
 *      href="http://data.police.uk/docs/method/crime-last-updated/">crime-last
 *      -updated</a>
 */
public class CrimeAvailabilityMethodCall extends BaseMethodCall {

	public ArrayList<Date> getAvailableDates() throws SocketTimeoutException,
			IOException, APIException, ParseException {
		String raw_json = doCall("crimes-street-dates", null);
		JsonArray idates = new JsonParser().parse(raw_json).getAsJsonArray();

		ArrayList<Date> dates = new ArrayList<Date>();
		for (JsonElement idate : idates) {
			dates.add(new SimpleDateFormat("yyyy-MM").parse(idate
					.getAsJsonObject().get("date").getAsString()));
		}

		return dates;
	}

	public Date getLastUpdated() throws SocketTimeoutException, IOException,
			APIException, ParseException {
		String raw_json = doCall("crime-last-updated", null);

		JsonObject dateObj = new JsonParser().parse(raw_json).getAsJsonObject();
		return new SimpleDateFormat("yyyy-MM-dd").parse(dateObj.get("date")
				.getAsString());
	}
}
