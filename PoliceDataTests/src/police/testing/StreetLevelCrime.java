package police.testing;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import police.errors.APIException;
import police.methodcalls.StreetLevelCrimeMethodCall;
import police.methodcalls.StreetLevelOutcomesMethodCall;
import police.types.Crime;
import police.types.Outcome;

public class StreetLevelCrime {

	private Double lon;
	private Double lat;
	private Date date;

	@Before
	public void setUp() throws Exception {
		lon = -0.0186942;
		lat = 51.4452824;
		date = new SimpleDateFormat("yyyy-MM").parse("2013-01");
	}

	@Test
	public void streetLevelCrimeWithPointAndDate() throws IOException,
			APIException {
		Collection<Crime> crimesInCatford = new StreetLevelCrimeMethodCall()
				.addPoint(lat, lon).addDate(date).getStreetLevelCrime();
		int n_crimesProcessed = 0;
		int n_crimesWithErrors = 0;
		for (Crime aCrime : crimesInCatford) {
			Entry<String, Collection<String>> crimeWithRemarks = aCrime
					.toStringWithExtraInfo();
			if (crimeWithRemarks.getValue().isEmpty()) {
				n_crimesProcessed++;
			} else {
				n_crimesProcessed++;
				n_crimesWithErrors++;
				System.out.println(crimeWithRemarks.getKey());
				for (String remark : crimeWithRemarks.getValue()) {
					System.err.println(remark);
				}
			}
		}
		System.out.printf(
				"\nDone. Processed %d crimes total, %d of which had errors.\n",
				n_crimesProcessed, n_crimesWithErrors);
	}

	@Test
	public void streetLevelOutcomesWithPointAndDate()
			throws SocketTimeoutException, IOException, APIException {
		Collection<Outcome> outcomesInCatford = ((StreetLevelOutcomesMethodCall) new StreetLevelOutcomesMethodCall()
				.addPoint(lat, lon).addDate(date)).getOutcomesAtLocation();
		for (Outcome outcome : outcomesInCatford) {
			Entry<String, Collection<String>> outcomeWithRemarks = outcome
					.toStringWithExtraInfo();
			System.out.println(outcomeWithRemarks.getKey());
			System.err.println(outcomeWithRemarks.getValue());
		}
	}

}
