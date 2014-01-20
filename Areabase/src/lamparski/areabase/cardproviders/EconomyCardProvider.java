package lamparski.areabase.cardproviders;

import static nde2.helpers.CensusHelpers.findRequiredFamilies;
import static nde2.helpers.CensusHelpers.findSubject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lamparski.areabase.R;
import lamparski.areabase.cards.PlayCard;
import lamparski.areabase.map_support.HoloCSSColourValues;
import nde2.errors.InvalidParameterException;
import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.pull.methodcalls.delivery.GetTables;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.Dataset;
import nde2.pull.types.Subject;
import nde2.pull.types.Topic;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;
import android.util.Log;

import com.fima.cardsui.objects.CardModel;

public class EconomyCardProvider {
	public static final String[] CENSUS_KEYWORDS = {
			"KS601EW", // Economic activity
			"QS605EW" // Type of economic activity
			};
	public static final String[] ECONOMY_KEYWORDS = {
			"Income" // Finds the model median income
			};
		
	public static CardModel economyCardForArea(Area area, Resources res)
			throws InvalidParameterException, IOException,
			XmlPullParserException, NDE2Exception, ClassNotFoundException {
		
		// The data we need is split between two subjects,
		// so both need to be fetched. We'll sort that out later.
		Subject economySubject = findSubject(area, "Economic Deprivation");
		Subject censusSubject = findSubject(area, "Census");
		
		if(economySubject == null || censusSubject == null)
			throw new ValueNotAvailable("Cannot find the required subjects for the area");
		
		// Coalesce the required families into a single list.
		List<DataSetFamily> requiredFamilies = findRequiredFamilies(area, censusSubject, CENSUS_KEYWORDS);
		requiredFamilies.addAll(findRequiredFamilies(area, economySubject, ECONOMY_KEYWORDS));
		
		Set<Dataset> theDatasets = new GetTables().forArea(area).inFamilies(requiredFamilies).execute();
		
		String type_of_economic_activity = getTypeOfEconomicActivity(theDatasets);
		String biggest_sector = getBiggestEconomySector(theDatasets);
		int income_descriptor = compareIncomeWithNational(theDatasets);
		String avg_income_v_national = res.getStringArray(R.array.card_economy_compare_income_with_national)[income_descriptor + 2];
		String avg_income_trend = res.getStringArray(R.array.card_economy_income_trend)[2];
		
		String card_title = res.getString(R.string.card_economy_title, area.getName());
		String card_body = res.getString(R.string.card_economy_body_base, area.getName(),
				type_of_economic_activity,
				biggest_sector,
				avg_income_v_national,
				avg_income_trend);
		return makeCard(card_title, card_body);
	}
	
	private static int compareIncomeWithNational(Set<Dataset> theDatasets) {
		TrendDescription td = new TrendDescription();
		
		for(Dataset ds : theDatasets){
			// search for the average, then compare it to a constant value
			// it's bad but will do for now.
		}
		
		return td.which;
	}

	private static String getBiggestEconomySector(Set<Dataset> theDatasets) {
		String largestEconomySector = null;
		int count_lES = 0;
		
		for(Dataset ds : theDatasets){
			if(ds.getTitle().endsWith("(QS605EW)")){
				for(Topic t : ds.getTopics().values()){
					if(!(t.getTitle().startsWith("All Usual Residents"))){
						// ^[A-Z](,[A-Z])?(\\d+\\W+)*\\W?
						int count = (int) ds.getItems(t).iterator().next().getValue();
						if(count > count_lES){
							largestEconomySector = t.getTitle().replaceAll("^[A-Z](,[A-Z])?(\\d+\\W+)*\\W?", "");
							count_lES = count;
						}
					}
				}
			}
		}
		return largestEconomySector.toLowerCase(Locale.UK);
	}

	private static String getTypeOfEconomicActivity(
			Set<Dataset> theDatasets) {
		// The answer variable
		String answer = null;
		// Largest type of economic activity
		String largestEconomicActivity = null;
		int count_lEA = 0;
		// Largest type of economic inactivity
		String largestEconomicInactivity = null;
		int count_lEI = 0;
		// Tally it up
		for(Dataset ds : theDatasets){
			if(ds.getTitle().endsWith("(KS601EW)")){
				for(Topic t : ds.getTopics().values()){
					// Compare raw count of persons
					if(t.getTitle().startsWith("Economically Active; ") && t.getCoinageUnit().equals("Count")){
						/*
						 * Why substring and not replace:
						 * Substrings share the same base character array as their parent string,
						 * whereas upon replacing, a new array is created and data copied. I think.
						 * Thus, using substring may be faster.
						 */
						String econActivity = t.getTitle().substring("Economically Active; ".length());
						int count = (int) ds.getItems(t).iterator().next().getValue();
						if (count > count_lEA){
							count_lEA = count;
							largestEconomicActivity = econActivity;
						}
					} else if(t.getTitle().startsWith("Economically Inactive; ") && t.getCoinageUnit().equals("Count")){
						String econInactivity = t.getTitle().substring("Economically Inactive; ".length());
						int count = (int) ds.getItems(t).iterator().next().getValue();
						if (count > count_lEI){
							count_lEI = count;
							largestEconomicInactivity = econInactivity;
						}
					}
				}
			}
		}
		
		if(count_lEI > count_lEA){
			if(largestEconomicInactivity.equals("Retired"))
				answer = "retired";
			else if(largestEconomicInactivity.equals("Student (Including Full-Time Students)"))
				answer = "economically inactive full-time students";
			else if(largestEconomicInactivity.equals("Looking After Home or Family"))
				answer = "homemakers";
			else if(largestEconomicInactivity.equals("Long-Term Sick or Disabled"))
				answer = "disabled";
			else
				answer = "economically inactive*";
		} else {
			if(largestEconomicActivity.startsWith("Employee; ")){
				String employeeType = largestEconomicActivity.substring("Employee; ".length());
				if(employeeType.equals("Part-Time"))
					answer = "part-time employees";
				else if (employeeType.equals("Full-Time"))
					answer = "full-time employees";
			} 
			else if (largestEconomicActivity.equals("Self-Employed"))
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
