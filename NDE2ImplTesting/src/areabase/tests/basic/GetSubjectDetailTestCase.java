package areabase.tests.basic;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.methodcalls.discovery.GetSubjectDetailMethodCall;
import nde2.types.discovery.DetailedSubject;
import nde2.types.discovery.Subject;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class GetSubjectDetailTestCase {

	Subject mockSubject;

	@Before
	public void setUp() throws Exception {
		mockSubject = new Subject("Crime and Safety", 3);
	}

	@Test
	public void test() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception {
		DetailedSubject retSubj = null;

		retSubj = mockSubject.getDetailed();

		System.out.println(String.format("Subject #%d: %s -- %s -- %s",
				retSubj.getId(), retSubj.getName(), retSubj.getDescription(),
				retSubj.getMoreDescription()));
	}

	@SuppressWarnings("unused")
	@Test(expected = NDE2Exception.class)
	public void testInvalid() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception {
		DetailedSubject retSubj = new GetSubjectDetailMethodCall().addSubject(
				new Subject("Invalid", -1)).getSubjectDetail();
	}

}
