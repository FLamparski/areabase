/**
 * 
 */
package police.testing;

import java.io.IOException;

import org.junit.Test;

import police.errors.APIException;
import police.methodcalls.BaseMethodCall;

/**
 * @author filip
 * 
 */
public class BaseCallTest {

	/**
	 * Test method for
	 * {@link police.methodcalls.BaseMethodCall#doCall(java.lang.String, java.util.Map)}
	 * .
	 * 
	 * @throws APIException
	 * @throws IOException
	 */
	@Test
	public void testDoCall() throws IOException, APIException {
		class TestCall extends BaseMethodCall {
			public String doCall() throws IOException, APIException {
				return super.doCall("forces", null);
			}
		}
		System.out.println(new TestCall().doCall());
	}

}
