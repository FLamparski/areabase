package areabase.tests.ultimate;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.delivery.GetTablesMethodCall;
import nde2.methodcalls.discovery.FindAreasMethodCall;
import nde2.methodcalls.discovery.GetDatasetsMethodCall;
import nde2.types.delivery.DataSetItem;
import nde2.types.delivery.Dataset;
import nde2.types.delivery.Topic;
import nde2.types.discovery.Area;
import nde2.types.discovery.DataSetFamily;
import nde2.types.discovery.Subject;

import org.junit.Test;
import org.xml.sax.SAXException;

public class DemographicsTest {
	public final static String POSTCODE = "EC2R 8AH";
	public static final String[] DATASET_KEYWORDS = { "Population Density",
			"Sex", "Age Structure", "Age by Single Year" };

	public final static float POP_STABLE_LOWER_THRESHOLD = -0.005f;
	public final static float POP_STABLE_UPPER_THRESHOLD = 0.005f;
	public final static float POP_RAPID_UPPER_THRESHOLD = 0.3f;
	public final static float POP_RAPID_LOWER_THRESHOLD = -0.3f;

	public static class TrendDescription {
		public static final int FALLING_RAPIDLY = -2;
		public static final int FALLING = -1;
		public static final int STABLE = 0;
		public static final int RISING = 1;
		public static final int RISING_RAPIDLY = 2;

		public int which;
		public float currentValue;
	}

	@Test
	public void test() throws XPathExpressionException, NDE2Exception,
			ParserConfigurationException, SAXException, IOException,
			ParseException {
		System.out.println("START: We are in area close to postcode "
				+ POSTCODE + " and looking for Census datasets: "
				+ DATASET_KEYWORDS.toString());

		long startFindAreasMethodCall = System.currentTimeMillis();
		Area bankArea;
		try {
			bankArea = new FindAreasMethodCall().addPostcode(POSTCODE)
					.findAreas().get(2);
		} catch (ValueNotAvailable e) {
			System.err.println("This area is not available.");
			e.printStackTrace();
			fail();
			return;
		}
		long endFindAreasMethodCall = System.currentTimeMillis();

		long timeFindAreasMethodCall = endFindAreasMethodCall
				- startFindAreasMethodCall;

		long startFindCensusSubject = System.currentTimeMillis();
		Subject censusSubject = findCensusSubject(bankArea);
		long endFindCensusSubject = System.currentTimeMillis();

		long timeFindCensusSubject = endFindCensusSubject
				- startFindCensusSubject;

		long startFindRequiredFamilies = System.currentTimeMillis();
		List<DataSetFamily> requiredFamilies;
		try {
			requiredFamilies = findRequiredFamilies(bankArea, censusSubject);
		} catch (ValueNotAvailable e) {
			System.err.println("Required families not available.");
			e.printStackTrace();
			fail();
			return;
		}
		long endFindRequiredFamilies = System.currentTimeMillis();

		long timeFindRequiredFamilies = endFindRequiredFamilies
				- startFindRequiredFamilies;

		System.out.println("DISCOVERY: We are in an area called "
				+ bankArea.getName() + " which has an ID of "
				+ bankArea.getAreaId()
				+ ". The Census subject for this area has ID of "
				+ censusSubject.getId() + ", and references "
				+ requiredFamilies.size() + " required datasets.");

		List<Area> la = new ArrayList<>();
		la.add(bankArea);

		long startFetchingDatasets = System.currentTimeMillis();
		List<Dataset> theDatasets = new GetTablesMethodCall()
				.addDatasetFamilies(requiredFamilies).addAreas(la).getTables();
		long endFetchingDatasets = System.currentTimeMillis();

		long timeFetchingDatasets = endFetchingDatasets - startFetchingDatasets;

		System.out
				.println("DELIVERY: We now have the actual datasets for this area, "
						+ theDatasets.size() + " of them.");

		System.out.println("-------------------------------------");
		long totalDiscoDeliTime = timeFetchingDatasets
				+ timeFindAreasMethodCall + timeFindCensusSubject
				+ timeFindRequiredFamilies;
		System.out.println("At this point we have taken " + totalDiscoDeliTime
				+ "ms to FETCH required data and put it into models.");
		System.out.println("Finding areas: " + timeFindAreasMethodCall);
		System.out.println("Finding census subject: " + timeFindCensusSubject);
		System.out.println("Finding required families: "
				+ timeFindRequiredFamilies);
		System.out.println("Processing datasets: " + timeFetchingDatasets);

		System.out.println(" Finding average age...");
		float avgAge = 0;
		long startAvgAge = System.currentTimeMillis();
		try {
			avgAge = calculateAverageAge(theDatasets);
		} catch (ValueNotAvailable e) {
			System.err.println(" Some values are not available. Fuck me.");
			e.printStackTrace();
		}
		long endAvgAge = System.currentTimeMillis();
		long timeAvgAge = endAvgAge - startAvgAge;
		System.out.println(" Done; avgAge = " + avgAge + "; time taken = "
				+ timeAvgAge + " ms");

		System.out.println(" Finding gender ratio...");
		float genderRatio = 0;
		long startGenderRatio = System.currentTimeMillis();
		try {
			genderRatio = calculateGenderRatio(theDatasets);
		} catch (ValueNotAvailable e) {
			System.err.println(" Some values are not available. Fuck me.");
			e.printStackTrace();
		}
		long endGenderRatio = System.currentTimeMillis();
		long timeGenderRatio = endGenderRatio - startGenderRatio;
		System.out.println(" Done; genderRatio = " + genderRatio
				+ "; time taken = " + timeGenderRatio + " ms");

		System.out.println(" Finding population density...");
		float popDensity = 0;
		long startPopDensity = System.currentTimeMillis();
		try {
			popDensity = getPopulationDensity(theDatasets);
		} catch (ValueNotAvailable e) {
			System.err.println(" Some values are not available. Fuck me.");
			e.printStackTrace();
		}
		long endPopDensity = System.currentTimeMillis();
		long timePopDensity = endPopDensity - startPopDensity;
		System.out.println(" Done; popDensity = " + popDensity
				+ "; time taken = " + timePopDensity + " ms");

		TrendDescription popTrend = new TrendDescription();
		long startPopTrend = System.currentTimeMillis();
		try {
			popTrend = calculatePopulationTrend(theDatasets);
		} catch (ValueNotAvailable e) {
			System.err.println(" Some values are not available. Fuck me.");
			e.printStackTrace();
		}
		long endPopTrend = System.currentTimeMillis();
		long timePopTrend = endPopTrend - startPopTrend;
		System.out.println(" Done; population = " + popTrend.currentValue
				+ "; trend = " + popTrend.which + "; time taken = "
				+ timeAvgAge + " ms");

		System.out.println("-------------------------------------");
		long totalProcessTime = timeAvgAge + timeGenderRatio + timePopDensity
				+ timePopTrend;
		System.out.println("At this point we have taken " + totalProcessTime
				+ " ms to fully process the datasets, on top of the "
				+ totalDiscoDeliTime + " ms, totalling");
		System.out.println("The GRAND TOTAL time is "
				+ (totalProcessTime + totalDiscoDeliTime) + " ms.");
		System.out.println("Calculating average age: " + timeAvgAge);
		System.out.println("Calculating gender ratio: " + timeGenderRatio);
		System.out.println("Calculating population density: " + timePopDensity);
		System.out.println("Calculating population trend:" + timePopTrend);
	}

	private static TrendDescription calculatePopulationTrend(
			List<Dataset> theDatasets) throws ValueNotAvailable {
		TrendDescription desc = new TrendDescription();
		int mostRecentYear = 0;
		/**
		 * where key is year and value is the population
		 */
		Map<Integer, Integer> popPerYear = new HashMap<Integer, Integer>();
		for (Dataset ds : theDatasets) {
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
					List<DataSetItem> relevants = ds.getItems(allPeople);
					popPerYear.put(year, (int) relevants.get(0).getValue());
				}
				if (year > mostRecentYear)
					mostRecentYear = year;
			}
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
			System.out
					.println(String
							.format("Percentage difference between %d (in %d) and %d (in %d) = %f",
									oldPop, previous_year, newPop,
									current_year, percentageChange));
			if (avgPercentageChange == 0f) {
				avgPercentageChange = percentageChange;
				System.out.println("Set new avgPercentageChange = "
						+ avgPercentageChange);
			} else {
				avgPercentageChange = (avgPercentageChange + percentageChange) / 2;
			}
		}

		desc.currentValue = popPerYear.get(mostRecentYear);

		if (avgPercentageChange > POP_STABLE_LOWER_THRESHOLD
				&& avgPercentageChange < POP_STABLE_UPPER_THRESHOLD) {
			desc.which = TrendDescription.STABLE;
		} else if (avgPercentageChange <= POP_STABLE_LOWER_THRESHOLD
				&& avgPercentageChange > POP_RAPID_LOWER_THRESHOLD) {
			desc.which = TrendDescription.FALLING;
		} else if (avgPercentageChange <= POP_RAPID_LOWER_THRESHOLD) {
			desc.which = TrendDescription.FALLING_RAPIDLY;
		} else if (avgPercentageChange >= POP_STABLE_UPPER_THRESHOLD
				&& avgPercentageChange < POP_RAPID_UPPER_THRESHOLD) {
			desc.which = TrendDescription.RISING;
		} else {
			desc.which = TrendDescription.RISING_RAPIDLY;
		}

		return desc;
	}

	private static float getPopulationDensity(List<Dataset> theDatasets)
			throws ValueNotAvailable {
		int year = 0;
		float density = 0;

		for (Dataset ds : theDatasets) {
			if (ds.getTitle().startsWith("Population Density")) {
				int cyr = Integer.parseInt(ds.getTitle().substring(20, 24));
				if (year < cyr) {
					Topic pdTopic = null;
					Iterator<Topic> iterator = ds.getTopics().values()
							.iterator();
					while (iterator.hasNext() && pdTopic == null) {
						Topic t = iterator.next();
						if (t.getTitle().startsWith("Density")) {
							pdTopic = t;
						}
					}

					density = ds.getItems(pdTopic).get(0).getValue();

				}
			}
		}

		return (float) (density * 0.01);
	}

	private static float calculateGenderRatio(List<Dataset> theDatasets)
			throws ValueNotAvailable {
		float ratio = 0f;
		int year = 0;
		for (Dataset ds : theDatasets) {
			if (ds.getTitle().startsWith("Sex")) {
				int males = 0, females = 0;
				int cyr = Integer.parseInt(ds.getTitle().substring(5, 9));
				if (year < cyr) {

					Iterator<Topic> iterator = ds.getTopics().values()
							.iterator();
					while (iterator.hasNext()) {
						Topic t = iterator.next();
						if (t.getTitle().startsWith("Males")) {
							males = (int) ds.getItems(t).get(0).getValue();
						} else if (t.getTitle().startsWith("Females")) {
							females = (int) ds.getItems(t).get(0).getValue();
						}
					}

				}
				ratio = ((float) males) / ((float) females);

			}
		}
		return ratio;
	}

	private static float calculateAverageAge(List<Dataset> theDatasets)
			throws ValueNotAvailable {
		/*
		 * The "Age by Single Year, 2011" dataset contains almost everything
		 * that I might need to calculate the average age. Nevertheless, it
		 * actually lists the *number of people who happened to be n years old*
		 * at the census, and 0 <= n <= 99 and a column for ages 100 <= x < 115.
		 * The "Age 2001" dataset SUCKS because it's weird and histogram like
		 * and messy.
		 */
		float avgAge = 0f;
		final Pattern REGEX_AGE = Pattern.compile("Age( Under)? (\\d+)");
		int year = 0;
		for (Dataset ds : theDatasets) {

			if (ds.getTitle().startsWith("Age by Single Year")) {
				int cyr = Integer.parseInt(ds.getTitle().substring(20, 24));

				if (year < cyr) {
					float rollingAge = 0;
					float rollingPopulation = 0;
					for (Topic t : ds.getTopics().values()) {
						Matcher titleMatcher = REGEX_AGE.matcher(t.getTitle());
						if (titleMatcher.matches()) {
							float curAge = Float.parseFloat(titleMatcher
									.group(2));
							float curPop = ds.getItems(t).get(0).getValue();
							rollingAge += curAge * curPop;
							rollingPopulation += curPop;
						}
					}
					avgAge = rollingAge / rollingPopulation;
				}
			}
		}
		return avgAge;
	}

	public static Subject findCensusSubject(Area area)
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, NDE2Exception {
		Subject censusSubject = null;
		Map<Subject, Integer> areaSubjects = area.getCompatibleSubjects();
		/*
		 * Loop exit condition: valid subject id is found; or there are no more
		 * subjects left.
		 */
		Iterator<Subject> subjectIter = areaSubjects.keySet().iterator();
		while (subjectIter.hasNext() && censusSubject == null) {
			Subject s = subjectIter.next();
			if (s.getName().equals("Census")) {
				censusSubject = s;
				System.out.println("Found subject Census for Area "
						+ area.getName() + "; contains " + areaSubjects.get(s)
						+ " elements; id = " + s.getId());
			} else {
				System.out
						.println("Subject " + s.getName() + " is not Census.");
			}
		}

		return censusSubject;
	}

	public static List<DataSetFamily> findRequiredFamilies(Area area,
			Subject censusSubject) throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, ParseException, ValueNotAvailable {
		List<DataSetFamily> censusDatasetFamilies = new GetDatasetsMethodCall()
				.addArea(area).addSubject(censusSubject).getDatasets();
		List<DataSetFamily> requiredFamilies = new ArrayList<DataSetFamily>();

		for (DataSetFamily family : censusDatasetFamilies) {
			for (String kw : DATASET_KEYWORDS) {
				if (family.getName().startsWith(kw))
					requiredFamilies.add(family);
			}
		}

		return requiredFamilies;
	}

}
