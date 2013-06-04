import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Dictionary;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.methodcalls.discovery.GetCompatibleSubjectsMethodCall;
import nde2.types.discovery.Area;
import nde2.types.discovery.Subject;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class GetCompatibleSubjectsTestCase {

	Area area;

	@Before
	public void setUp() throws Exception {
		area = new Area("Lewisham", 6275153L, 13, 26);
	}

	@Test
	public void testGetCompatibleSubjects() {
		Dictionary<Subject, Integer> compatibleSubjects = null;
		try {
			compatibleSubjects = new GetCompatibleSubjectsMethodCall().addArea(
					area).getCompatibleSubjects();
		} catch (XPathExpressionException | ParserConfigurationException
				| SAXException | IOException | NDE2Exception e) {
			e.printStackTrace();
			fail("Error fetching compatible subjects");
		}
		Subject testSubject = compatibleSubjects.keys().nextElement();
		int testSubjectCount = compatibleSubjects.get(testSubject).intValue();
		assertEquals(58, testSubject.getId());
		assertEquals(190, testSubjectCount);
	}

}
