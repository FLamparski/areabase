package areabase.tests.extended;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.discovery.GetAreaChildrenMethodCall;
import nde2.types.discovery.Area;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.xml.sax.SAXException;

/**
 * Tests for behaviour when getting children for areas that are already at the
 * bottom of the hierarchy.
 * 
 * @author filip
 * 
 */
public class ExtremeAreaChildren {

	@Mock
	private Area leafArea;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(leafArea.getAreaId()).thenReturn(6333197l);
	}

	@SuppressWarnings("unused")
	@Test(expected = ValueNotAvailable.class)
	public void testChildlessArea() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, ValueNotAvailable {
		List<Area> nothing = new GetAreaChildrenMethodCall().addArea(leafArea)
				.getAreaChildren();
	}

}
