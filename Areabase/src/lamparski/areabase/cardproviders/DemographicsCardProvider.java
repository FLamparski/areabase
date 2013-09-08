package lamparski.areabase.cardproviders;

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

import lamparski.areabase.R;
import lamparski.areabase.cards.PlayCard;
import lamparski.areabase.map_support.HoloCSSColourValues;
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

	private final static float POP_STABLE_LOWER_THRESHOLD = -0.05f;
	private final static float POP_STABLE_UPPER_THRESHOLD = 0.05f;
	private final static float POP_RAPID_UPPER_THRESHOLD = 0.3f;
	private final static float POP_RAPID_LOWER_THRESHOLD = -0.3f;

	private final static float DENS_VDENSE_LOWER_THRESHOLD = 3844f;
	private final static float DENS_DENSE_LOWER_THRESHOLD = 1286f;
	private final static float DENS_NORMAL_LOWER_THRESHOLD = 360f;
	private final static float DENS_SPARSE_LOWER_THRESHOLD = 134f;

	private final static float GENDER_MORE_MALES_LOWER_THRESHOLD = 1.03f;
	private final static float GENDER_MORE_FEMALES_UPPER_THRESHOLD = 0.97f;
	private final static float GENDER_MORE_FEMALES_LOWER_THRESHOLD = 0.8f;
	private final static float GENDER_MORE_MALES_UPPER_THRESHOLD = 1.2f;

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
		int whichPopDensityDescriptor = popDensityDescriptor(getPopulationDensity(theDatasets));
		int whichGenderRatioDescriptor = genderRatioDescriptor(calculateGenderRatio(theDatasets));
		float avgAge = calculateAverageAge(theDatasets);

		String card_title = res
				.getString(
						R.string.card_demographics_title_base,
						area.getName(),
						res.getStringArray(R.array.card_demographics_pop_size_trend_descriptors)[popSizeTrendDesc.which + 2]);
		String card_description = res
				.getString(
						R.string.card_demographics_body_base,
						res.getStringArray(R.array.card_demographics_sex_dominance_descriptors)[whichGenderRatioDescriptor],
						avgAge,
						res.getStringArray(R.array.card_demographics_pop_density_descriptors)[whichPopDensityDescriptor]);

		return makeCard(card_title, card_description);
	}

	private static CardModel makeCard(String card_title, String card_description) {
		return new CardModel(card_title, card_description,
				HoloCSSColourValues.AQUAMARINE.getCssValue(),
				HoloCSSColourValues.AQUAMARINE.getCssValue(), false, false,
				PlayCard.class);
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
		final Pattern REGEX_AGE = Pattern.compile("aged (\\d+) years?");
		for (Dataset ds : theDatasets) {
			if (ds.getTitle().startsWith("Age by Single Year")) {
				int year = 0;

				int cyr = Integer.parseInt(ds.getTitle().substring(20, 25));
				if (year < cyr) {
					float rollingAge = 0;
					float rollingPopulation = 0;
					for (Topic t : ds.getTopics().values()) {
						Matcher titleMatcher = REGEX_AGE.matcher(t.getTitle());
						if (titleMatcher.matches()) {
							float curAge = Float.parseFloat(titleMatcher
									.group(1));
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

	private static int genderRatioDescriptor(float genderRatio) {
		if (genderRatio >= GENDER_MORE_FEMALES_UPPER_THRESHOLD
				&& genderRatio <= GENDER_MORE_MALES_LOWER_THRESHOLD) {
			return 2; // balance
		} else if (genderRatio < GENDER_MORE_FEMALES_UPPER_THRESHOLD
				&& genderRatio >= GENDER_MORE_FEMALES_LOWER_THRESHOLD) {
			return 1; // more females
		} else if (genderRatio < GENDER_MORE_FEMALES_LOWER_THRESHOLD) {
			return 0; // significantly more females
		} else if (genderRatio > GENDER_MORE_MALES_UPPER_THRESHOLD) {
			return 4; // significantly more males
		} else {
			return 3; // more males
		}
	}

	private static float calculateGenderRatio(List<Dataset> theDatasets)
			throws ValueNotAvailable {
		float ratio = 0f;
		for (Dataset ds : theDatasets) {
			if (ds.getTitle().startsWith("Sex")) {
				int males = 0, females = 0, year = 0;
				int cyr = Integer.parseInt(ds.getTitle().substring(5, 10));
				if (year < cyr) {

					while (ds.getTopics().values().iterator().hasNext()) {
						Topic t = ds.getTopics().values().iterator().next();
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

	private static int popDensityDescriptor(float populationDensity) {
		if (populationDensity >= DENS_VDENSE_LOWER_THRESHOLD) {
			return 4; // very dense
		} else if (populationDensity >= DENS_DENSE_LOWER_THRESHOLD) {
			return 3; // dense
		} else if (populationDensity >= DENS_NORMAL_LOWER_THRESHOLD) {
			return 2; // normal
		} else if (populationDensity >= DENS_SPARSE_LOWER_THRESHOLD) {
			return 1; // sparse
		}
		return 0; // very sparse
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
		Iterator<Subject> subjectIter = areaSubjects.keySet().iterator();
		while (subjectIter.hasNext() && censusSubject == null) {
			Subject s = subjectIter.next();
			if (s.getName().equals("Census")) {
				censusSubject = s;
				Log.i("DemographicsCardProvider",
						"Found subject Census for Area " + area.getName()
								+ "; contains " + areaSubjects.get(s)
								+ " elements; id = " + s.getId());
			} else {
				Log.d("DemographicsCardProvider", "Subject " + s.getName()
						+ " is not Census.");
			}
		}

		return censusSubject;
	}

	private static float personPerHectareToPersonPerSqKm(float pph) {
		return (float) (pph * 0.01);
	}
}
