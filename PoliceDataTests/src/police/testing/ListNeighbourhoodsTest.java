package police.testing;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;

import org.junit.Test;

import police.errors.APIException;
import police.methodcalls.NeighbourhoodsMethodCall;

public class ListNeighbourhoodsTest {

	@Test
	public void test() throws SocketTimeoutException, IOException, APIException {
		Map<String, String> neighbourhoods = new NeighbourhoodsMethodCall()
				.listNeighbourhoods("metropolitan");
		System.out.println(neighbourhoods.toString());
	}
}
