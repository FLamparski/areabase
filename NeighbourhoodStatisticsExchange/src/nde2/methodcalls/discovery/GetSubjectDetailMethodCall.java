package nde2.methodcalls.discovery;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nde2.errors.NDE2Exception;
import nde2.types.discovery.DetailedSubject;
import nde2.types.discovery.Subject;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * <i>Encapsulates the GetSubjectDetail() call to the NDE2 web service. Creation
 * follows the Builder pattern.</i>
 * 
 * <p>
 * This operation allows you to obtain the metadata associated with your
 * requested subject. Dataset families are not returned.
 * 
 * @author filip
 * 
 */
public class GetSubjectDetailMethodCall extends BaseMethodCall {

	private static final String METHOD_NAME = "GetSubjectDetail";

	private Subject basicSubject;

	public GetSubjectDetailMethodCall() {
		basicSubject = null;
	}

	public GetSubjectDetailMethodCall addSubject(Subject subject) {
		this.basicSubject = subject;
		return this;
	}

	/**
	 * 
	 * @return A detailed representation of the supplied {@link Subject}
	 * @throws XPathExpressionException
	 *             Thrown when the XPath expressions fail to evaluate.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws NDE2Exception
	 * @see {@link BaseMethodCall} for more information about the exceptions
	 *      thrown.
	 */
	public DetailedSubject getSubjectDetail() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception {
		/*
		 * First, create a Dictionary containing parameters to call the remote
		 * method with.
		 */
		Map<String, String> params = new Hashtable<String, String>();
		params.put("SubjectId", Long.toString(basicSubject.getId()));

		/*
		 * Second, call the remote method. Store the response in a Document.
		 * Create an XPath object as well. Note that this Document should be
		 * what is actually expected, as service errors are detected by
		 * doCall_base(), and thrown as Exceptions.
		 */
		Document nessResponse = doCall_base(METHOD_NAME, params);
		XPath xpath = XPathFactory.newInstance().newXPath();

		String desc = (String) xpath.evaluate(
				"//*[local-name() = 'Description']/text()", nessResponse,
				XPathConstants.STRING);
		String desc_ext = (String) xpath.evaluate(
				"//*[local-name() = 'OptionalMetaData']/text()", nessResponse,
				XPathConstants.STRING);

		DetailedSubject detailedSubject = new DetailedSubject(basicSubject,
				desc, desc_ext);
		return detailedSubject;
	}
}
