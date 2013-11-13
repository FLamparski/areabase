package com.comparetheparser;

import static org.junit.Assert.assertEquals;

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
				.ofLevelType(Area.LEVELTYPE_LOCAL_AUTHORITY)
				.inHierarchy(Area.HIERARCHY_2011_STATISTICAL_GEOGRAPHY)
				.execute();
		// The call above should return only ONE element. If there's more,
		// complain.
		assertEquals(1, theAreas.size());

		Area theArea = null;
		Iterator<Area> aI = theAreas.iterator();

		theArea = aI.next(); /*
							 * The call to FindAreas will return only one
							 * element, so it is safe to say this.
							 */

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
