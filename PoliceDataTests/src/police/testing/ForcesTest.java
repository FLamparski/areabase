package police.testing;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import police.errors.APIException;
import police.methodcalls.ForcesMethodCall;
import police.types.ForceInformation;

public class ForcesTest {

	private static ForcesMethodCall mCall;

	@BeforeClass
	public static void setUpClass() {
		mCall = new ForcesMethodCall();
	}

	@Test
	public void listForces() throws SocketTimeoutException, IOException,
			APIException {
		Map<String, String> forces = mCall.listForces();
		Set<Entry<String, String>> forceSet = forces.entrySet();
		for (Entry<String, String> force : forceSet) {
			System.out.printf("%s\t%s\n", force.getKey(), force.getValue());
		}
	}

	@Test
	public void forceDetail() throws SocketTimeoutException, IOException,
			APIException {
		ForceInformation metropolitanForceInformation = mCall
				.getForceDetails("metropolitan");
		System.out.println(metropolitanForceInformation.toString());
	}

}
