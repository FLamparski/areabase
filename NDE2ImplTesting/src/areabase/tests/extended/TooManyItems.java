package areabase.tests.extended;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.methodcalls.delivery.GetTablesMethodCall;
import nde2.types.delivery.Dataset;
import nde2.types.discovery.Area;
import nde2.types.discovery.DataSetFamily;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.xml.sax.SAXException;

public class TooManyItems {
	private ArrayList<Area> myAreas;
	private ArrayList<DataSetFamily> myFamilies;

	private ArrayList<Area> badAreas;
	private ArrayList<DataSetFamily> badDsFamilies;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		myAreas = new ArrayList<>();
		myFamilies = new ArrayList<>();

		Area mockArea = mock(Area.class);
		when(mockArea.getAreaId()).thenReturn(6275657l, 6275658l, 6275659l,
				6275660l, 6275661l, 6275662l, 6275663l);
		for (int i = 0; i < 7; i++) {
			myAreas.add(mockArea);
		}

		DataSetFamily mockDsFamily = mock(DataSetFamily.class);
		when(mockDsFamily.getFamilyId()).thenReturn(2266, 2265, 2264, 1471,
				1470, 1469, 1468, 1163, 904, 971, 650);
		for (int i = 0; i < 11; i++) {
			myFamilies.add(mockDsFamily);
		}

		Area badArea = new Area("Failtown", 0l, 0, 0);
		badAreas = new ArrayList<>();
		badAreas.add(badArea);

		DataSetFamily badDsFam = mock(DataSetFamily.class);
		when(badDsFam.getFamilyId()).thenReturn(-1);
		badDsFamilies = new ArrayList<>();
		badDsFamilies.add(badDsFam);
	}

	@SuppressWarnings("unused")
	@Test(expected = NDE2Exception.class)
	public void testGetTablesWithTooManyCells()
			throws XPathExpressionException, NullPointerException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, ParseException {
		List<Dataset> ds = new GetTablesMethodCall().addAreas(myAreas)
				.addDatasetFamilies(myFamilies).getTables();
	}

	@SuppressWarnings("unused")
	@Test(expected = NDE2Exception.class)
	public void testGetTablesWithBadData() throws XPathExpressionException,
			NullPointerException, ParserConfigurationException, SAXException,
			IOException, NDE2Exception, ParseException {
		List<Dataset> ds = new GetTablesMethodCall().addAreas(badAreas)
				.addDatasetFamilies(badDsFamilies).getTables();
	}

}
