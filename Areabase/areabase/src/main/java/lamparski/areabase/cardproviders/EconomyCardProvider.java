package lamparski.areabase.cardproviders;

import android.content.res.Resources;

import com.fima.cardsui.objects.CardModel;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import lamparski.areabase.R;
import lamparski.areabase.cards.PlayCard;
import lamparski.areabase.map_support.HoloCSSColourValues;
import nde2.errors.InvalidParameterException;
import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.helpers.Statistics;
import nde2.pull.methodcalls.delivery.GetTables;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.DataSetItem;
import nde2.pull.types.Dataset;
import nde2.pull.types.DateRange;
import nde2.pull.types.Subject;
import nde2.pull.types.Topic;

import static nde2.helpers.CensusHelpers.findRequiredFamilies;
import static nde2.helpers.CensusHelpers.findSubject;

public class EconomyCardProvider {
	public static final String[] CENSUS_KEYWORDS = { "KS601EW", "QS605EW" };
	public static final String[] ECONOMY_KEYWORDS = { "Worklessness: Economic Activity" };

	private static final int WORKLESSNESS_DATASET_ID = 2212;
    private static final long UNIX_30_DAYS = 1000l * 60 * 60 * 24 * 30;

	/**
	 * Generates a summary of economic data for the supplied area.
	 * @param area The area to query.
	 * @param res Used to build the text on the card.
	 * @return A card with the summary of the economic activity within the area.
	 * @throws InvalidParameterException
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws NDE2Exception
	 */
	public static CardModel economyCardForArea(Area area, Resources res)
			throws InvalidParameterException, IOException,
			XmlPullParserException, NDE2Exception {

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
		TrendDescription unemploymentDescription = getUnemploymentRate(area);
		float unempl_val = unemploymentDescription.currentValue;
        String unempl_trend = res.getStringArray(R.array.card_economy_unemployment_trend)[unemploymentDescription.which + 2];

		String card_title = res.getString(R.string.card_economy_title,
				area.getName());
		String card_body = res.getString(R.string.card_economy_body_base,
				area.getName(), type_of_economic_activity, biggest_sector,
				unempl_val, unempl_trend);
		return makeCard(card_title, card_body);
	}

    public static TrendDescription getUnemploymentRate(Area area) throws XmlPullParserException, IOException, NDE2Exception {
        TrendDescription unemploymentTrendDescription = new TrendDescription();

        Subject econSubject = findSubject(area, "Economic Deprivation");
        List<DataSetFamily> dataSetFamilies = findRequiredFamilies(area, econSubject, ECONOMY_KEYWORDS);
        DataSetFamily worklessnessFamily = null;
        for(DataSetFamily f : dataSetFamilies){
            if(f.getFamilyId() == WORKLESSNESS_DATASET_ID){
                worklessnessFamily = f;
            }
        }

        DateRange[] periods = worklessnessFamily.getDateRanges();
        DateRange mostRecent = DateRange.mostRecent(periods);
        Map<DateRange, Dataset> datasets = new HashMap<DateRange, Dataset>();
        for(DateRange p : periods){
            Set<Dataset> ds = new GetTables().forArea(area).inFamily(worklessnessFamily).inDateRange(p).execute();
            datasets.put(p, ds.iterator().next());
        }

        float currentLevel = 0f;
        Map<Integer, Float> datapoints = new HashMap<Integer, Float>();
        for(Entry<DateRange, Dataset> entry : datasets.entrySet()){
            DateRange drkey = entry.getKey();
            int intkey = (int) (drkey.getEndDate().getTime() / (UNIX_30_DAYS * 4l));
            Dataset daval = entry.getValue();
            float fval = 0f;
            for(Topic t : daval.getTopics().values()){
                if(t.getTitle().contains("Unemployment Rate; Aged 16-64")){
                    DataSetItem item = null;
                    try{
                        item = daval.getItems(t).iterator().next();
                    } catch (NoSuchElementException e){
                        /*
                        This can occur when there is no data for the given point in time, in which
                        case skip to next data point.
                         */
                        continue;
                    }
                    fval = item.getValue();
                    if(mostRecent.equals(drkey)){
                        currentLevel = fval;
                    }
                    datapoints.put(intkey, fval);
                }
            }
        }

        double ols = Statistics.linearRegressionGradient(datapoints);

        if(ols <= -0.75){
            unemploymentTrendDescription.which = TrendDescription.FALLING_RAPIDLY;
        } else if(ols <= -0.05){
            unemploymentTrendDescription.which = TrendDescription.FALLING;
        } else if(ols <= 0.05){
            unemploymentTrendDescription.which = TrendDescription.STABLE;
        } else if(ols <= 0.75){
            unemploymentTrendDescription.which = TrendDescription.RISING;
        } else {
            unemploymentTrendDescription.which = TrendDescription.RISING_RAPIDLY;
        }
        unemploymentTrendDescription.currentValue = currentLevel;

        return unemploymentTrendDescription;
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
