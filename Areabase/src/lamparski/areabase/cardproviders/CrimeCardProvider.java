package lamparski.areabase.cardproviders;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

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
			return crimeCardForArea_policeData(area, policeDataCrimes, areaPolygon, res);
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
		dataCube.put(latestAvailable.getTime(), crimeSlice(policeDataCrimes));
		
		Date earliestDate = new Date();
		for(Date n : availableDates){
			if(!(n.equals(latestAvailable))){
				Collection<Crime> crimesForDate = new StreetLevelCrimeMethodCall().addAreaPolygon(areaPolygon).addDate(n).getStreetLevelCrime();
				dataCube.put(n.getTime(), crimeSlice(crimesForDate));
				if(n.before(earliestDate)){
					earliestDate = n;
				}
			}
		}
		
		int countAtBeginningOfPeriod = totalCrimes(dataCube.get(earliestDate.getTime()));
		int countAtEndOfPeriod = totalCrimes(dataCube.get(latestAvailable.getTime()));
		
		float gradient = (countAtEndOfPeriod - countAtBeginningOfPeriod) / (latestAvailable.getTime() - earliestDate.getTime());
		
		String.format("Crime gradient for %s is %f.", area.getName(), gradient);
		return new CardModel("Census data should take over here", "Incomplete implementation",
				HoloCSSColourValues.PINK.getCssValue(), HoloCSSColourValues.PINK.getCssValue(), false, false, PlayCard.class);
		
		//return null;
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
}
