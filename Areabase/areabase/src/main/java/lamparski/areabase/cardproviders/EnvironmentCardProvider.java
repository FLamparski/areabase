package lamparski.areabase.cardproviders;
/** !license-block 
    This file is part of Areabase.

    Areabase is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Areabase is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Areabase.  If not, see <http://www.gnu.org/licenses/>.

    Areabase (C) 2013-2014 Filip Wieland <filiplamparski@gmail.com>
*/
import android.annotation.SuppressLint;
import android.content.res.Resources;

import com.fima.cardsui.objects.CardModel;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lamparski.areabase.R;
import lamparski.areabase.cards.PlayCard;
import nde2.errors.InvalidParameterException;
import nde2.errors.NDE2Exception;
import nde2.helpers.DateFormat;
import nde2.helpers.Statistics;
import nde2.pull.methodcalls.delivery.GetTables;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.DataSetItem;
import nde2.pull.types.Dataset;
import nde2.pull.types.DateRange;
import nde2.pull.types.Subject;
import nde2.pull.types.Topic;

import static nde2.helpers.CensusHelpers.findRequiredFamilies;
import static nde2.helpers.CensusHelpers.findSubject;

public class EnvironmentCardProvider {
	public static final String[] REQUIRED_FAMILIES = { "Land Use",
			"Domestic Energy Consumption" };
	public static final String ENVIRONMENT_SUBJECT = "Physical Environment";

	private static final int AREA_RURAL = 0;
	private static final int AREA_URBAN = 1;
	private static final int AREA_EXTRA_URBAN = 2;

	private static final long UNIX_30_DAYS = 1000l * 60 * 60 * 24 * 30;

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
		String energyUseTrendString = res
				.getStringArray(R.array.card_environ_energy_trend)[getEnergyTrend(
				area, families).which + 2];

		String title = res.getString(R.string.card_environ_title,
				area.getName());
		String descr = res.getString(R.string.card_environ_body_base,
				area.getName(), urbanisationTypeString,
				Integer.toString((int) energyCurrentUse[0]),
				energyCurrentUse[1], energyUseTrendString);
		return makeCard(title, descr);
	}

	private static CardModel makeCard(String title, String description) {
		return new CardModel(title, description, "#BADA55", "#7F992C", false,
				false, PlayCard.class);
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

		if (((domesticBuildingsArea + nonDomesticBuildingsArea + roadsArea) / totalNonAdminArea) > 0.5) {
            return AREA_EXTRA_URBAN;
        } else if ((greenspaceArea / totalNonAdminArea) < 0.5) {
            return AREA_URBAN;
        } else {
            return AREA_RURAL;
        }

	}

	/**
	 * 
	 * @param theDatasets datasets that has the actual data
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

	@SuppressLint("UseSparseArrays")
	public static TrendDescription getEnergyTrend(Area area,
			List<DataSetFamily> fams) throws InvalidParameterException,
			IOException, XmlPullParserException, NDE2Exception {
		TrendDescription ans = new TrendDescription();
		DataSetFamily fam = null;
		for (DataSetFamily f : fams) {
			if (f.getName().contains("Domestic Energy Consumption")) {
                fam = f;
            }
		}

		Set<Dataset> theDatasets = new HashSet<Dataset>();
		for (DateRange rng : fam.getDateRanges()) {
			theDatasets.addAll(new GetTables().forArea(area).inFamily(fam)
					.inDateRange(rng).execute());
		}

		Map<Integer, Float> datapoints = new HashMap<Integer, Float>();
		for (Dataset ds : theDatasets) {
			for (Topic t : ds.getTopics().values()) {
				if (t.getTitle().contains(
						"Total Consumption of Domestic Electricity and Gas")) {
					DataSetItem item = ds.getItems(t).iterator().next();
					int shortDate = (int) (item.getPeriod().getEndDate()
							.getTime() / UNIX_30_DAYS);
					float value = item.getValue();
					datapoints.put(shortDate, value);
				}
			}
		}

		double gradient = Statistics.linearRegressionGradient(datapoints);

		if (gradient <= -1.2) {
            ans.which = TrendDescription.FALLING_RAPIDLY;
        } else if (gradient > -1.2 && gradient <= -0.1) {
            ans.which = TrendDescription.FALLING;
        } else if (gradient > -0.1 && gradient <= 0.1) {
            ans.which = TrendDescription.STABLE;
        } else if (gradient > 0.1 && gradient <= 1.2) {
            ans.which = TrendDescription.RISING;
        } else {
            ans.which = TrendDescription.RISING_RAPIDLY;
        }

		return ans;
	}
}
