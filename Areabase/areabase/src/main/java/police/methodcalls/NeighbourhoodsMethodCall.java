package police.methodcalls;

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
