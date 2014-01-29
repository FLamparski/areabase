package lamparski.areabase.cardproviders;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import lamparski.areabase.R;
import lamparski.areabase.cards.PlayCard;
import lamparski.areabase.map_support.HoloCSSColourValues;
import nde2.errors.NDE2Exception;
import nde2.helpers.ArrayHelpers;
import nde2.pull.types.Area;

import org.mysociety.mapit.Mapper;
import org.xml.sax.SAXException;

import police.errors.APIException;
import police.methodcalls.CrimeAvailabilityMethodCall;
import police.methodcalls.StreetLevelCrimeMethodCall;
import police.types.Crime;
import android.content.res.Resources;

import com.fima.cardsui.objects.CardModel;

public class CrimeCardProvider {
	private static final double TREND_STABLE_UPPER_THRESHOLD = 0.1;
	private static final double TREND_STABLE_LOWER_THRESHOLD = -0.1;
	private static final double TREND_RAPID_UPPER_THRESHOLD = 0.5;
	private static final double TREND_RAPID_LOWER_THRESHOLD = -0.5;
	
	/**
	 * Generates a summary of crime data for the area
	 * @param area The area to query.
	 * @param res Used to build the text.
	 * @return A card containing the summary of crime in the area.
	 * @throws Exception 
	 * @throws NDE2Exception 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws XPathExpressionException 
	 */
	public static CardModel crimeCardForArea(Area area, Resources res) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, NDE2Exception, Exception{
		double[][] areaPolygon = Mapper.getGeometryForArea(area);
		double[][] simplifiedPolygon = ArrayHelpers.every_nth_pair(areaPolygon, 10);
		
		try{
			Collection<Crime> policeDataCrimes = new StreetLevelCrimeMethodCall().addAreaPolygon(simplifiedPolygon).getStreetLevelCrime();
			return crimeCardForArea_policeData(area, policeDataCrimes, simplifiedPolygon, res);
		} catch (APIException apiex){
			return new CardModel("Census data should take over here", "Incomplete implementation",
					HoloCSSColourValues.PINK.getCssValue(), HoloCSSColourValues.PINK.getCssValue(), false, false, PlayCard.class);
		}
	}

	private static CardModel crimeCardForArea_policeData(Area area,
			Collection<Crime> policeDataCrimes, double[][] areaPolygon, Resources res) throws SocketTimeoutException, IOException, APIException, ParseException {
		Map<Long, Map<String, Integer>> dataCube = new HashMap<Long, Map<String, Integer>>();
		CrimeAvailabilityMethodCall avail = new CrimeAvailabilityMethodCall();
		Date latestAvailable = avail.getLastUpdated();
		List<Date> availableDates = avail.getAvailableDates();
		ArrayList<String> common_crime_array = new ArrayList<String>();
		Map<String, Integer> firstSlice = crimeSlice(policeDataCrimes);
		dataCube.put(latestAvailable.getTime(), firstSlice);
		
		/*
		 * This will ensure we only take last 12 months,
		 * not the whole available range.
		 */
		Collections.sort(availableDates);
		Collections.reverse(availableDates);
		availableDates = availableDates.subList(0, 12);
		
		Date earliestDate = new Date();
		for(Date n : availableDates){
			Map<String, Integer> slice = firstSlice;
			if(!(n.equals(latestAvailable))){
				Collection<Crime> crimesForDate = new StreetLevelCrimeMethodCall().addAreaPolygon(areaPolygon).addDate(n).getStreetLevelCrime();
				slice = crimeSlice(crimesForDate);
				dataCube.put(n.getTime(), slice);
				if(n.before(earliestDate)){
					earliestDate = n;
				}
			}
			common_crime_array.add(getMostCommonCrime(slice));
		}
		
		int countAtBeginningOfPeriod = totalCrimes(dataCube.get(earliestDate.getTime()));
		int countAtEndOfPeriod = totalCrimes(dataCube.get(latestAvailable.getTime()));
		
		double gradient = (double)(countAtEndOfPeriod - countAtBeginningOfPeriod) / 12.0; // dx is known
		String most_common_crime = mostCommon(common_crime_array);
		
		/*String crimegrad_s = String.format("Crime gradient is %f. The most common type of crime last available month is %s.", gradient, most_common_crime);
		return new CardModel("Test card for Crime in " + area.getName(), "Incomplete implementation. " + crimegrad_s,
				HoloCSSColourValues.PINK.getCssValue(), HoloCSSColourValues.PINK.getCssValue(), false, false, PlayCard.class);*/
		
		return makeCard(res, area, most_common_crime, gradient);
		
		//return null;
	}
	
	private static CardModel makeCard(Resources res, Area area, String most_common_crime, double gradient){
		TrendDescription trend_desc = new TrendDescription();
		
		if(gradient > TREND_RAPID_LOWER_THRESHOLD && gradient <= TREND_STABLE_LOWER_THRESHOLD)
			trend_desc.which = TrendDescription.FALLING;
		if(gradient > TREND_STABLE_LOWER_THRESHOLD && gradient <= TREND_STABLE_UPPER_THRESHOLD)
			trend_desc.which = TrendDescription.STABLE;
		if(gradient > TREND_STABLE_UPPER_THRESHOLD && gradient <= TREND_RAPID_UPPER_THRESHOLD)
			trend_desc.which = TrendDescription.RISING;
		if(gradient > TREND_RAPID_UPPER_THRESHOLD)
			trend_desc.which = TrendDescription.RISING_RAPIDLY;
		else
			trend_desc.which = TrendDescription.FALLING_RAPIDLY;
		
		String trend_desc_s = res.getStringArray(R.array.card_crime_real_trend)[trend_desc.which + 2];
		String body_s = res.getString(R.string.card_crime_real_body_base, most_common_crime, trend_desc_s);
		return new CardModel(res.getString(R.string.card_crime_real_title, area.getName()), "[wip] " + body_s,
				HoloCSSColourValues.PINK.getCssValue(), HoloCSSColourValues.PINK.getCssValue(), false, false, PlayCard.class);
	}

	private static String getMostCommonCrime(Map<String, Integer> slice) {
		int c = 0;
		String rawname = null;
		for(String category : slice.keySet()){
			if(c < slice.get(category)){
				rawname = category;
			}
		}
		if(rawname.equals("anti-social-behaviour"))
			return "anti-social behaviour";
		else
			return rawname.replace("-", " ");
	}

	private static Map<String, Integer> crimeSlice(
			Collection<Crime> policeDataCrimes) {
		HashMap<String, Integer> categoryTally = new HashMap<String, Integer>();
		for (Crime crime : policeDataCrimes) {
			if (!(categoryTally.containsKey(crime.getCategory()))) {
				/* Have we encountered a new category? */
				categoryTally.put(crime.getCategory(), 1);
			} else {
				/* No? Increment an existing one then */
				Integer t = categoryTally.get(crime.getCategory());
				t++;
				categoryTally.put(crime.getCategory(), t);
			}
		}
		
		return categoryTally;
	}
	
	private static int totalCrimes(Map<String, Integer> slice){
		int sum = 0;
		
		for(Integer c : slice.values()){
			sum += c;
		}
		
		return sum;
	}
	
	private static <T> T mostCommon(List<T> list){
		Map<T, Integer> map = new HashMap<T, Integer>();
		
		for(T e : list){
			Integer c = map.get(e);
			map.put(e, c == null ? 1 : c + 1);
		}
		
		Entry<T, Integer> max = null;
		
		for(Entry<T, Integer> e : map.entrySet()){
			if (max == null || e.getValue() > max.getValue())
				max = e;
		}
		
		return max.getKey();
	}
}
