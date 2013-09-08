package areabase.tests.extended;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.discovery.GetDatasetsMethodCall;
import nde2.types.discovery.DataSetFamily;

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
		List<DataSetFamily> nothing = new GetDatasetsMethodCall()
				.addAreaId(6275153l).addSubjectId(1).getDatasets();
	}

	@SuppressWarnings("unused")
	@Test(expected = NDE2Exception.class)
	public void testGetDatasets_SubjectMinusOne()
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, NDE2Exception, ParseException,
			ValueNotAvailable {
		List<DataSetFamily> nothing = new GetDatasetsMethodCall()
				.addAreaId(6275153l).addSubjectId(-1).getDatasets();
	}

	@SuppressWarnings("unused")
	@Test
	public void testGetDatasets_CensusDatasetsForBank()
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, NDE2Exception, ParseException,
			ValueNotAvailable {
		System.out.println("Testing Bank.");
		long startTime = System.currentTimeMillis();
		List<DataSetFamily> nothing = new GetDatasetsMethodCall()
				.addAreaId(6479546l).addSubjectId(5).getDatasets();
		long endTime = System.currentTimeMillis();
		System.out.println("This took " + (endTime - startTime) + "ms");
	}

}
