package lamparski.areabase.cardproviders;

import static nde2.helpers.CensusHelpers.findRequiredFamilies;
import static nde2.helpers.CensusHelpers.findSubject;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import lamparski.areabase.R;
import lamparski.areabase.cards.PlayCard;
import lamparski.areabase.map_support.HoloCSSColourValues;
import nde2.errors.NDE2Exception;
import nde2.helpers.DateFormat;
import nde2.pull.methodcalls.delivery.GetTables;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.DataSetItem;
import nde2.pull.types.Dataset;
import nde2.pull.types.Subject;
import nde2.pull.types.Topic;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;

import com.fima.cardsui.objects.CardModel;

public class EnvironmentCardProvider {
	private static final String[] REQUIRED_FAMILIES = { "Land Use",
			"Domestic Energy Consumption" };
	private static final String ENVIRONMENT_SUBJECT = "Physical Environment";

	private static final int AREA_RURAL = 0;
	private static final int AREA_URBAN = 1;
	private static final int AREA_EXTRA_URBAN = 2;

	public static CardModel environmentCardForArea(Area area, Resources res)
			throws IOException, XmlPullParserException, NDE2Exception {
		Subject environmentSubject = findSubject(area, ENVIRONMENT_SUBJECT);

		List<DataSetFamily> families = findRequiredFamilies(area,
				environmentSubject, REQUIRED_FAMILIES);

		Set<Dataset> theDatasets = new GetTables().inFamilies(families)
				.forArea(area).execute();

		int urbanisationType = getUrbanisationType(theDatasets);
		String urbanisationTypeString = res
				.getStringArray(R.array.card_environ_area_types)[urbanisationType];

		float[] energyCurrentUse = getLatestEnergyUse(theDatasets);

		String title = res.getString(R.string.card_environ_title,
				area.getName());
		String descr = res.getString(R.string.card_environ_body_base,
				area.getName(), urbanisationTypeString,
				Integer.toString((int) energyCurrentUse[0]),
				energyCurrentUse[1], "FOO");
		return makeCard(title, descr);
	}

	private static CardModel makeCard(String title, String description) {
		return new CardModel(title, description,
				HoloCSSColourValues.PURPLE.getCssValue(),
				HoloCSSColourValues.PURPLE.getCssValue(), false, false,
				PlayCard.class);
	}

	private static int getUrbanisationType(Set<Dataset> theDatasets) {
		float greenspaceArea = 0f;
		float domesticBuildingsArea = 0f;
		float nonDomesticBuildingsArea = 0f;
		float roadsArea = 0f;
		float totalNonAdminArea = 0f;

		for (Dataset ds : theDatasets) {
			if (ds.getTitle().contains(REQUIRED_FAMILIES[0])) {
				for (Topic t : ds.getTopics().values()) {
					if (t.getTitle().contains("Total Area of All Land Types")) {
						totalNonAdminArea = ds.getItems(t).iterator().next()
								.getValue();
					}
					if (t.getTitle().contains("Area of Domestic Buildings")) {
						domesticBuildingsArea = ds.getItems(t).iterator()
								.next().getValue();
					}
					if (t.getTitle().contains("Area of Non Domestic Buildings")) {
						nonDomesticBuildingsArea = ds.getItems(t).iterator()
								.next().getValue();
					}
					if (t.getTitle().contains("Area of Road")) {
						roadsArea = ds.getItems(t).iterator().next().getValue();
					}
					if (t.getTitle().contains("Area of Greenspace")) {
						greenspaceArea = ds.getItems(t).iterator().next()
								.getValue();
					}
				}
			}
		}

		if (((domesticBuildingsArea + nonDomesticBuildingsArea + roadsArea) / totalNonAdminArea) > 0.5)
			return AREA_EXTRA_URBAN;
		else if ((greenspaceArea / totalNonAdminArea) < 0.5)
			return AREA_URBAN;
		else
			return AREA_RURAL;

	}

	/**
	 * 
	 * @param theDatasets
	 * @return the latest year at index 0, the actual energy use at index 1.
	 */
	private static float[] getLatestEnergyUse(Set<Dataset> theDatasets) {
		float[] retval = new float[2];

		for (Dataset ds : theDatasets) {
			if (ds.getTitle().contains("Domestic Energy Consumption")) {
				for (Topic t : ds.getTopics().values()) {
					if (t.getTitle()
							.contains(
									"Total Consumption of Domestic Electricity and Gas")) {
						DataSetItem item = ds.getItems(t).iterator().next();
						retval[0] = (float) DateFormat.getYear(item.getPeriod()
								.getEndDate());
						retval[1] = item.getValue();
					}
				}
			}
		}

		return retval;
	}
}
