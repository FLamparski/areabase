package areabase.tests.ultimate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nde2.errors.NDE2Exception;
import nde2.pull.methodcalls.delivery.GetTables;
import nde2.pull.methodcalls.discovery.FindAreas;
import nde2.pull.methodcalls.discovery.GetDatasetFamilies;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.DataSetItem;
import nde2.pull.types.Dataset;
import nde2.pull.types.Subject;
import nde2.pull.types.Topic;

import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;

import areabase.tests.Repr;

public class PullDemographics {
	public final static String POSTCODE = "EC2R 8AH";
	public static final String[] DATASET_KEYWORDS = { "Population Density",
			"Sex", "Age by Single Year" };
	public static final String CENSUS_SUBJECT_NAME = "Census";

	@Test
	public void test() throws IOException, XmlPullParserException,
			NDE2Exception, IllegalArgumentException, IllegalAccessException {
		long startFindAreas = System.currentTimeMillis();
		Set<Area> areaSet = new FindAreas().forPostcode(POSTCODE)
				.ofLevelType(Area.LEVELTYPE_LOCAL_AUTHORITY)
				.inHierarchy(Area.HIERARCHY_2011_STATISTICAL_GEOGRAPHY)
				.execute();
		long endFindAreas = System.currentTimeMillis();
		long timeFindAreas = endFindAreas - startFindAreas;

		// It should switch to the workaround call, but you never know.
		assertEquals(1, areaSet.size());

		// This is safe by this point
		Area myArea = areaSet.iterator().next();

		Repr.repr(myArea);

		long startCompatSubjects = System.currentTimeMillis();
		Map<Subject, Integer> areaSubjects = myArea.getCompatibleSubjects();
		long endCompatSubjects = System.currentTimeMillis();
		long timeCompatSubjects = endCompatSubjects - startCompatSubjects;

		Subject censusSubject = null;
		for (Subject s : areaSubjects.keySet()) {
			Repr.repr(s);
			if (s.getName().equals(CENSUS_SUBJECT_NAME)) {
				censusSubject = s;
			}
		}

		if (censusSubject == null) {
			fail("Census not found!");
		}

		long startGetFamilies = System.currentTimeMillis();
		List<DataSetFamily> dataSetFamilies = new GetDatasetFamilies(
				censusSubject).forArea(myArea).execute();
		long endGetFamilies = System.currentTimeMillis();
		long timeGetFamilies = endGetFamilies - startGetFamilies;

		List<DataSetFamily> requiredDsFamilies = new ArrayList<>();
		for (DataSetFamily dsf : dataSetFamilies) {
			Repr.repr(dsf);
			for (String keyword : DATASET_KEYWORDS) {
				if (dsf.getName().startsWith(keyword))
					requiredDsFamilies.add(dsf);
			}
		}

		long startTables = System.currentTimeMillis();
		Set<Dataset> tableSet = new GetTables().forArea(myArea)
				.inFamilies(requiredDsFamilies).execute();
		long endTables = System.currentTimeMillis();
		long timeTables = endTables - startTables;

		System.out
				.println("Done loading tables. Total time taken: "
						+ (timeFindAreas + timeCompatSubjects + timeGetFamilies + timeTables)
						+ " ms");
		System.out.println("Finding the area: " + timeFindAreas + " ms");
		System.out.println("Fetching compatible subjects: "
				+ timeCompatSubjects + " ms");
		System.out.println("Fetching dataset families: " + timeGetFamilies
				+ " ms");
		System.out.println("Fetching the datasets: " + timeTables + " ms");

		Map<Integer, Integer> popPerYear = new HashMap<Integer, Integer>();
		int mostRecentYear = 0;
		for (Dataset ds : tableSet) {
			System.out.println("Now considering dataset " + ds.getTitle());
			if (ds.getTitle().startsWith("Sex")) {
				int year = Integer.parseInt(ds.getTitle().substring(5, 9));
				if (!(popPerYear.containsKey(year))) {
					Topic allPeople = null;
					/*
					 * exit condition: found relevant topic or no more topics
					 * left
					 */
					Iterator<Topic> iterator = ds.getTopics().values()
							.iterator();
					while (iterator.hasNext() && allPeople == null) {
						Topic t = iterator.next();
						if (t.getTitle().startsWith("All"))
							allPeople = t;
					}
					Set<DataSetItem> relevants = ds.getItems(allPeople);
					System.out
							.println("Size of relevants: " + relevants.size());
					popPerYear.put(year, (int) relevants.iterator().next()
							.getValue());
				}
				if (year > mostRecentYear)
					mostRecentYear = year;
			}
		}
		for (Entry<Integer, Integer> singleEntry : popPerYear.entrySet()) {
			System.err.println(String.format("In %d, %d people lived in %s.",
					singleEntry.getKey(), singleEntry.getValue(),
					myArea.getName()));
		}
		float avgPercentageChange = 0;

		/*
		 * We need a sorted collection here
		 */
		Set<Integer> years_set = popPerYear.keySet();
		List<Integer> years = new ArrayList<Integer>(years_set.size());
		for (Integer y : years_set) {
			years.add(y);
		}
		Collections.sort(years);

		int previous_year = 0;
		for (Integer current_year : years) {
			if (previous_year == 0) {
				previous_year = current_year;
				continue;
			}
			int oldPop = popPerYear.get(previous_year);
			int newPop = popPerYear.get(current_year);
			float percentageChange = ((float) newPop - (float) oldPop)
					/ (float) oldPop;
			System.err
					.println(String
							.format("Percentage difference between %d (in %d) and %d (in %d) = %f",
									oldPop, previous_year, newPop,
									current_year, percentageChange * 100f));
			if (avgPercentageChange == 0f) {
				avgPercentageChange = percentageChange;
				System.out.println("Set new avgPercentageChange = "
						+ avgPercentageChange);
			} else {
				avgPercentageChange = (avgPercentageChange + percentageChange) / 2;
			}
		}
		int year = 0;
		float density = 0f;
		for (Dataset ds : tableSet) {
			if (ds.getTitle().startsWith("Population Density")) {
				int cyr = Integer.parseInt(ds.getTitle().substring(20, 24));
				if (year < cyr) {
					year = cyr;
					Topic pdTopic = null;
					Iterator<Topic> iterator = ds.getTopics().values()
							.iterator();
					while (iterator.hasNext() && pdTopic == null) {
						Topic t = iterator.next();
						if (t.getTitle().startsWith("Density")) {
							pdTopic = t;
						}
					}
					density = ds.getItems(pdTopic).iterator().next().getValue();
				}
			}
		}
		System.err.println(String.format(
				"The population density in %s is %f p/sq. km.",
				myArea.getName(), density * 0.01f));
		float ratio = 0f;
		year = 0;
		for (Dataset ds : tableSet) {
			if (ds.getTitle().startsWith("Sex")) {
				int males = 0, females = 0;
				int cyr = Integer.parseInt(ds.getTitle().substring(5, 9));
				if (year < cyr) {
					year = cyr;
					Iterator<Topic> iterator = ds.getTopics().values()
							.iterator();
					while (iterator.hasNext()) {
						Topic t = iterator.next();
						if (t.getTitle().startsWith("Males")) {
							males = (int) ds.getItems(t).iterator().next()
									.getValue();
						} else if (t.getTitle().startsWith("Females")) {
							females = (int) ds.getItems(t).iterator().next()
									.getValue();
						}
					}

				}
				ratio = ((float) males) / ((float) females);

			}
		}
		System.err.println(String.format(
				"The male-to-female ratio in %s in %d was %f.",
				myArea.getName(), year, ratio));
		float avgAge = 0f;
		final Pattern REGEX_AGE = Pattern.compile("Age( Under)? (\\d+)");
		year = 0;
		for (Dataset ds : tableSet) {

			if (ds.getTitle().startsWith("Age by Single Year")) {
				int cyr = Integer.parseInt(ds.getTitle().substring(20, 24));

				if (year < cyr) {
					year = cyr;
					float rollingAge = 0;
					float rollingPopulation = 0;
					for (Topic t : ds.getTopics().values()) {
						Matcher titleMatcher = REGEX_AGE.matcher(t.getTitle());
						if (titleMatcher.matches()) {
							float curAge = Float.parseFloat(titleMatcher
									.group(2));
							float curPop = ds.getItems(t).iterator().next()
									.getValue();
							rollingAge += curAge * curPop;
							rollingPopulation += curPop;
						}
					}
					avgAge = rollingAge / rollingPopulation;
				}
			}
		}
		System.err.println(String.format("The average age in %s in %s was %f",
				myArea.getName(), year, avgAge));
	}
}
