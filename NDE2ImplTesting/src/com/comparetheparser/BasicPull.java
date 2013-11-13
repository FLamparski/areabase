package com.comparetheparser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;

import nde2.pull.methodcalls.discovery.FindAreas;
import nde2.pull.methodcalls.discovery.GetAreaChildren;
import nde2.pull.methodcalls.discovery.GetAreaParent;
import nde2.pull.methodcalls.discovery.GetCompatibleSubjects;
import nde2.pull.types.Area;
import nde2.pull.types.Subject;

import org.junit.BeforeClass;
import org.junit.Test;

public class BasicPull implements BasicAreaMethods {

	private static Area mockLewisham;

	@BeforeClass
	public static void setupBefore() {
		mockLewisham = mock(Area.class);
		when(mockLewisham.getAreaId()).thenReturn(6275153);
	}

	@Override
	@Test
	public void findAreas() throws Exception {
		Set<Area> as = new FindAreas().forPostcode("SE6 4UX").execute();
		System.out.println("BasicPull.findAreas(): Found " + as.size()
				+ " areas.");
	}

	@Override
	@Test
	public void getChildren() throws Exception {
		Set<Area> as = new GetAreaChildren(mockLewisham).execute();
		System.out.println("BasicPull.getChildren(): Found " + as.size()
				+ " areas.");
	}

	@Override
	@Test
	public void getParent() throws Exception {
		Area parent = new GetAreaParent(mockLewisham).execute();
	}

	@Override
	@Test
	public void getSubjects() throws Exception {
		Map<Subject, Integer> cs = new GetCompatibleSubjects(mockLewisham)
				.execute();
		System.out.println("BasicPull.getSubjects(): Found " + cs.size()
				+ " subjects.");
	}

}
