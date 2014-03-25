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

import java.util.Date;
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

    private static Map<String, String> inMemoryCache = null;

    private Map<String, String> getFromStorage() throws Exception {
        String raw_json = doCall(METHOD, null);

        Hashtable<String, String> categories = new Hashtable<String, String>();
        JsonArray raw_array = new JsonParser().parse(raw_json).getAsJsonArray();
        for (JsonElement el : raw_array) {
            JsonObject obj = el.getAsJsonObject();
            categories.put(obj.get("url").getAsString(), obj.get("name")
                    .getAsString());
        }
        return categories;
    }

	public Map<String, String> getCrimeCategories()
            throws Exception {
		if(inMemoryCache == null){
            inMemoryCache = getFromStorage();
        }
		return inMemoryCache;
	}
}
