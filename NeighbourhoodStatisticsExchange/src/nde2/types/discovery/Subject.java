package nde2.types.discovery;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.methodcalls.discovery.GetSubjectDetailMethodCall;
import nde2.types.NDE2Result;

import org.xml.sax.SAXException;

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

	public String getName() {
		return name;
	}

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
