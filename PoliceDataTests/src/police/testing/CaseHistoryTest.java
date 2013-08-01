package police.testing;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Map.Entry;

import org.junit.Test;

import police.errors.APIException;
import police.methodcalls.CrimeOutcomesMethodCall;
import police.types.CaseHistory;

public class CaseHistoryTest {

	@Test
	public void getOutcomes() throws SocketTimeoutException, IOException,
			APIException {
		CaseHistory history = new CrimeOutcomesMethodCall()
				.getOutcomes("512353fc46fc187b2592837e92b384d969ecb12463020b6b7287676f74ca3827");
		Entry<String, Collection<String>> historyStringified = history
				.toStringWithExtraInfo();
		System.out.println(historyStringified.getKey());
		System.err.println(historyStringified.getValue());
	}

}
