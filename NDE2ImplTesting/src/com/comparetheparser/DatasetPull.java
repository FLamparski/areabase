package com.comparetheparser;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nde2.pull.methodcalls.discovery.FindAreas;
import nde2.pull.methodcalls.discovery.GetDatasetFamilies;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.Subject;

import org.junit.Test;

public class DatasetPull implements DatasetFamiliesWorkflow {

	@Override
	@Test
	public void crimeForMyArea() throws Exception {
		Set<Area> theAreas = new FindAreas().forPostcode("SE6 4UX")
				.ofLevelType(Area.LEVELTYPE_LA)
				.inHierarchy(Area.HIERARCHY_2011_STATISTICAL_GEOGRAPHY)
				.execute();

		Area theArea = null;
		Iterator<Area> aI = theAreas.iterator();
		while (theArea == null) {
			Area a = aI.next();
			if (a.getHierarchyId() == Area.HIERARCHY_2011_STATISTICAL_GEOGRAPHY)
				/*
				 * Wait a second, didn't I just say that? Yes, in line 23. The
				 * problem is that for some fked up reason, ONS also returns an
				 * Area from hierarchy 2. They have different IDs, too.
				 */
				theArea = a;
		}

		Map<Subject, Integer> subjectListing = theArea.getCompatibleSubjects();
		Set<Subject> subjectSet = subjectListing.keySet();
		Subject c = null;
		for (Subject s : subjectSet) {
			if (s.getName().equals("Crime and Safety"))
				c = s;
		}

		List<DataSetFamily> dsFamilies = new GetDatasetFamilies(c).forArea(
				theArea).execute();

		System.out.println("DatasetPull.crimeForMyArea(): Found "
				+ dsFamilies.size() + " datasets.");
	}

}
