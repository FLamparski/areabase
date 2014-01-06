package nde2.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nde2.errors.NDE2Exception;
import nde2.pull.methodcalls.discovery.GetDatasetFamilies;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.Subject;

import org.xmlpull.v1.XmlPullParserException;

public class CensusHelpers {

	/**
	 * Finds {@link DataSetFamily DataSetFamilies} for an {@link Area} that
	 * contain any of the keywords.
	 * 
	 * @param area
	 *            Area to find datasets for
	 * @param subject
	 *            The subject to find these datasets in
	 * @param keywords
	 *            Strings that the required datasets should start with
	 * @return A List of {@link DataSetFamily} objects representing the
	 *         requested datasets.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws NDE2Exception
	 * @throws ClassNotFoundException
	 */
	public static List<DataSetFamily> findRequiredFamilies(Area area,
			Subject subject, String[] keywords) throws IOException,
			XmlPullParserException, NDE2Exception, ClassNotFoundException {
		List<DataSetFamily> censusDatasetFamilies = new GetDatasetFamilies(
				subject).forArea(area).execute();

		List<DataSetFamily> requiredFamilies = new ArrayList<DataSetFamily>();

		for (DataSetFamily family : censusDatasetFamilies) {
			for (String kw : keywords) {
				if (family.getName().contains(kw))
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
	 * @throws NDE2Exception
	 * @throws XmlPullParserException
	 * @throws IOException
	 * 
	 */
	public static Subject findSubject(Area area, String subjectName)
			throws IOException, XmlPullParserException, NDE2Exception {
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
