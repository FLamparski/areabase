package areabase.tests.ultimate;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.discovery.GetDatasetsMethodCall;
import nde2.types.discovery.Area;
import nde2.types.discovery.DataSetFamily;
import nde2.types.discovery.Subject;

import org.xml.sax.SAXException;

public class DataProviderTestBase {

	protected static List<DataSetFamily> findRequiredFamilies(Area area,
			Subject subject, String[] keywords)
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, NDE2Exception, ParseException,
			ValueNotAvailable {
		List<DataSetFamily> censusDatasetFamilies = new GetDatasetsMethodCall()
				.addArea(area).addSubject(subject).getDatasets();
		List<DataSetFamily> requiredFamilies = new ArrayList<DataSetFamily>();

		for (DataSetFamily family : censusDatasetFamilies) {
			for (String kw : keywords) {
				if (family.getName().startsWith(kw))
					requiredFamilies.add(family);
			}
		}

		return requiredFamilies;
	}

	protected static Subject findSubject(Area area, String subjectName)
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, NDE2Exception {
		Subject subject = null;
		Map<Subject, Integer> areaSubjects = area.getCompatibleSubjects();
		/*
		 * Loop exit condition: valid subject id is found; or there are no more
		 * subjects left.
		 */
		Iterator<Subject> subjectIter = areaSubjects.keySet().iterator();
		while (subjectIter.hasNext() && subject == null) {
			Subject s = subjectIter.next();
			if (s.getName().equals(subjectName)) {
				subject = s;
				System.out.println("Found subject " + subjectName
						+ " for Area " + area.getName() + "; contains "
						+ areaSubjects.get(s) + " elements; id = " + s.getId());
			} else {
				System.out.println("Subject " + s.getName() + " is not "
						+ subjectName + ".");
			}
		}

		return subject;
	}

}
