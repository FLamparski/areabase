package police.methodcalls;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import police.errors.APIException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class NeighbourhoodsMethodCall extends BaseMethodCall {
	private static final String METHOD = "neighbourhoods";

	public Map<String, String> listNeighbourhoods(String forceId)
			throws SocketTimeoutException, IOException, APIException {
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
