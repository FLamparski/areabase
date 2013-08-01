package police.testing;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import police.errors.APIException;
import police.methodcalls.CrimeCategoriesMethodCall;

public class CrimeCategories {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws SocketTimeoutException, IOException, APIException {
		Map<String, String> categories = new CrimeCategoriesMethodCall()
				.getCrimeCategories();
		System.out.printf("Got %d categories:\n", categories.size());
		Set<Entry<String, String>> categorySet = categories.entrySet();
		for (Entry<String, String> category : categorySet) {
			System.out.printf("%s\t%s\n", category.getKey(),
					category.getValue());
		}
	}

}
