package lamparski.areabase.cardproviders;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import lamparski.areabase.R;
import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.delivery.GetTablesMethodCall;
import nde2.methodcalls.discovery.GetDatasetsMethodCall;
import nde2.types.delivery.DataSetItem;
import nde2.types.delivery.Dataset;
import nde2.types.delivery.Topic;
import nde2.types.discovery.Area;
import nde2.types.discovery.DataSetFamily;
import nde2.types.discovery.Subject;

import org.xml.sax.SAXException;

import android.content.res.Resources;
import android.util.Log;

import com.fima.cardsui.objects.CardModel;

public class DemographicsCardProvider {
	public static final String[] DATASET_KEYWORDS = { "Population Density",
			"Sex", "Age Structure" };

	private static float POP_STABLE_LOWER_THRESHOLD = -0.05f;
	private static float POP_STABLE_UPPER_THRESHOLD = 0.05f;
	private static float POP_RAPID_UPPER_THRESHOLD = 0.3f;
	private static float POP_RAPID_LOWER_THRESHOLD = -0.3f;

	public static CardModel demographicsCardForArea(Area area, Resources res)
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, NDE2Exception, ValueNotAvailable,
			ParseException {
		/*
		 * Step 1: Demographics data is located under Census category. We need
		 * to retrieve it and then get the necessary datasets.
		 */
		Subject censusSubject = findCensusSubject(area);

		if (censusSubject == null)
			throw new ValueNotAvailable(
					"Cannot find Census subject for this area.");

		List<DataSetFamily> requiredFamilies = findRequiredFamilies(area,
				censusSubject);

		List<Area> areaList = new ArrayList<Area>();
		areaList.add(area);

		List<Dataset> theDatasets = new GetTablesMethodCall()
				.addDatasetFamilies(requiredFamilies).addAreas(areaList)
				.getTables();

		TrendDescription popSizeTrendDesc = calculatePopulationTrend(theDatasets);
		float popDensity = getPopulationDensity(theDatasets);

		String card_title = res
				.getString(
						R.string.card_demographics_title_base,
						area.getName(),
						res.getStringArray(R.array.card_demographics_pop_size_trend_descriptors)[popSizeTrendDesc.which + 2]);

		return null;
	}

	private static float getPopulationDensity(List<Dataset> theDatasets)
			throws ValueNotAvailable {
		int year = 0;
		float density = 0;

		for (Dataset ds : theDatasets) {
			if (ds.getTitle().startsWith("Population Density")) {
				int cyr = Integer.parseInt(ds.getTitle().substring(20, 25));
				if (year < cyr) {
					Topic pdTopic = null;
					while (ds.getTopics().values().iterator().hasNext()
							&& pdTopic == null) {
						Topic t = ds.getTopics().values().iterator().next();
						if (t.getTitle().startsWith("Density")) {
							pdTopic = t;
						}
					}
					density = ds.getItems(pdTopic).get(0).getValue();
				}
			}
		}

		return personPerHectareToPersonPerSqKm(density);
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
			if (ds.getTitle().startsWith("Sex")) {
				int year = Integer.parseInt(ds.getTitle().substring(5, 10));
				if (popPerYear.containsKey(year)) {
					Topic allPeople = null;
					/*
					 * exit condition: found relevant topic or no more topics
					 * left
					 */
					while (ds.getTopics().values().iterator().hasNext()
							&& allPeople == null) {
						Topic t = ds.getTopics().values().iterator().next();
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
			float percentageChange = (newPop - oldPop) / oldPop;
			if (avgPercentageChange == 0f) {
				avgPercentageChange = percentageChange;
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

	private static List<DataSetFamily> findRequiredFamilies(Area area,
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

	private static Subject findCensusSubject(Area area)
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, NDE2Exception {
		Subject censusSubject = null;
		Map<Subject, Integer> areaSubjects = area.getCompatibleSubjects();
		/*
		 * Loop exit condition: valid subject id is found; or there are no more
		 * subjects left.
		 */
		while (areaSubjects.keySet().iterator().hasNext()
				&& censusSubject == null) {
			Subject s = areaSubjects.keySet().iterator().next();
			if (s.getName().equals("Census")) {
				censusSubject = s;
				Log.d("DemographicsCardProvider",
						"Found subject Census for Area " + area.getName()
								+ "; contains " + areaSubjects.get(s)
								+ " elements; id = " + s.getId());
			}
		}

		return censusSubject;
	}

	private static float personPerHectareToPersonPerSqKm(float pph) {
		return (float) (pph * 0.01);
	}
}
