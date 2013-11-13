package com.comparetheparser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.discovery.FindAreasMethodCall;
import nde2.methodcalls.discovery.GetAreaChildrenMethodCall;
import nde2.methodcalls.discovery.GetAreaParentMethodCall;
import nde2.methodcalls.discovery.GetCompatibleSubjectsMethodCall;
import nde2.types.discovery.Area;
import nde2.types.discovery.Subject;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

public class BasicXPath implements BasicAreaMethods {

	private static Area mockLewisham;

	@SuppressWarnings("deprecation")
	@BeforeClass
	public static void setupBefore() {
		mockLewisham = mock(Area.class);
		when(mockLewisham.getAreaId()).thenReturn(6275153l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.comparetheparser.BasicAreaMethods#findAreas()
	 */
	@Override
	@SuppressWarnings("deprecation")
	@Test
	public void findAreas() throws XPathExpressionException, ValueNotAvailable,
			NDE2Exception, ParserConfigurationException, SAXException,
			IOException {
		List<Area> al = new FindAreasMethodCall().addPostcode("SE6 4UX")
				.findAreas();
		System.out.println("BasicXPath.findAreas(): Found " + al.size()
				+ " areas.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.comparetheparser.BasicAreaMethods#getChildren()
	 */
	@Override
	@SuppressWarnings("deprecation")
	@Test
	public void getChildren() throws XPathExpressionException,
			ValueNotAvailable, ParserConfigurationException, SAXException,
			IOException, NDE2Exception {
		List<Area> children = new GetAreaChildrenMethodCall().addArea(
				mockLewisham).getAreaChildren();
		System.out.println("BasicXPath.getChildren(): Found " + children.size()
				+ " areas.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.comparetheparser.BasicAreaMethods#getParent()
	 */
	@Override
	@SuppressWarnings("deprecation")
	@Test
	public void getParent() throws XPathExpressionException, ValueNotAvailable,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception {
		Area parent = new GetAreaParentMethodCall().addArea(mockLewisham)
				.getAreaParent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.comparetheparser.BasicAreaMethods#getSubjects()
	 */
	@Override
	@SuppressWarnings("deprecation")
	@Test
	public void getSubjects() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception {
		Map<Subject, Integer> s = new GetCompatibleSubjectsMethodCall()
				.addArea(mockLewisham).getCompatibleSubjects();
		System.out.println("BasicXPath.getSubjects(): Found " + s.size()
				+ " subjects.");
	}

}
