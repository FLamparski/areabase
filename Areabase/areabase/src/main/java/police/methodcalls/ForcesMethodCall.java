package police.methodcalls;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

import police.types.ForceInformation;

public class ForcesMethodCall extends BaseMethodCall {
	public static final String METHOD = "forces";

	public Map<String, String> listForces() throws Exception {
		String raw_json = doCall(METHOD, null);
		HashMap<String, String> forces = new HashMap<String, String>();
		JsonArray forces_array = new JsonParser().parse(raw_json)
				.getAsJsonArray();
		for (JsonElement force_element : forces_array) {
			forces.put(force_element.getAsJsonObject().get("id").getAsString(),
					force_element.getAsJsonObject().get("name").getAsString());
		}
		return forces;
	}

	public ForceInformation getForceDetails(String id)
            throws Exception {
		String raw_json = doCall(METHOD + "/" + id, null);
		return new Gson().fromJson(raw_json, ForceInformation.class);
	}
}
