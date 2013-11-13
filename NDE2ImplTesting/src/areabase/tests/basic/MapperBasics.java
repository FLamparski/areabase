package areabase.tests.basic;

import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.discovery.FindAreasMethodCall;
import nde2.types.discovery.Area;

import org.junit.Test;
import org.mysociety.mapit.Mapper;

public class MapperBasics {

	@Test
	public void fetchCoords() throws Exception, ValueNotAvailable {
		Area ar = new FindAreasMethodCall().addPostcode("EC2R 8AH").findAreas()
				.get(2);

		double[][] coords = Mapper.getGeometryForArea(ar);

		for (double[] pair : coords) {
			System.out.println(pair[0] + ", " + pair[1]);
		}
	}

}
