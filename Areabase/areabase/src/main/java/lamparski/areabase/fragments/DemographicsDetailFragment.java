package lamparski.areabase.fragments;
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
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

import lamparski.areabase.AreaActivity;
import lamparski.areabase.R;
import lamparski.areabase.cardproviders.DemographicsCardProvider;
import nde2.helpers.CensusHelpers;
import nde2.pull.methodcalls.delivery.GetTables;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.Dataset;
import nde2.pull.types.Subject;
import nde2.pull.types.Topic;

/**
 * Keeping this one around as it may come in handy.
 *
 * Displays slightly more detailed information about the demographics in the area.
 *
 * @see lamparski.areabase.fragments.SummaryFragment
 */
@Deprecated
@SuppressWarnings("unused")
public abstract class DemographicsDetailFragment extends DetailViewFragment {

	private View myView;

	@Override
	public void onStart() {
		super.onStart();

		myView = getView();
	}

	/**
	 * Sets a label with id whichLabel to targetText. This is a helper function
	 * to help deal with cross-thread communication.
	 * 
	 * @param whichLabel
	 *            The R.id of the label to set
	 * @param targetText
	 *            new text to set
	 */
	private void setLabelOnUiThread(final int whichLabel,
			final String targetText) {
		if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) myView.findViewById(whichLabel))
                            .setText(targetText);
                }
            });
        }
	}

	@Override
	public void refreshContent() {
		if(getActivity() != null){
            if (area != null) {
                ((AreaActivity) getActivity()).setTitle(area.getName());
            } else {
                ((AreaActivity) getActivity()).setTitle("Demographics view");
            }
        }
		new AsyncTask<Area, Void, Void>() {
			@Override
			protected void onPreExecute() {
				// do something to show the progress indicator on action bar
			}

			@Override
			protected Void doInBackground(Area... params) {
				try {
					Area theArea = params[0];
					Subject subj = CensusHelpers.findSubject(theArea, "Census");
					List<DataSetFamily> dsfamilies = CensusHelpers
							.findRequiredFamilies(theArea, subj, new String[] {
									"Population Density", "Sex",
									"Age by Single Year", "Country of Birth",
									"Ethnic Group" });

					Set<Dataset> datasets = new GetTables().forArea(theArea)
							.inFamilies(dsfamilies).execute();

					String popdens = String.format("%.2f",
							findPopulationDensity(theArea, datasets));
					setLabelOnUiThread(
							R.id.demographics_summary_pop_density_val, popdens);

					String avgage = String.format("%.1f",
							findAverageAge(theArea, datasets));
					setLabelOnUiThread(R.id.demographics_summary_avg_age_val,
							avgage);

					String popsize = Integer.toString(findPopulationSize(
							theArea, datasets));
					setLabelOnUiThread(R.id.demographics_summary_pop_size_val,
							popsize);

					String women = String.format("%.1f",
							findWomenPercentage(theArea, datasets));
					setLabelOnUiThread(R.id.demographics_summary_pop_women_val,
							women);

					String main_ethnic_group = findMainEthnicGroup(theArea,
							datasets);
					setLabelOnUiThread(
							R.id.demographics_summary_ethnic_group_val,
							main_ethnic_group);

					String immigration = String.format("%.1f",
							findImmigration(theArea, datasets));
					setLabelOnUiThread(
							R.id.demographics_summary_immigration_val,
							immigration);
				} catch (final Throwable t) {
					showCroutonCrossThread(t.toString());
				}

				return null;
			}

			protected void onPostExecute(Void result) {
				// hide the progress indicator
			}

		}.execute(area);
	}

	protected float findImmigration(Area theArea, Set<Dataset> datasets) {
		float percentage = 0f;

		float all = 0f;
		float nonuk = 0f;
		for (Dataset ds : datasets) {
			if (ds.getTitle().contains("Country of Birth")) {
				for (Topic t : ds.getTopics().values()) {
					if (t.getTitle().contains("All Usual Residents")) {
						all = ds.getItems(t).iterator().next().getValue();
					} else if (t.getTitle().equals("Europe; Other Europe")
							|| t.getTitle().equals("Europe; Ireland")
							|| t.getTitle().equals("Africa")
							|| t.getTitle().equals("Middle East and Asia")
							|| t.getTitle().equals(
									"The Americas and the Caribbean")
							|| t.getTitle().equals("Antarctica and Oceania")) {
						nonuk += ds.getItems(t).iterator().next().getValue();
					}
				}
			}
		}

		percentage = (nonuk / all) * 100;

		return percentage;
	}

	protected String findMainEthnicGroup(Area theArea, Set<Dataset> datasets) {
		String ethnicGroup = "Undetermined";
		float all = 0f;
		float cEthnicGroup = 0f;

		for (Dataset ds : datasets) {
			if (ds.getTitle().contains("Ethnic Group")) {
				for (Topic t : ds.getTopics().values()) {
					if (t.getTitle().contains("All People")) {
						all = ds.getItems(t).iterator().next().getValue();
					} else {
						float curval = ds.getItems(t).iterator().next()
								.getValue();
						if (curval > cEthnicGroup) {
							cEthnicGroup = curval;
							ethnicGroup = t.getTitle();
						}
					}
				}
			}
		}

		return String.format("%s (%.1f)", ethnicGroup,
				(cEthnicGroup / all) * 100);
	}

	protected float findWomenPercentage(Area theArea, Set<Dataset> datasets) {
		float percentage = 0f;

		float women = 0;
		float all = 0;

		for (Dataset ds : datasets) {
			if (ds.getTitle().contains("Sex")) {
				for (Topic t : ds.getTopics().values()) {
					if (t.getTitle().startsWith("All")) {
						all = ds.getItems(t).iterator().next().getValue();
					} else if (t.getTitle().startsWith("Females")) {
						women = ds.getItems(t).iterator().next().getValue();
					}
				}
			}
		}

		percentage = (women / all) * 100f;
		return percentage;
	}

	protected int findPopulationSize(Area theArea, Set<Dataset> datasets) {
		int size = 0;

		for (Dataset ds : datasets) {
			if (ds.getTitle().contains("Sex")) {
				for (Topic t : ds.getTopics().values()) {
					if (t.getTitle().startsWith("All")) {
						size = (int) ds.getItems(t).iterator().next()
								.getValue();
					}
				}
			}
		}

		return size;
	}

	protected float findAverageAge(Area theArea, Set<Dataset> datasets) {
		// code reuse, ugly but should work
		return DemographicsCardProvider.calculateAverageAge(datasets);
	}

	protected float findPopulationDensity(Area theArea, Set<Dataset> datasets) {
		return DemographicsCardProvider.getPopulationDensity(datasets);
	}

}
