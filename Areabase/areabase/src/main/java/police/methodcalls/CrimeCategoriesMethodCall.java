package police.methodcalls;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Get a map of crime category slugs to their human-readable names.
 *
 * <a href="http://data.police.uk/docs/method/crime-categories/">See original docs</a>
 */
public class CrimeCategoriesMethodCall extends BaseMethodCall {
	private final static String METHOD = "crime-categories";
	private Date date = null;

	public CrimeCategoriesMethodCall addDate(Date date) {
		this.date = date;
		return this;
	}

	public Map<String, String> getCrimeCategories()
            throws Exception {
		HashMap<String, String> params = null;
		if (!(date == null)) {
			params = new HashMap<String, String>();
			params.put("date", new SimpleDateFormat("yyyy-MM").format(date));
		}

		String raw_json = doCall(METHOD, params);

		Hashtable<String, String> categories = new Hashtable<String, String>();
		JsonArray raw_array = new JsonParser().parse(raw_json).getAsJsonArray();
		for (JsonElement el : raw_array) {
			JsonObject obj = el.getAsJsonObject();
			categories.put(obj.get("url").getAsString(), obj.get("name")
					.getAsString());
		}
		return categories;
	}
}
