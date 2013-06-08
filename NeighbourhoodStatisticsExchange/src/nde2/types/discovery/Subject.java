package nde2.types.discovery;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.methodcalls.discovery.GetSubjectDetailMethodCall;
import nde2.types.NDE2Result;

import org.xml.sax.SAXException;

/**
 * Represents a subject in the NDE database.
 * 
 * @author filip
 * @see {@link DetailedSubject}
 * 
 */
public class Subject extends NDE2Result {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private int id;

	public Subject(String name, int id) {
		this.name = name;
		this.id = id;
	}

	protected Subject(Subject copy) {
		this.name = copy.name;
		this.id = copy.id;
	}

	/**
	 * 
	 * @return The subject's proper name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return The subject's ID, used for NDE querying.
	 */
	public int getId() {
		return id;
	}

	/**
	 * This is a web method. Call asynchronously.
	 * 
	 * @return A more detailed representation of this subject.
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws NDE2Exception
	 */
	public DetailedSubject getDetailed() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception {
		return new GetSubjectDetailMethodCall().addSubject(this)
				.getSubjectDetail();
	}

}
