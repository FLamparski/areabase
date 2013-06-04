import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import nde2.methodcalls.discovery.FindAreasMethodCall;
import nde2.methodcalls.discovery.GetAreaChildrenMethodCall;
import nde2.types.discovery.Area;

import org.junit.Before;
import org.junit.Test;

public class GetAreaChildrenTestCase {

	Area baseArea;

	@Before
	public void setUp() throws Exception {
		baseArea = new FindAreasMethodCall().addAreaNamePart("Lewisham")
				.findAreas().get(0);
	}

	@Test
	public void test() {
		List<Area> children = null;
		try {
			children = new GetAreaChildrenMethodCall().addArea(baseArea)
					.getAreaChildren();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error fetching area children");
		}
		assertEquals(baseArea, children.get(0).getParent());
		assertEquals(6275990l, children.get(0).getAreaId());
	}

}
