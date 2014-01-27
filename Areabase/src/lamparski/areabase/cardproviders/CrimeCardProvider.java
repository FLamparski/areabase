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
		
		try{
			Collection<Crime> policeDataCrimes = new StreetLevelCrimeMethodCall().addAreaPolygon(areaPolygon).getStreetLevelCrime();
			return crimeCardForArea_policeData(area, policeDataCrimes, res);
		} catch (APIException apiex){
			return new CardModel("Census data should take over here", "Incomplete implementation",
					HoloCSSColourValues.PINK.getCssValue(), HoloCSSColourValues.PINK.getCssValue(), false, false, PlayCard.class);
		}
	}

	private static CardModel crimeCardForArea_policeData(Area area,
			Collection<Crime> policeDataCrimes, Resources res) throws SocketTimeoutException, IOException, APIException, ParseException {
		Map<Long, Map<String, Integer>> dataCube = new HashMap<Long, Map<String, Integer>>();
		CrimeAvailabilityMethodCall avail = new CrimeAvailabilityMethodCall();
		Date now = avail.getLastUpdated();
		List<Date> availableDates = avail.getAvailableDates();
		dataCube.put(now.getTime(), crimeSlice(policeDataCrimes));
		
		// the following might not work, depending on
		// whether the ArrayList compares references or
		// calls .equals() on objects.
		availableDates.remove(now);
		
		return null;
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
}
