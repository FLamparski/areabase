package police.methodcalls;

import com.google.gson.Gson;

import police.types.CaseHistory;
import police.types.Crime;

public class CaseHistoryMethodCall extends BaseMethodCall {
	private final static String METHOD = "outcomes-for-crime";

	private String persistent_id;

	public CaseHistory getOutcomes(String persistent_crime_id)
            throws Exception {
		this.persistent_id = persistent_crime_id;
		return getOutcomes();
	}

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
