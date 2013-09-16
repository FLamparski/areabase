package areabase.tests;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.delivery.GetTablesMethodCall;
import nde2.methodcalls.discovery.FindAreasMethodCall;
import nde2.types.delivery.DataSetItem;
import nde2.types.delivery.Dataset;
import nde2.types.delivery.Topic;
import nde2.types.discovery.Area;
import nde2.types.discovery.DataSetFamily;
import nde2.types.discovery.Subject;

import org.xml.sax.SAXException;

import areabase.tests.ultimate.DemographicsTest;
import areabase.tests.ultimate.DemographicsTest.TrendDescription;

public class CheckAllSortsOfData {

	/**
	 * @param args
	 * @throws NDE2Exception
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 * @throws ParseException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void main(String[] args) throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, ParseException, IllegalArgumentException,
			IllegalAccessException {
		Area bankArea;
		try {
			bankArea = new FindAreasMethodCall()
					.addPostcode(DemographicsTest.POSTCODE).findAreas().get(2);
		} catch (ValueNotAvailable e) {
			System.err.println("This area is not available.");
			e.printStackTrace();
			return;
		}
		Subject censusSubject = DemographicsTest.findCensusSubject(bankArea);
		List<DataSetFamily> requiredFamilies;
		try {
			requiredFamilies = DemographicsTest.findRequiredFamilies(bankArea,
					censusSubject);
		} catch (ValueNotAvailable e) {
			System.err.println("Required families not available.");
			e.printStackTrace();
			return;
		}

		printFamilies(requiredFamilies);

		List<Area> la = new ArrayList<>();
		la.add(bankArea);

		List<Dataset> theDatasets = new GetTablesMethodCall()
				.addDatasetFamilies(requiredFamilies).addAreas(la).getTables();

		TrendDescription desc;
		try {
			desc = calculatePopulationTrend(theDatasets);
		} catch (ValueNotAvailable e) {
			e.printStackTrace();
			System.err.println("Value not available");
			return;
		}
		Repr.repr(desc);
	}

	private static void printFamilies(List<DataSetFamily> requiredFamilies) {
		System.out.println("\n\n");
		System.out.println("Families: ");
		for (DataSetFamily dsfam : requiredFamilies) {
			System.out.println(String.format("%d\t%s", dsfam.getFamilyId(),
					dsfam.getName()));
		}
		System.out.println("\n\n");
	}

	private static TrendDescription calculatePopulationTrend(
			List<Dataset> theDatasets) throws ValueNotAvailable {
		TrendDescription desc = new TrendDescription();
		int mostRecentYear = 0;
		/**
		 * where key is year and value is the population
		 */
		Map<Integer, Integer> popPerYear = new HashMap<>();
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

		if (avgPercentageChange > DemographicsTest.POP_STABLE_LOWER_THRESHOLD
				&& avgPercentageChange < DemographicsTest.POP_STABLE_UPPER_THRESHOLD) {
			desc.which = TrendDescription.STABLE;
		} else if (avgPercentageChange <= DemographicsTest.POP_STABLE_LOWER_THRESHOLD
				&& avgPercentageChange > DemographicsTest.POP_RAPID_LOWER_THRESHOLD) {
			desc.which = TrendDescription.FALLING;
		} else if (avgPercentageChange <= DemographicsTest.POP_RAPID_LOWER_THRESHOLD) {
			desc.which = TrendDescription.FALLING_RAPIDLY;
		} else if (avgPercentageChange >= DemographicsTest.POP_STABLE_UPPER_THRESHOLD
				&& avgPercentageChange < DemographicsTest.POP_RAPID_UPPER_THRESHOLD) {
			desc.which = TrendDescription.RISING;
		} else {
			desc.which = TrendDescription.RISING_RAPIDLY;
		}

		return desc;
	}

}
