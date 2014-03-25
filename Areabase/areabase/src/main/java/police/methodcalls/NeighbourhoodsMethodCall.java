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
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Gets neighbourhoods for a specific force.
 *
 * <a href="http://data.police.uk/docs/method/neighbourhoods/">See original docs</a>
 */
public class NeighbourhoodsMethodCall extends BaseMethodCall {
	private static final String METHOD = "neighbourhoods";

	public Map<String, String> listNeighbourhoods(String forceId)
            throws Exception {
		String raw_json = doCall(forceId + "/" + METHOD, null);

		HashMap<String, String> neighbourhoods = new HashMap<String, String>();
		JsonArray neighbourhoods_array = new JsonParser().parse(raw_json)
				.getAsJsonArray();
		for (JsonElement neighbourhood_element : neighbourhoods_array) {
			neighbourhoods.put(neighbourhood_element.getAsJsonObject()
					.get("id").getAsString(), neighbourhood_element
					.getAsJsonObject().get("name").getAsString());
		}
		return neighbourhoods;
	}

}
