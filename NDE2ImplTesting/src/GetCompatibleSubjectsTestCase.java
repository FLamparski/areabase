import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
		Map<Subject, Integer> compatibleSubjects = null;
		try {
			compatibleSubjects = new GetCompatibleSubjectsMethodCall().addArea(
					area).getCompatibleSubjects();
		} catch (XPathExpressionException | ParserConfigurationException
				| SAXException | IOException | NDE2Exception e) {
			e.printStackTrace();
			fail("Error fetching compatible subjects");
		}
		Set<Entry<Subject, Integer>> subjects_with_counts = compatibleSubjects
				.entrySet();
		boolean found_crime_safety = false;
		for (Entry<Subject, Integer> subject_with_count : subjects_with_counts) {
			if (subject_with_count.getKey().getId() == 3) {
				found_crime_safety = true;
			}
		}
		if (!(found_crime_safety))
			fail("Did not find required subject Crime and Safety");
	}

}
