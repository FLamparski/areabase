package areabase.tests.ultimate;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.discovery.FindAreasMethodCall;
import nde2.methodcalls.discovery.GetDatasetsMethodCall;
import nde2.types.discovery.Area;
import nde2.types.discovery.DataSetFamily;
import nde2.types.discovery.Subject;

import org.junit.Test;
import org.mysociety.mapit.Mapper;

import police.errors.APIException;
import police.methodcalls.StreetLevelCrimeMethodCall;
import police.types.Crime;

public class CrimeDataTest extends DataProviderTestBase {
	public final static String POSTCODE = "EC2R 8AH";
	public static final String[] DATASET_KEYWORDS = { "Population Density",
			"Sex", "Age by Single Year" };
	public static final String CRIME_SUBJECT_NAME = "Crime and Safety";

	@Test
	public void crimeCardForArea() throws Exception {
		long startFindAreasMethodCall = System.currentTimeMillis();
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
		long endFindAreasMethodCall = System.currentTimeMillis();

		long timeFindAreasMethodCall = endFindAreasMethodCall
				- startFindAreasMethodCall;

		System.out.println("Bank / CoL.001: "
				+ bankArea.getDetailed().getExtCode());

		Subject crimeSubject = findSubject(bankArea, CRIME_SUBJECT_NAME);

		try {
			List<DataSetFamily> crimeThings = new GetDatasetsMethodCall()
					.addArea(bankArea).addSubject(crimeSubject).getDatasets();
			for (DataSetFamily dsf : crimeThings) {
				System.out.println("Dataset: " + dsf.getName());
			}
		} catch (ValueNotAvailable e) {
			e.printStackTrace();
		}

		System.out.println("Finding polygon for area...");
		double[][] coordinates = Mapper.getGeometryForArea(bankArea);
		System.out.println("Polygon found, " + coordinates.length
				+ " vertices.");
		Collection<Crime> crimesForPoly = null;
		try {
			crimesForPoly = new StreetLevelCrimeMethodCall().addAreaPolygon(
					coordinates).getStreetLevelCrime();
		} catch (APIException e) {
			System.out
					.println("Cannot use the police service, falling back to census...");
		}
	}

	public double[][] every_nth_pair(double[][] original, int n) {
		double[][] destination = new double[original.length / n][2];

		for (int i = 0; i < original.length; i += n) {
			destination[i / n] = original[i];
		}

		return destination;
	}

	@Test
	public void everyNthTest() throws Exception, ValueNotAvailable {
		double[][] coordinates = Mapper
				.getGeometryForArea(new FindAreasMethodCall()
						.addPostcode(POSTCODE).findAreas().get(2));
		double[][] less_coords = every_nth_pair(coordinates, 5);
		System.out.println("Original array: " + coordinates.length
				+ " pairs, new array: " + less_coords.length + " pairs.");
	}

}
