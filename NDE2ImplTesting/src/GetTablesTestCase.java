import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.methodcalls.delivery.GetTablesMethodCall;
import nde2.types.delivery.Dataset;
import nde2.types.delivery.Topic;
import nde2.types.discovery.Area;
import nde2.types.discovery.DataSetFamiliy;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class GetTablesTestCase {

	private Area mArea;
	private DataSetFamiliy mDsFamily;
	private List<DataSetFamiliy> mDsFams;
	private List<Area> mAreas;

	@Before
	public void setUp() throws Exception {
		/*
		 * Set up a mock area
		 */
		mArea = mock(Area.class);
		when(mArea.getAreaId()).thenReturn(6274999l);
		/*
		 * Set up a mock dataset family
		 */
		mDsFamily = mock(DataSetFamiliy.class);
		when(mDsFamily.getFamilyId()).thenReturn(2266);

		mAreas = new ArrayList<Area>();
		mAreas.add(mArea);
		mDsFams = new ArrayList<DataSetFamiliy>();
		mDsFams.add(mDsFamily);
	}

	@Test
	public void testGetTables() throws XPathExpressionException,
			NullPointerException, ParserConfigurationException, SAXException,
			IOException, NDE2Exception, ParseException {
		List<Dataset> returnedDatasets = new GetTablesMethodCall()
				.addAreas(mAreas).addDatasetFamilies(mDsFams).getTables();
		System.out.println("---------- DATASETS: ----------");
		System.out.println("Topics\tTitle\t\tCreator");
		for (Dataset dataset : returnedDatasets) {
			System.out.println(String.format("%d\t%s\t%s", dataset.getTopics()
					.size(), dataset.getTitle(), dataset.getCreator()));
			System.out.printf("  >> %d topics\n", dataset.getItems().size());
			System.out.println("\t-------- Topics: --------");
			System.out.println("\tID\tCode\tName");
			for (Topic topic : dataset.getTopics()) {
				System.out.printf("\t%d\t%d\t%s\n", topic.getTopicId(),
						topic.getTopicCode(), topic.getTitle());
				System.out.printf("\t  >> %d items\n", dataset.getItems(topic)
						.size());
			}
		}
	}

}
