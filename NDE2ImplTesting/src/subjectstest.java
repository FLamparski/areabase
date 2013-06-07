import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;

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
				Dictionary<Subject, Integer> availSubjects = area
						.getCompatibleDatasets();
				System.out.println(String.format(
						"Found %d available subjects.", availSubjects.size()));
				Enumeration<Subject> availSubjectEnumeration = availSubjects
						.keys();
				while (availSubjectEnumeration.hasMoreElements()) {
					Subject sbj = availSubjectEnumeration.nextElement();
					System.out.println(String.format(
							"Subject #%d: %s (%d datasets)", sbj.getId(),
							sbj.getName(), availSubjects.get(sbj)));
				}
			} catch (XPathExpressionException | ParserConfigurationException
					| SAXException | IOException | NDE2Exception e) {
				e.printStackTrace();
			}
		}
	}

}
