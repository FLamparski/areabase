package police.methodcalls;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

import police.types.ForceInformation;

/**
 * Acquire information about police forces
 */
public class ForcesMethodCall extends BaseMethodCall {
	public static final String METHOD = "forces";

    /**
     * A list of all the police forces available via the API. Unique force identifiers obtained here
     * are used in requests for force-specific data via other methods.
     *
     * <a href="http://data.police.uk/docs/method/forces/">See original docs</a>
     *
     * @return a list of forces available
     * @throws Exception
     */
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

    /**
     * Detailed information about a specific force.
     *
     * <a href="http://data.police.uk/docs/method/force/">See original docs</a>
     *
     * @param id the force's id
     * @return an info object containing information about the force
     * @throws Exception
     */
	public ForceInformation getForceDetails(String id)
            throws Exception {
		String raw_json = doCall(METHOD + "/" + id, null);
		return new Gson().fromJson(raw_json, ForceInformation.class);
	}
}
