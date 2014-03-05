package lamparski.areabase.cardproviders;

import android.content.res.Resources;
import android.util.Log;

import com.fima.cardsui.objects.CardModel;

import org.mysociety.mapit.Mapper;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import lamparski.areabase.R;
import lamparski.areabase.cards.PlayCard;
import lamparski.areabase.map_support.HoloCSSColourValues;
import nde2.errors.NDE2Exception;
import nde2.helpers.ArrayHelpers;
import nde2.helpers.CensusHelpers;
import nde2.helpers.Statistics;
import nde2.pull.methodcalls.delivery.GetTables;
import nde2.pull.methodcalls.discovery.GetCompatibleSubjects;
import nde2.pull.methodcalls.discovery.GetDatasetFamilies;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.Dataset;
import nde2.pull.types.DateRange;
import nde2.pull.types.Subject;
import nde2.pull.types.Topic;
import police.errors.APIException;
import police.methodcalls.CrimeAvailabilityMethodCall;
import police.methodcalls.StreetLevelCrimeMethodCall;
import police.types.Crime;

public class CrimeCardProvider {
	public static final double TREND_STABLE_UPPER_THRESHOLD = 0.1;
	public static final double TREND_STABLE_LOWER_THRESHOLD = -0.1;
	public static final double TREND_RAPID_UPPER_THRESHOLD = 0.75;
	public static final double TREND_RAPID_LOWER_THRESHOLD = -0.75;
	private static final long UNIX_30_DAYS = 1000l * 60 * 60 * 24 * 30;

	private static final int ONS_NOTIFIABLE_OFFENCES_RECORDED_BY_POLICE = 904;

	/**
	 * Generates a summary of crime data for the area
	 * 
	 * @param area
	 *            The area to query.
	 * @param res
	 *            Used to build the text.
	 * @return A card containing the summary of crime in the area.
	 * @throws Exception
	 * @throws NDE2Exception
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	public static CardModel crimeCardForArea(Area area, Resources res)
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, NDE2Exception, Exception {
		double[][] areaPolygon = Mapper.getGeometryForArea(area);
		double[][] simplifiedPolygon = ArrayHelpers.every_nth_pair(areaPolygon,
				10);

		try {
			Collection<Crime> policeDataCrimes = new StreetLevelCrimeMethodCall()
					.addAreaPolygon(simplifiedPolygon).getStreetLevelCrime();
			CardModel mdl = crimeCardForArea_policeData(area, policeDataCrimes,
					simplifiedPolygon, res);
			mdl.setData(areaPolygon);
			return mdl;
		} catch (APIException apiex) {
			return crimeCardForArea_censusData(area, res);
		}
	}

	private static CardModel crimeCardForArea_policeData(Area area,
			Collection<Crime> policeDataCrimes, double[][] areaPolygon,
			Resources res) throws Exception {
		Map<Long, Map<String, Integer>> dataCube = new HashMap<Long, Map<String, Integer>>();
		CrimeAvailabilityMethodCall avail = new CrimeAvailabilityMethodCall();
		Date latestAvailable = avail.getLastUpdated();
		List<Date> availableDates = avail.getAvailableDates();
		ArrayList<String> common_crime_array = new ArrayList<String>();
		Map<String, Integer> firstSlice = crimeSlice(policeDataCrimes);
		dataCube.put(latestAvailable.getTime() / UNIX_30_DAYS, firstSlice);

		/*
		 * This will ensure we only take last 12 months, not the whole available
		 * range.
		 */
		Collections.sort(availableDates);
		Collections.reverse(availableDates);
		availableDates = availableDates.subList(0, 12);

		Date earliestDate = new Date();
		for (Date n : availableDates) {
			Map<String, Integer> slice = firstSlice;
			if (!(n.equals(latestAvailable))) {
				Collection<Crime> crimesForDate = new StreetLevelCrimeMethodCall()
						.addAreaPolygon(areaPolygon).addDate(n)
						.getStreetLevelCrime();
				slice = crimeSlice(crimesForDate);
				dataCube.put(n.getTime() / UNIX_30_DAYS, slice);
				if (n.before(earliestDate)) {
					earliestDate = n;
				}
			}
			common_crime_array.add(mostCommonCrime_police(slice));
		}

		double gradient = calculateCrimeGradient(dataCube);
		Log.i("linear-regression",
				"The OLS linear regression for this dataset is " + gradient);

		String most_common_crime = ArrayHelpers.mostCommon(common_crime_array);

		return makeCard(res, area, most_common_crime, gradient);
	}

    public static double getCrimeTrend(Area area) throws Exception {
        double[][] areaPolygon = Mapper.getGeometryForArea(area);
        double[][] simplifiedPolygon = ArrayHelpers.every_nth_pair(areaPolygon,
                10);

        Collection<Crime> policeDataCrimes = null;
        try{
            policeDataCrimes = new StreetLevelCrimeMethodCall()
                    .addAreaPolygon(simplifiedPolygon).getStreetLevelCrime();
            Map<Long, Map<String, Integer>> dataCube = new HashMap<Long, Map<String, Integer>>();
            CrimeAvailabilityMethodCall avail = new CrimeAvailabilityMethodCall();
            Date latestAvailable = avail.getLastUpdated();
            List<Date> availableDates = avail.getAvailableDates();
            dataCube.put(latestAvailable.getTime() / UNIX_30_DAYS, crimeSlice(policeDataCrimes));

            /*
             * This will ensure we only take last 12 months, not the whole available
             * range.
             */
            Collections.sort(availableDates);
            Collections.reverse(availableDates);
            availableDates = availableDates.subList(0, 12);

            Date earliestDate = new Date();
            for (Date n : availableDates) {
                if (!(n.equals(latestAvailable))) {
                    Collection<Crime> crimesForDate = new StreetLevelCrimeMethodCall()
                            .addAreaPolygon(areaPolygon).addDate(n)
                            .getStreetLevelCrime();
                    Map<String, Integer> slice = crimeSlice(crimesForDate);
                    dataCube.put(n.getTime() / UNIX_30_DAYS, slice);
                    if (n.before(earliestDate)) {
                        earliestDate = n;
                    }
                }
            }

            return calculateCrimeGradient(dataCube);
        } catch (APIException e) {
            DataSetFamily crimeFamily = getCrimeFamily(area);
            Set<Dataset> theDatasets = new HashSet<Dataset>();
            for (DateRange r : crimeFamily.getDateRanges()) {
                Set<Dataset> currentDataset = new GetTables().forArea(area)
                        .inFamily(crimeFamily).inDateRange(r).execute();
                theDatasets.addAll(currentDataset);
            }

            long earliestDate = 0, latestDate = 0;

            Map<Long, Map<String, Integer>> onsDatacube = new HashMap<Long, Map<String, Integer>>();
            for (Dataset d : theDatasets) {
                long date = d.getPeriods().values().iterator().next().getEndDate()
                        .getTime();
                if (date < earliestDate && earliestDate > 0) {
                    earliestDate = date;
                }
                if (date > latestDate) {
                    latestDate = date;
                }
                Map<String, Integer> crimeSlice = new HashMap<String, Integer>();
                for (Topic t : d.getTopics().values()) {
                    String key = t.getTitle();
                    int value;
                    try{
                        value = (int) d.getItems(t).iterator().next().getValue();
                    } catch (NoSuchElementException nsee){
                        continue;
                    }
                    crimeSlice.put(key, value);
                }
                onsDatacube.put(date, crimeSlice);
            }

            return calculateCrimeGradient(onsDatacube);
        }
    }

	/**
	 * This method uses Ordinary Least Squares linear regression *** TESTS
	 * NEEDED ***
	 * 
	 * @param dataCube
	 * @return b of the OLS for dataCube
	 */
	private static double calculateCrimeGradient(
			Map<Long, Map<String, Integer>> dataCube) {
		Map<Long, Integer> crimesForDate = new HashMap<Long, Integer>();
		for (Entry<Long, Map<String, Integer>> sliceWithDate : dataCube
				.entrySet()) {
			crimesForDate.put(sliceWithDate.getKey(),
					totalCrimes(sliceWithDate.getValue()));
		}

		return Statistics.linearRegressionGradient(crimesForDate);
	}


    private static DataSetFamily getCrimeFamily(Area area) throws XmlPullParserException, IOException, NDE2Exception {
        Map<Subject, Integer> subjects = new GetCompatibleSubjects(area)
                .execute();
        Subject crimeSubject = CensusHelpers.findSubject(area, "Crime and Safety");

        List<DataSetFamily> families = new GetDatasetFamilies(crimeSubject)
                .forArea(area).execute();
        DataSetFamily crimeFamily = null;
        for (DataSetFamily f : families) {
            if (f.getFamilyId() == ONS_NOTIFIABLE_OFFENCES_RECORDED_BY_POLICE) {
                crimeFamily = f;
            }
        }
        return crimeFamily;
    }

	private static CardModel crimeCardForArea_censusData(Area area,
			Resources res) throws IOException, XmlPullParserException,
			NDE2Exception {
		DataSetFamily crimeFamily = getCrimeFamily(area);

		Set<Dataset> theDatasets = new HashSet<Dataset>();
		for (DateRange r : crimeFamily.getDateRanges()) {
			Set<Dataset> currentDataset = new GetTables().forArea(area)
					.inFamily(crimeFamily).inDateRange(r).execute();
			theDatasets.addAll(currentDataset);
		}

		long earliestDate = 0, latestDate = 0;
		List<String> most_common_array = new ArrayList<String>();

		Map<Long, Map<String, Integer>> onsDatacube = new HashMap<Long, Map<String, Integer>>();
		for (Dataset d : theDatasets) {
			long date = d.getPeriods().values().iterator().next().getEndDate()
					.getTime();
			if (date < earliestDate && earliestDate > 0) {
                earliestDate = date;
            }
			if (date > latestDate) {
                latestDate = date;
            }
			Map<String, Integer> crimeSlice = new HashMap<String, Integer>();
			for (Topic t : d.getTopics().values()) {
				String key = t.getTitle();
				int value = (int) d.getItems(t).iterator().next().getValue();
				crimeSlice.put(key, value);
			}
			most_common_array.add(mostCommonCrime_ons(crimeSlice));
			onsDatacube.put(date, crimeSlice);
		}

		String most_common_crime = ArrayHelpers.mostCommon(most_common_array);

		/* TODO: Test needed! */
		double dYdX = calculateCrimeGradient(onsDatacube);

		Log.i("linear-regression",
				"The OLS linear regression for this dataset is " + dYdX);

		return makeCard(res, area, most_common_crime, dYdX);
	}

	private static CardModel makeCard(Resources res, Area area,
			String most_common_crime, double gradient) {
		TrendDescription trend_desc = new TrendDescription();

		if (gradient > TREND_RAPID_LOWER_THRESHOLD
				&& gradient <= TREND_STABLE_LOWER_THRESHOLD) {
            trend_desc.which = TrendDescription.FALLING;
        } else if (gradient > TREND_STABLE_LOWER_THRESHOLD
				&& gradient <= TREND_STABLE_UPPER_THRESHOLD) {
            trend_desc.which = TrendDescription.STABLE;
        } else if (gradient > TREND_STABLE_UPPER_THRESHOLD
				&& gradient <= TREND_RAPID_UPPER_THRESHOLD) {
            trend_desc.which = TrendDescription.RISING;
        } else if (gradient > TREND_RAPID_UPPER_THRESHOLD) {
            trend_desc.which = TrendDescription.RISING_RAPIDLY;
        } else {
            trend_desc.which = TrendDescription.FALLING_RAPIDLY;
        }

		String trend_desc_s = res.getStringArray(R.array.card_crime_real_trend)[trend_desc.which + 2];
		String body_s = res.getString(R.string.card_crime_real_body_base,
				most_common_crime, trend_desc_s);
		return new CardModel(res.getString(R.string.card_crime_real_title,
				area.getName()), "[wip] " + body_s,
				HoloCSSColourValues.PINK.getCssValue(),
				HoloCSSColourValues.PINK.getCssValue(), false, false,
				PlayCard.class);
	}

	private static String mostCommonCrime_police(Map<String, Integer> slice) {
		int c = 0;
		String rawname = null;
		for (String category : slice.keySet()) {
			if (c < slice.get(category)) {
				rawname = category;
			}
		}
		/*
		 * To be more machine-readable, crime categories do not contain spaces.
		 * Normally, a simple replace would do, but in the case of anti-social
		 * behaviour, the first dash needs to be preserved.
		 */
		if (rawname.equals("anti-social-behaviour")) {
            return "anti-social behaviour";
        } else {
            return rawname.replace('-', ' ');
        }
	}

	private static String mostCommonCrime_ons(Map<String, Integer> slice) {
		int c = 0;
		String rawname = null;
		for (String category : slice.keySet()) {
			if (c < slice.get(category)) {
				rawname = category;
			}
		}
		return rawname.toLowerCase(Locale.UK);
	}

	public static Map<String, Integer> crimeSlice(
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

	private static int totalCrimes(Map<String, Integer> slice) {
		int sum = 0;

		for (Integer c : slice.values()) {
			sum += c;
		}

		return sum;
	}
}
