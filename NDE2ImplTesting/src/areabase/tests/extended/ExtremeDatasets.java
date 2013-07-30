package areabase.tests.extended;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.discovery.GetDatasetsMethodCall;
import nde2.types.discovery.DataSetFamiliy;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Tests behaviour when the requested subject is either invalid or there are no
 * datasets for it available.
 * 
 * @author filip
 * 
 */
public class ExtremeDatasets {

	@SuppressWarnings("unused")
	@Test(expected = ValueNotAvailable.class)
	public void testGetDatasets_SubjectOne() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, ParseException, ValueNotAvailable {
		List<DataSetFamiliy> nothing = new GetDatasetsMethodCall()
				.addAreaId(6275153l).addSubjectId(1).getDatasets();
	}

	@SuppressWarnings("unused")
	@Test(expected = NDE2Exception.class)
	public void testGetDatasets_SubjectMinusOne()
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, NDE2Exception, ParseException,
			ValueNotAvailable {
		List<DataSetFamiliy> nothing = new GetDatasetsMethodCall()
				.addAreaId(6275153l).addSubjectId(-1).getDatasets();
	}

}
