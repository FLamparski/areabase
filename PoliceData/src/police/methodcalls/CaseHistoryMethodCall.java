package police.methodcalls;

import java.io.IOException;
import java.net.SocketTimeoutException;

import police.errors.APIException;
import police.types.CaseHistory;
import police.types.Crime;

import com.google.gson.Gson;

public class CaseHistoryMethodCall extends BaseMethodCall {
	private final static String METHOD = "outcomes-for-crime";

	private String persistent_id;

	public CaseHistory getOutcomes(String persistent_crime_id)
			throws SocketTimeoutException, IOException, APIException {
		this.persistent_id = persistent_crime_id;
		return getOutcomes();
	}

	public CaseHistory getOutcomes(Crime crime) throws SocketTimeoutException,
			IOException, APIException {
		this.persistent_id = crime.getPersistent_id();
		return getOutcomes();
	}

	protected CaseHistory getOutcomes() throws SocketTimeoutException,
			IOException, APIException {
		String raw_json = doCall(METHOD + "/" + persistent_id, null);
		Gson gson = new Gson();
		return gson.fromJson(raw_json, CaseHistory.class);
	}
}
