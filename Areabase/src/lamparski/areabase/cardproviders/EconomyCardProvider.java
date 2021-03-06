package lamparski.areabase.cardproviders;

import static nde2.helpers.CensusHelpers.findRequiredFamilies;
import static nde2.helpers.CensusHelpers.findSubject;
import static nde2.helpers.DateFormat.getYear;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import lamparski.areabase.R;
import lamparski.areabase.cards.PlayCard;
import lamparski.areabase.map_support.HoloCSSColourValues;
import nde2.errors.InvalidParameterException;
import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.pull.methodcalls.delivery.GetTables;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.DataSetItem;
import nde2.pull.types.Dataset;
import nde2.pull.types.DateRange;
import nde2.pull.types.Subject;
import nde2.pull.types.Topic;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.content.res.Resources;

import com.fima.cardsui.objects.CardModel;

public class EconomyCardProvider {
	public static final String[] CENSUS_KEYWORDS = { "KS601EW", // Economic
																// activity
			"QS605EW" // Type of economic activity
	};
	public static final String[] ECONOMY_KEYWORDS = { "Income" // Finds the
																// model median
																// income
	};

	private final static float INCOME_STABLE_LOWER_THRESHOLD = -0.01f;
	private final static float INCOME_STABLE_UPPER_THRESHOLD = 0.01f;
	private final static float INCOME_RAPID_UPPER_THRESHOLD = 0.2f;
	private final static float INCOME_RAPID_LOWER_THRESHOLD = -0.2f;
	private static final float NATIONAL_MEDIAN_WEEKLY_INCOME = 500f;

	/**
	 * Generates a summary of economic data for the supplied area.
	 * @param area The area to query.
	 * @param res Used to build the text on the card.
	 * @return A card with the summary of the economic activity within the area.
	 * @throws InvalidParameterException
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws NDE2Exception
	 * @throws ClassNotFoundException
	 */
	public static CardModel economyCardForArea(Area area, Resources res)
			throws InvalidParameterException, IOException,
			XmlPullParserException, NDE2Exception, ClassNotFoundException {

		// The data we need is split between two subjects,
		// so both need to be fetched. We'll sort that out later.
		Subject economySubject = findSubject(area, "Economic Deprivation");
		Subject censusSubject = findSubject(area, "Census");

		if (economySubject == null || censusSubject == null)
			throw new ValueNotAvailable(
					"Cannot find the required subjects for the area");

		// Coalesce the required families into a single list.
		List<DataSetFamily> requiredFamilies = findRequiredFamilies(area,
				censusSubject, CENSUS_KEYWORDS);

		Set<Dataset> theDatasets = new GetTables().forArea(area)
				.inFamilies(requiredFamilies).execute();

		String type_of_economic_activity = getTypeOfEconomicActivity(theDatasets);
		String biggest_sector = getBiggestEconomySector(theDatasets);
		TrendDescription income_trend_descriptor = calculateIncomeTrend(area,
				findRequiredFamilies(area, economySubject, ECONOMY_KEYWORDS));
		int income_descriptor = compareIncomeWithNational(income_trend_descriptor.currentValue).which;
		String avg_income_v_national = res
				.getStringArray(R.array.card_economy_compare_income_with_national)[income_descriptor + 2];
		String avg_income_trend = res
				.getStringArray(R.array.card_economy_income_trend)[income_trend_descriptor.which + 2];

		String card_title = res.getString(R.string.card_economy_title,
				area.getName());
		String card_body = res.getString(R.string.card_economy_body_base,
				area.getName(), type_of_economic_activity, biggest_sector,
				avg_income_v_national, avg_income_trend);
		return makeCard(card_title, card_body);
	}

	private static TrendDescription compareIncomeWithNational(float meanIncome) {
		TrendDescription td = new TrendDescription();
		td.currentValue = meanIncome;

		if (meanIncome <= NATIONAL_MEDIAN_WEEKLY_INCOME * 0.75)
			td.which = TrendDescription.FALLING_RAPIDLY;
		else if (meanIncome > NATIONAL_MEDIAN_WEEKLY_INCOME * 0.75
				&& meanIncome <= NATIONAL_MEDIAN_WEEKLY_INCOME * 0.99)
			td.which = TrendDescription.FALLING;
		else if (meanIncome > NATIONAL_MEDIAN_WEEKLY_INCOME * 0.99
				&& meanIncome <= NATIONAL_MEDIAN_WEEKLY_INCOME * 1.01)
			td.which = TrendDescription.STABLE;
		else if (meanIncome > NATIONAL_MEDIAN_WEEKLY_INCOME * 1.01
				&& meanIncome <= NATIONAL_MEDIAN_WEEKLY_INCOME * 1.25)
			td.which = TrendDescription.RISING;
		else
			td.which = TrendDescription.RISING_RAPIDLY;

		return td;
	}

	@SuppressLint("UseSparseArrays")
	private static TrendDescription calculateIncomeTrend(Area area,
			List<DataSetFamily> datasetFamilies)
			throws InvalidParameterException, IOException,
			XmlPullParserException, NDE2Exception {
		TrendDescription td = new TrendDescription();

		DataSetFamily dsFamily = datasetFamilies.get(0);
		DateRange[] dateRages = dsFamily.getDateRanges();
		Set<Dataset> theDatasets = new HashSet<Dataset>();
		for (DateRange period : dateRages) {
			Set<Dataset> currentDataset = new GetTables().forArea(area)
					.inFamily(dsFamily).inDateRange(period).execute();
			theDatasets.addAll(currentDataset);
		}

		/* maps the year to the mean income */
		Map<Integer, Float> yearToIncomeMap = new HashMap<Integer, Float>();

		for (Dataset ds : theDatasets) {
			for (Topic t : ds.getTopics().values()) {
				if (t.getTitle().equals(
						"Average Weekly Household Net Income Estimate")) {
					Set<DataSetItem> datapoints = ds.getItems(t);
					if (datapoints.size() < 1)
						continue; // skip this one
					DataSetItem datapoint = datapoints.iterator().next();
					int year = getYear(datapoint.getPeriod().getEndDate());
					float value = datapoint.getValue();
					yearToIncomeMap.put(year, value);
				}
			}
		}

		/* a sorted list is needed (well is it?) */
		List<Integer> years = new ArrayList<Integer>(yearToIncomeMap.keySet());
		Collections.sort(years);
		float avgPercentageChange = 0f;
		int previous_year = 0;
		for (Integer current_year : years) {
			if (previous_year == 0) {
				previous_year = current_year;
				continue;
			}
			Float oldVal = yearToIncomeMap.get(previous_year);
			Float newVal = yearToIncomeMap.get(current_year);
			float percentageChange = ((float) newVal - (float) oldVal)
					/ (float) oldVal;

			if (avgPercentageChange == 0f) {
				avgPercentageChange = percentageChange;
			} else {
				avgPercentageChange = (avgPercentageChange + percentageChange) / 2;
			}
		}

		if (avgPercentageChange > INCOME_STABLE_LOWER_THRESHOLD
				&& avgPercentageChange < INCOME_STABLE_UPPER_THRESHOLD) {
			td.which = TrendDescription.STABLE;
		} else if (avgPercentageChange <= INCOME_STABLE_LOWER_THRESHOLD
				&& avgPercentageChange > INCOME_RAPID_LOWER_THRESHOLD) {
			td.which = TrendDescription.FALLING;
		} else if (avgPercentageChange <= INCOME_RAPID_LOWER_THRESHOLD) {
			td.which = TrendDescription.FALLING_RAPIDLY;
		} else if (avgPercentageChange >= INCOME_STABLE_UPPER_THRESHOLD
				&& avgPercentageChange < INCOME_RAPID_UPPER_THRESHOLD) {
			td.which = TrendDescription.RISING;
		} else {
			td.which = TrendDescription.RISING_RAPIDLY;
		}

		td.currentValue = yearToIncomeMap.get(years.get(years.size() - 1));

		return td;
	}

	private static String getBiggestEconomySector(Set<Dataset> theDatasets) {
		String largestEconomySector = null;
		int count_lES = 0;

		for (Dataset ds : theDatasets) {
			if (ds.getTitle().endsWith("(QS605EW)")) {
				for (Topic t : ds.getTopics().values()) {
					if (!(t.getTitle().startsWith("All Usual Residents"))) {
						// ^[A-Z](,[A-Z])?(\\d+\\W+)*\\W?
						int count = (int) ds.getItems(t).iterator().next()
								.getValue();
						if (count > count_lES) {
							largestEconomySector = t.getTitle().replaceAll(
									"^[A-Z](,[A-Z])?(\\d+\\W+)*\\W?", "");
							count_lES = count;
						}
					}
				}
			}
		}
		return largestEconomySector.toLowerCase(Locale.UK);
	}

	private static String getTypeOfEconomicActivity(Set<Dataset> theDatasets) {
		// The answer variable
		String answer = null;
		// Largest type of economic activity
		String largestEconomicActivity = null;
		int count_lEA = 0;
		// Largest type of economic inactivity
		String largestEconomicInactivity = null;
		int count_lEI = 0;
		// Tally it up
		for (Dataset ds : theDatasets) {
			if (ds.getTitle().endsWith("(KS601EW)")) {
				for (Topic t : ds.getTopics().values()) {
					// Compare raw count of persons
					if (t.getTitle().startsWith("Economically Active; ")
							&& t.getCoinageUnit().equals("Count")) {
						/*
						 * Why substring and not replace: Substrings share the
						 * same base character array as their parent string,
						 * whereas upon replacing, a new array is created and
						 * data copied. I think. Thus, using substring may be
						 * faster.
						 */
						String econActivity = t.getTitle().substring(
								"Economically Active; ".length());
						int count = (int) ds.getItems(t).iterator().next()
								.getValue();
						if (count > count_lEA) {
							count_lEA = count;
							largestEconomicActivity = econActivity;
						}
					} else if (t.getTitle().startsWith(
							"Economically Inactive; ")
							&& t.getCoinageUnit().equals("Count")) {
						String econInactivity = t.getTitle().substring(
								"Economically Inactive; ".length());
						int count = (int) ds.getItems(t).iterator().next()
								.getValue();
						if (count > count_lEI) {
							count_lEI = count;
							largestEconomicInactivity = econInactivity;
						}
					}
				}
			}
		}

		if (count_lEI > count_lEA) {
			if (largestEconomicInactivity.equals("Retired"))
				answer = "retired";
			else if (largestEconomicInactivity
					.equals("Student (Including Full-Time Students)"))
				answer = "economically inactive full-time students";
			else if (largestEconomicInactivity
					.equals("Looking After Home or Family"))
				answer = "homemakers";
			else if (largestEconomicInactivity
					.equals("Long-Term Sick or Disabled"))
				answer = "disabled";
			else
				answer = "economically inactive*";
		} else {
			if (largestEconomicActivity.startsWith("Employee; ")) {
				String employeeType = largestEconomicActivity
						.substring("Employee; ".length());
				if (employeeType.equals("Part-Time"))
					answer = "part-time employees";
				else if (employeeType.equals("Full-Time"))
					answer = "full-time employees";
			} else if (largestEconomicActivity.equals("Self-Employed"))
				answer = "self-employed";
			else if (largestEconomicActivity.equals("Unemployed"))
				answer = "unemployed";
			else if (largestEconomicActivity.equals("Full-Time Student"))
				answer = "full-time students";
			else
				answer = "economically active*";
		}

		return answer;
	}

	private static CardModel makeCard(String card_title, String card_description) {
		return new CardModel(card_title, card_description,
				HoloCSSColourValues.GREEN.getCssValue(),
				HoloCSSColourValues.GREEN.getCssValue(), false, false,
				PlayCard.class);
	}
}
