package nde2.methodcalls.discovery;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nde2.errors.NDE2Exception;
import nde2.types.discovery.Area;
import nde2.types.discovery.Subject;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <i>Encapsulates the GetCompatibleSubjects() call to the NDE2 web service.
 * Creation follows the Builder pattern.</i>
 * <p>
 * This operation retrieves the available subjects, without dataset families.
 * With each subject, however, a count is returned of dataset families that are
 * compatible with the supplied area.
 * 
 * @author filip
 * 
 */
public class GetCompatibleSubjectsMethodCall extends BaseMethodCall {
	private static final String METHOD_NAME = "GetCompatibleSubjects";
	private Area area;

	public GetCompatibleSubjectsMethodCall() {
		area = null;
	}

	public GetCompatibleSubjectsMethodCall addArea(Area area) {
		this.area = area;
		return this;
	}

	public Dictionary<Subject, Integer> getCompatibleSubjects()
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, NDE2Exception {
		Dictionary<String, String> params = new Hashtable<String, String>();
		params.put("AreaId", Long.toString(area.getAreaId()));

		Document nessResponse = doCall_base(METHOD_NAME, params);
		XPath xpath = XPathFactory.newInstance().newXPath();

		NodeList subjectIds = (NodeList) xpath.evaluate(
				"//*[local-name() = 'SubjectId']", nessResponse,
				XPathConstants.NODESET);
		NodeList subjectNames = (NodeList) xpath.evaluate(
				"//*[local-name() = 'Name']", nessResponse,
				XPathConstants.NODESET);
		NodeList subjectCounts = (NodeList) xpath.evaluate(
				"//*[local-name() = 'Count']", nessResponse,
				XPathConstants.NODESET);

		Hashtable<Subject, Integer> results = new Hashtable<Subject, Integer>();
		for (int i = 0; i < subjectIds.getLength(); i++) {
			String subjectName = subjectNames.item(i).getTextContent();
			int subjectId = Integer.parseInt(subjectIds.item(i)
					.getTextContent());
			int subjectCount = Integer.parseInt(subjectCounts.item(i)
					.getTextContent());

			Subject subject = new Subject(subjectName, subjectId);
			results.put(subject, new Integer(subjectCount));
		}
		return results;
	}
}
