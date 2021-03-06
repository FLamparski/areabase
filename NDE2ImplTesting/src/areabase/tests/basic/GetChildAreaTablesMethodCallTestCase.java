package areabase.tests.basic;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.methodcalls.delivery.GetChildAreaTablesMethodCall;
import nde2.types.delivery.Dataset;
import nde2.types.delivery.Topic;
import nde2.types.discovery.Area;
import nde2.types.discovery.DataSetFamily;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class GetChildAreaTablesMethodCallTestCase {
	private Area mArea;
	private DataSetFamily mDsFamily;
	private List<DataSetFamily> mDsFams;

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
		mDsFamily = mock(DataSetFamily.class);
		when(mDsFamily.getFamilyId()).thenReturn(2266);

		mDsFams = new ArrayList<DataSetFamily>();
		mDsFams.add(mDsFamily);
	}

	@Test
	public void testGetTables() throws XPathExpressionException,
			NullPointerException, ParserConfigurationException, SAXException,
			IOException, NDE2Exception, ParseException {
		List<Dataset> returnedDatasets = new GetChildAreaTablesMethodCall()
				.addParentArea(mArea).addDatasetFamilies(mDsFams).getTables();
		System.out.println("---------- DATASETS: ----------");
		System.out.println("Topics\tTitle\t\tCreator");
		for (Dataset dataset : returnedDatasets) {
			System.out.println(String.format("%d\t%s\t%s", dataset.getTopics()
					.size(), dataset.getTitle(), dataset.getCreator()));
			System.out.printf("  >> %d items\n", dataset.getItems().size());
			System.out.println("\t-------- Boundaries --------");
			System.out.println("\tID\tCode\tName");
			Collection<Topic> topics = dataset.getTopics().values();
			for (Topic topic : topics) {
				System.out.printf("\t%s\t%s\n", topic.getTopicCode(),
						topic.getTitle());
				System.out.printf("\t >> %d items\n", dataset.getItems(topic)
						.size());
			}
		}
	}

}
