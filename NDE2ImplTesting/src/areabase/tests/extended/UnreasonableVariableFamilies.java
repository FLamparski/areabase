package areabase.tests.extended;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.discovery.GetVariablesMethodCall;
import nde2.types.discovery.DataSetFamiliy;
import nde2.types.discovery.DateRange;
import nde2.types.discovery.VariableFamily;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.xml.sax.SAXException;

public class UnreasonableVariableFamilies {

	@Mock
	private DataSetFamiliy mockDsFam;
	@Mock
	private DataSetFamiliy mockBadFam;
	@Mock
	private DateRange mockDateRange;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(mockDsFam.getFamilyId()).thenReturn(2266);
		when(mockBadFam.getFamilyId()).thenReturn(-1);
		when(mockDateRange.getStartDate()).thenReturn(
				(new SimpleDateFormat("yyyy-MM-dd").parse("1994-12-05")));
		when(mockDateRange.getEndDate()).thenReturn(
				(new SimpleDateFormat("yyyy-MM-dd").parse("1995-12-04")));

	}

	@SuppressWarnings("unused")
	@Test(expected = ValueNotAvailable.class)
	public void testGetVariablesWithBadDates() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, ParseException, ValueNotAvailable {
		List<VariableFamily> families = new GetVariablesMethodCall()
				.addDatasetFamily(mockDsFam).addDateRange(mockDateRange)
				.getVariables();
	}

	@SuppressWarnings("unused")
	@Test(expected = NDE2Exception.class)
	public void testGetVariablesWithBadDsFamily()
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, NDE2Exception, ParseException,
			ValueNotAvailable {
		List<VariableFamily> families = new GetVariablesMethodCall()
				.addDatasetFamily(mockBadFam).getVariables();
	}

}
