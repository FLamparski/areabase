package areabase.tests.ultimate;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import nde2.errors.ValueNotAvailable;
import nde2.helpers.ArrayHelpers;
import nde2.helpers.CensusHelpers;
import nde2.methodcalls.discovery.FindAreasMethodCall;
import nde2.methodcalls.discovery.GetDatasetsMethodCall;
import nde2.types.discovery.Area;
import nde2.types.discovery.DataSetFamily;
import nde2.types.discovery.Subject;

import org.junit.Test;
import org.mysociety.mapit.Mapper;

import police.errors.APIException;
import police.methodcalls.CrimeAvailabilityMethodCall;
import police.methodcalls.StreetLevelCrimeMethodCall;
import police.types.Crime;

public class CrimeDataTest {
	public final static String POSTCODE = "SE6 4UX";
	public static final String[] DATASET_KEYWORDS = { "Population Density",
			"Sex", "Age by Single Year" };
	public static final String CRIME_SUBJECT_NAME = "Crime and Safety";

	private static final String CSVPATH = "./crime for Lewisham-001 types.csv";

	/**
	 * This method is intended to only be used with my college network. It uses
	 * NTLM authentication, and with this being a standalone java program, I'd
	 * otherwise have to manage it myself. What I'm doing instead is running a
	 * proxy forwarder locally on port 3128, which handles the NTLM stuff.
	 * 
	 * To disable, comment out the annotation.
	 */
	// @org.junit.BeforeClass
	public static void setupProxyStuff() {
		System.setProperty("http.proxyHost", "localhost");
		System.setProperty("http.proxyPort", "3128");
	}

	@Test
	public void crimeCardForArea() throws Exception {
		Area bankArea;
		try {
			bankArea = new FindAreasMethodCall().addPostcode(POSTCODE)
					.findAreas().get(2);
		} catch (ValueNotAvailable e) {
			System.err.println("This area is not available.");
			e.printStackTrace();
			fail();
			return;
		}

		System.out.println(bankArea.getName() + " -- extcode: "
				+ bankArea.getDetailed().getExtCode());

		Subject crimeSubject = CensusHelpers.findSubject(bankArea,
				CRIME_SUBJECT_NAME);

		List<DataSetFamily> crimeCensusDatasets = null;

		try {
			crimeCensusDatasets = new GetDatasetsMethodCall().addArea(bankArea)
					.addSubject(crimeSubject).getDatasets();
			for (DataSetFamily dsf : crimeCensusDatasets) {
				System.out.println("Dataset: " + dsf.getName());
			}
		} catch (ValueNotAvailable e) {
			e.printStackTrace();
		}

		System.out.println("Finding polygon for area...");
		double[][] coordinates = ArrayHelpers.every_nth_pair(
				Mapper.getGeometryForArea(bankArea), 5);
		System.out.println("Polygon found, " + coordinates.length
				+ " vertices.");
		try {
			Collection<Crime> crimesForPoly = new StreetLevelCrimeMethodCall()
					.addAreaPolygon(coordinates).getStreetLevelCrime();
			processUsingPoliceData(crimesForPoly);
		} catch (APIException e) {
			System.out
					.println("Cannot use the police service, falling back to census...");
			processUsingCensusData(bankArea, crimeCensusDatasets);
		}
	}

	private void processUsingCensusData(Area bankArea,
			List<DataSetFamily> crimeCensusDatasets) throws Exception {
		throw new Exception("Not yet implemented!");
	}

	private void processUsingPoliceData(Collection<Crime> crimesForPoly) {
		System.out.println("Using the Police.uk data");
		System.out.println("Number of crimes: " + crimesForPoly.size());
		HashMap<String, Integer> categoryTally = new HashMap<>();
		for (Crime crime : crimesForPoly) {
			if (!(categoryTally.containsKey(crime.getCategory()))) {
				categoryTally.put(crime.getCategory(), 1);
			} else {
				Integer t = categoryTally.get(crime.getCategory());
				t++;
				categoryTally.put(crime.getCategory(), t);
			}
		}
		Set<Entry<String, Integer>> table = categoryTally.entrySet();
		for (Entry<String, Integer> i : table) {
			System.out.println(i.getKey().replace("-", " ") + "\t"
					+ i.getValue());
		}
	}

	@Test
	public void testTrendSpotting() throws Exception {
		Area bankArea;
		try {
			bankArea = new FindAreasMethodCall().addPostcode(POSTCODE)
					.findAreas().get(2);
		} catch (ValueNotAvailable e) {
			System.err.println("This area is not available.");
			e.printStackTrace();
			fail();
			return;
		}

		System.out.println(bankArea.getName() + " -- extcode: "
				+ bankArea.getDetailed().getExtCode());

		System.out.println("Finding polygon for area...");
		double[][] coordinates = ArrayHelpers.every_nth_pair(
				Mapper.getGeometryForArea(bankArea), 5);
		System.out.println("Polygon found, " + coordinates.length
				+ " vertices.");

		List<Date> _availableDates = new CrimeAvailabilityMethodCall()
				.getAvailableDates();

		List<Date> availableDates = _availableDates.subList(0, 12);

		File csv_dest = null;
		FileWriter csv_writer = null;

		try {
			csv_dest = new File(CSVPATH);
			csv_dest.createNewFile();
			csv_writer = new FileWriter(csv_dest);

			String HEADERS = "Category|";
			for (Date availDate : availableDates) {
				HEADERS += new SimpleDateFormat("MM-yyyy").format(availDate)
						+ "|";
			}
			csv_writer.write(HEADERS.substring(0, HEADERS.length() - 1) + "\n");

			HashMap<String, List<Integer>> ROWS = new HashMap<>();

			for (Date availDate : availableDates) {
				Collection<Crime> crimes = new StreetLevelCrimeMethodCall()
						.addAreaPolygon(coordinates).addDate(availDate)
						.getStreetLevelCrime();
				for (Crime crime : crimes) {
					if (ROWS.containsKey(crime.getCategory())) {
						try {
							Integer t = ROWS.get(crime.getCategory()).get(
									availableDates.indexOf(availDate));
							t += 1;
							ROWS.get(crime.getCategory()).set(
									availableDates.indexOf(availDate), t);
						} catch (IndexOutOfBoundsException e) {
							ROWS.get(crime.getCategory()).add(1);
						}
					} else {
						ArrayList<Integer> newList = new ArrayList<>();
						newList.add(1);
						ROWS.put(crime.getCategory(), newList);
					}
				}
			}

			Set<Entry<String, List<Integer>>> table = ROWS.entrySet();
			for (Entry<String, List<Integer>> row : table) {
				String txtRow = row.getKey().replace("-", " ") + "|";
				for (Integer v : row.getValue()) {
					txtRow += v.toString() + "|";
				}
				csv_writer.write(txtRow.substring(0, txtRow.length() - 1)
						+ "\n");
			}

			csv_writer.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			if (csv_writer != null)
				csv_writer.close();
		}
	}

}
