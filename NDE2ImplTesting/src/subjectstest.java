import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.types.discovery.Area;
import nde2.types.discovery.Subject;

import org.xml.sax.SAXException;

public class subjectstest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Area area = new Area("Lewisham", 6275153L, 13, 26);
		for (int rep = 1; rep <= 5; rep++) {
			System.out.println("Repetition " + rep);
			try {
				Map<Subject, Integer> availSubjects = area
						.getCompatibleDatasets();
				System.out.println(String.format(
						"Found %d available subjects.", availSubjects.size()));
				Set<Entry<Subject, Integer>> availSubjectsList = availSubjects
						.entrySet();
				for (Entry<Subject, Integer> availSubject : availSubjectsList) {
					System.out.println(String.format("%d of Subject #%d: %s",
							availSubject.getValue(), availSubject.getKey()
									.getId(), availSubject.getKey().getName()));
				}
			} catch (XPathExpressionException | ParserConfigurationException
					| SAXException | IOException | NDE2Exception e) {
				e.printStackTrace();
			}
		}
	}

}
