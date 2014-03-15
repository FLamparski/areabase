package police.methodcalls;

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
