package nde2.helpers;

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

public class CensusHelpers {

	/**
	 * Finds {@link DataSetFamily DataSetFamilies} for an {@link Area} that
	 * begin with any of the keywords.
	 * 
	 * @param area
	 *            Area to find datasets for
	 * @param subject
	 *            The subject to find these datasets in
	 * @param keywords
	 *            Strings that the required datasets should start with
	 * @return A List of {@link DataSetFamily} objects representing the
	 *         requested datasets.
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws NDE2Exception
	 * @throws ParseException
	 * @throws ValueNotAvailable
	 */
	public static List<DataSetFamily> findRequiredFamilies(Area area,
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

	/**
	 * Finds a {@link Subject} for an {@link Area} by its name
	 * 
	 * @param area
	 *            Area to query
	 * @param subjectName
	 *            subject name to find
	 * @return A {@link Subject}
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws NDE2Exception
	 */
	public static Subject findSubject(Area area, String subjectName)
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
			}
		}

		return subject;
	}

}
