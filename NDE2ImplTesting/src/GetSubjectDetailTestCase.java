import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.types.discovery.DetailedSubject;
import nde2.types.discovery.Subject;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class GetSubjectDetailTestCase {

	Subject mockSubject;

	@Before
	public void setUp() throws Exception {
		mockSubject = new Subject("Crime and Safety", 3);
	}

	@Test
	public void test() {
		DetailedSubject retSubj = null;
		try {
			retSubj = mockSubject.getDetailed();
		} catch (XPathExpressionException | ParserConfigurationException
				| SAXException | IOException | NDE2Exception e) {
			fail("Exception has been thrown");
			e.printStackTrace();
		}
		System.out.println(String.format("Subject #%d: %s -- %s -- %s",
				retSubj.getId(), retSubj.getName(), retSubj.getDescription(),
				retSubj.getMoreDescription()));
	}

}
