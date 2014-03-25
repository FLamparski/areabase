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
import com.google.gson.Gson;

import police.types.CaseHistory;
import police.types.Crime;

/**
 * Returns the outcomes (case history) for the specified crime. Crime ID is 64-character identifier, as returned by other API methods.
 *
 * <p><strong>Note: Outcomes are not available for the Police Service of Northern Ireland.</strong></p>
 *
 * <p><a href="http://data.police.uk/docs/method/outcomes-for-crime/">See original docs</a></p>
 */
public class CaseHistoryMethodCall extends BaseMethodCall {
	private final static String METHOD = "outcomes-for-crime";

	private String persistent_id;

    /**
     * Get case history for a crime
     * @param persistent_crime_id crime UUID
     * @return case history
     * @throws Exception
     */
	public CaseHistory getOutcomes(String persistent_crime_id)
            throws Exception {
		this.persistent_id = persistent_crime_id;
		return getOutcomes();
	}

    /**
     * Get case history for a crime
     * @param crime crime object
     * @return case history
     * @throws Exception
     */
	public CaseHistory getOutcomes(Crime crime) throws Exception {
		this.persistent_id = crime.getPersistent_id();
		return getOutcomes();
	}

	protected CaseHistory getOutcomes() throws Exception {
		String raw_json = doCall(METHOD + "/" + persistent_id, null);
		Gson gson = new Gson();
		return gson.fromJson(raw_json, CaseHistory.class);
	}
}
