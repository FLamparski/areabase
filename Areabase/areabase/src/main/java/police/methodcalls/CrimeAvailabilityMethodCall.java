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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    /**
     * @return A list of dates for which crime data is available
     * @throws Exception
     */
	public ArrayList<Date> getAvailableDates() throws Exception {
		String raw_json = doCall("crimes-street-dates", null);
		JsonArray idates = new JsonParser().parse(raw_json).getAsJsonArray();

		ArrayList<Date> dates = new ArrayList<Date>();
		for (JsonElement idate : idates) {
			dates.add(new SimpleDateFormat("yyyy-MM").parse(idate
					.getAsJsonObject().get("date").getAsString()));
		}

		return dates;
	}

    /**
     * @return The latest date for which crime data is available
     * @throws Exception
     */
	public Date getLastUpdated() throws Exception {
		String raw_json = doCall("crime-last-updated", null);

		JsonObject dateObj = new JsonParser().parse(raw_json).getAsJsonObject();
		return new SimpleDateFormat("yyyy-MM-dd").parse(dateObj.get("date")
				.getAsString());
	}
}
