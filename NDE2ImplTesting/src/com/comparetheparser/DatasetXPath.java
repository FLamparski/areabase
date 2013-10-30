package com.comparetheparser;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.discovery.FindAreasMethodCall;
import nde2.methodcalls.discovery.GetDatasetsMethodCall;
import nde2.types.discovery.Area;
import nde2.types.discovery.DataSetFamily;
import nde2.types.discovery.Subject;

import org.junit.Test;
import org.xml.sax.SAXException;

public class DatasetXPath implements DatasetFamiliesWorkflow {

	@SuppressWarnings("deprecation")
	@Override
	@Test
	public void crimeForMyArea() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, ValueNotAvailable, ParseException {
		List<Area> theAreas = new FindAreasMethodCall().addPostcode("SE6 4UX")
				.findAreas();
		Area theArea = theAreas.get(2);
		Map<Subject, Integer> areaSubjects = theArea.getCompatibleSubjects();
		Set<Subject> subjectSet = areaSubjects.keySet();

		Subject c = null;
		for (Subject s : subjectSet) {
			if (s.getName().equals("Crime and Safety")) {
				c = s;
			}
		}

		List<DataSetFamily> datasets = new GetDatasetsMethodCall()
				.addArea(theArea).addSubject(c).getDatasets();

		System.out.println("DatasetXPath.crimeForMyArea(): Found "
				+ datasets.size() + " dataset families.");
	}

}
