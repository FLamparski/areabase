/**
 * 
 */
package police.testing;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import police.errors.APIException;
import police.methodcalls.CrimeAvailabilityMethodCall;

public class StreetLevelAvailability {

	private static CrimeAvailabilityMethodCall mcall;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mcall = new CrimeAvailabilityMethodCall();
	}

	@Test
	public void getAvailableDates() throws SocketTimeoutException, IOException,
			APIException, ParseException {
		ArrayList<Date> dates = mcall.getAvailableDates();
		System.out.println("Crime data is available for: ");
		for (Date date : dates) {
			System.out.println(new SimpleDateFormat("yyyy-MM").format(date));
		}
	}

	@Test
	public void getLastUpdated() throws SocketTimeoutException, IOException,
			APIException, ParseException {
		Date lastUpdated = mcall.getLastUpdated();
		System.out.println("Last updated: "
				+ new SimpleDateFormat("yyyy-MM-dd").format(lastUpdated));
	}

}
