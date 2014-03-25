package lamparski.areabase.services;
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
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.fima.cardsui.objects.CardModel;
import com.uk_postcodes.api.Postcode;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lamparski.areabase.cardproviders.CrimeCardProvider;
import lamparski.areabase.cardproviders.DemographicsCardProvider;
import lamparski.areabase.cardproviders.EconomyCardProvider;
import lamparski.areabase.cardproviders.EnvironmentCardProvider;
import lamparski.areabase.utils.OnError;
import nde2.helpers.CensusHelpers;
import nde2.helpers.Statistics;
import nde2.pull.methodcalls.delivery.GetTables;
import nde2.pull.methodcalls.discovery.FindAreas;
import nde2.pull.methodcalls.discovery.GetDatasetFamilies;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.Dataset;
import nde2.pull.types.DateRange;
import nde2.pull.types.Subject;

/**
 * A service for working with ONS data, asynchronously and all.
 *
 * Also defines a bunch of callback interfaces.
 * 
 * @author filip
 * 
 */
public class AreaDataService extends Service {



    public interface BasicAreaInfoIface extends OnError {
		public void allDone(Float areaRank);

		public void cardReady(CardModel cm);

		/*public void onValueNotAvailable();*/

		public void onAreaNameFound(String name);

		public void onAreaBoundaryFound(double[][] poly);
	}

	public interface AreaLookupCallbacks extends OnError {
		public void areaReady(Area area);
	}

    public interface AreaListCallbacks extends OnError {
        public void areasReady(List<Area> areas);
    }

    public interface SubjectDumpIface extends OnError {
        public void subjectDumpReady(Map<DataSetFamily, Dataset> map);

        public void onProgress(int position, int size);
    }

    public interface DatasetDumpCallbacks extends OnError {
        public void datasetsDownloaded(HashMap<DateRange, Dataset> datasets);
    }

	public class AreaDataBinder extends Binder {
		public AreaDataService getService() {
			return AreaDataService.this;
		}
	}

	private final IBinder mBinder = new AreaDataBinder();

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("AreaDataService",
				"Binding! Received intent: " + intent.toString());
		return mBinder;
	}

    /**
     * Used by the Summary Fragment to generate the cards for the area
     * @param area area to get a summary for
     * @param callbacks callback interface for asynchronous return
     */
    public void generateCardsForArea(final Area area, final BasicAreaInfoIface callbacks) {
        new AsyncTask<Area, CardModel, Float>(){
            @Override
            protected void onProgressUpdate(CardModel... values) {
                super.onProgressUpdate(values);
                callbacks.cardReady(values[0]);
            }

            @Override
            protected Float doInBackground(Area... params) {
                Area theArea = params[0];
                callbacks.onAreaNameFound(theArea.getName());

                try {
                    CardModel demoCm = DemographicsCardProvider
                            .demographicsCardForArea(theArea,
                                    getResources());
                    publishProgress(demoCm);
                } catch (Exception e) {
                    Log.e("AreaDataService",
                            "Error processing card for Demographics: "
                                    + e.getClass().getSimpleName(), e);
                    callbacks.onError(e);
                }
                // 2: Crime
                try {
                    CardModel crimeCm = CrimeCardProvider.crimeCardForArea(
                            theArea, getResources());
                    double[][] polygon = (double[][]) crimeCm.getData();
                    if (polygon != null) {
                        callbacks.onAreaBoundaryFound(polygon);
                    }
                    publishProgress(crimeCm);
                } catch (Exception e) {
                    Log.e("AreaDataService",
                            "Error processing card for Crime: "
                                    + e.getClass().getSimpleName(), e);
                    callbacks.onError(e);
                }
                // 3: Economy
                try {
                    CardModel econCm = EconomyCardProvider
                            .economyCardForArea(theArea, getResources());
                    publishProgress(econCm);
                } catch (Exception e) {
                    Log.e("AreaDataService",
                            "Error processing card for Economy: "
                                    + e.getClass().getSimpleName(), e);
                    callbacks.onError(e);
                }
                // 4: Environment
                try {
                    CardModel envCm = EnvironmentCardProvider
                            .environmentCardForArea(theArea, getResources());
                    publishProgress(envCm);
                } catch (Exception e) {
                    Log.e("AreaDataService",
                            "Error processing card for Environment: "
                                    + e.getClass().getSimpleName(), e);
                    callbacks.onError(e);
                }
                try {
                    return AreaRank.forArea(area);
                } catch (Exception e) {
                    callbacks.onError(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Float areaRank) {
                super.onPostExecute(areaRank);
                callbacks.allDone(areaRank);
            }
        }.execute(area);
    }

	/**
	 * Fetches a MSOA area which encloses the supplied location
	 * 
	 * @param location
	 *            the user's location
	 * @param commlink
	 *            the callback interface
	 */
	public void areaForLocation(Location location,
			final AreaLookupCallbacks commlink) {
		new AsyncTask<Location, Void, Area>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();

			}

			@Override
			protected Area doInBackground(Location... params) {
				Location loc = params[0];

				Area theArea = null;
				try {
                    String postcode = Postcode.forLocation(loc);
                    Log.i("AreaDataService", "Got postcode: " + postcode);
					Set<Area> areaSet = new FindAreas()
							.ofLevelType(Area.LEVELTYPE_LOCAL_AUTHORITY)
							.inHierarchy(
									Area.HIERARCHY_2011_STATISTICAL_GEOGRAPHY)
							.forPostcode(postcode).execute();

					for (Area a : areaSet) {
						if (a.getLevelTypeId() == Area.LEVELTYPE_LOCAL_AUTHORITY) {
                            theArea = a;
                        }
					}

				} catch (Throwable tr) {
					commlink.onError(tr);
				}
				return theArea;
			}

			@Override
			protected void onPostExecute(Area result) {
				super.onPostExecute(result);

				commlink.areaReady(result);
			}
		}.execute(location);
	}

    /**
     * Finds the area for the given postcode
     * @param postcode postcode for the area
     * @param callbacks callbacks for async call
     */
    public void areaForPostcode(final String postcode, final AreaLookupCallbacks callbacks) {
        new AsyncTask<Void, Void, Area>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Area doInBackground(Void... params) {
                Area theArea = null;
                try {
                    Set<Area> areaSet = new FindAreas()
                            .ofLevelType(Area.LEVELTYPE_LOCAL_AUTHORITY)
                            .inHierarchy(
                                    Area.HIERARCHY_2011_STATISTICAL_GEOGRAPHY)
                            .forPostcode(postcode).execute();

                    for (Area a : areaSet) {
                        if (a.getLevelTypeId() == Area.LEVELTYPE_LOCAL_AUTHORITY) {
                            theArea = a;
                        }
                    }

                } catch (Throwable tr) {
                    callbacks.onError(tr);
                }
                return theArea;
            }

            @Override
            protected void onPostExecute(Area result) {
                super.onPostExecute(result);

                callbacks.areaReady(result);
            }
        }.execute();
    }

	/**
	 * Fetches an area whose name most closely matches the supplied string
	 * (partials accepted)
	 * 
	 * @param areaName the name (partials accepted) for the area to find
	 * @param commlink
	 *            the callback interface
	 */
	public void areaForName(String areaName,
			final AreaLookupCallbacks commlink) {
		new AsyncTask<String, Void, Area>() {

			@Override
			protected Area doInBackground(String... params) {
				try {
					Set<Area> areaSet = new FindAreas()
							.inHierarchy(
									Area.HIERARCHY_2011_STATISTICAL_GEOGRAPHY)
                            .ofLevelType(Area.HIERARCHY_2011_STATISTICAL_GEOGRAPHY)
							.whoseNameContains(params[0]).execute();
                    if(areaSet.size() == 0){
                        commlink.onError(new FileNotFoundException("No areas returned for " +
                                "query string \"" + params[0] + "\""));
                    }
					Area theArea = null;
					int topStringMatchScore = 0;
					for (Area a : areaSet) {
                        assert a != null;
						int nameScore = Statistics.computeLevenshteinDistance(
								theArea != null ? theArea.getName() : "", params[0]);
						if (nameScore > topStringMatchScore) {
							theArea = a;
							topStringMatchScore = nameScore;
						}
					}
					return theArea;
				} catch (Throwable tr) {
					commlink.onError(tr);
				}
				return null;
			}

			protected void onPostExecute(Area result) {
				commlink.areaReady(result);
			}

		}.execute(areaName);
	}

    /**
     * Gets a list of areas whose name contains the string areaName, ordered by the similarity
     * to that string.
     * @param areaName the name (partials accepted) for the area to find
     * @param commlink callbacks for returning the value or reporting errors.
     */
    public void areasForName(final String areaName,
                            final AreaListCallbacks commlink) {
        /**
         * Compares the "distances" of given areas' names from the original query. This makes it
         * possible to sort the area list without having to worry about sorting algorithms.
         */
        final Comparator<Area> areaComparator = new Comparator<Area>() {
            @Override
            public int compare(Area lhs, Area rhs) {
                int distanceLhs = Statistics.computeLevenshteinDistance(lhs.getName(), areaName);
                int distanceRhs = Statistics.computeLevenshteinDistance(rhs.getName(), areaName);
                if(distanceLhs == distanceRhs){
                    return 0; // both areas' names are the same distance from the query
                } else if(distanceLhs < distanceRhs) {
                    return -1; // lhs area's name is closer to the query
                } else {
                    return 1; // rhs area's name is closer to the query
                }
            }
        };

        new AsyncTask<String, Void, List<Area>>() {

            @Override
            protected List<Area> doInBackground(String... params) {
                try {
                    Set<Area> areaSet = new FindAreas()
                            .inHierarchy(
                                    Area.HIERARCHY_2011_STATISTICAL_GEOGRAPHY)
                            .ofLevelType(Area.LEVELTYPE_LOCAL_AUTHORITY)
                            .whoseNameContains(params[0]).execute();
                    if(areaSet.size() == 0){
                        commlink.onError(new FileNotFoundException("No areas returned for " +
                                "query string \"" + params[0] + "\""));
                    }
                    List<Area> areas = new ArrayList<Area>(areaSet);
                    Collections.sort(areas, areaComparator);
                    return areas;
                } catch (Throwable tr) {
                    commlink.onError(tr);
                }
                return null;
            }

            protected void onPostExecute(List<Area> areas){
                commlink.areasReady(areas);
            }

        }.execute(areaName);
    }

    /**
     * Fetches all the datasets in the given subject. May be slow the first time.
     * @param area The area to fetch information for
     * @param subjectName The subject name
     * @param commlink A callback interface
     */
    public void subjectDump(Area area, final String subjectName, final SubjectDumpIface commlink){
        new AsyncTask<Area, Void, Map<DataSetFamily, Dataset>>(){

            @Override
            protected Map<DataSetFamily, Dataset> doInBackground(Area... params) {
                Area area = params[0];
                assert area != null;
                Map<DataSetFamily, Dataset> result = new HashMap<DataSetFamily, Dataset>();
                try{
                    Subject subject = CensusHelpers.findSubject(area, subjectName);
                    List<DataSetFamily> families = new GetDatasetFamilies(subject).forArea(area).execute();
                    for(int i = 0; i < families.size(); i++){
                        commlink.onProgress(i + 1, families.size());
                        DataSetFamily family = families.get(i);
                        Set<Dataset> datasets = new GetTables().forArea(area).inFamily(family).execute();
                        if (datasets.size() != 1){
                            Log.wtf("AreaDataService", "Asked for 1 dataset, got "
                                    + datasets.size() + " instead.");
                            System.exit(1);
                        }
                        Dataset dataset = datasets.iterator().next();
                        result.put(family, dataset);
                    }
                } catch (Throwable tr) {
                    commlink.onError(tr);
                }
                return result;
            }

            @Override
            protected void onPostExecute(Map<DataSetFamily, Dataset> result) {
                super.onPostExecute(result);
                commlink.subjectDumpReady(result);
            }
        }.execute(area);
    }

    /**
     * Fetches all data for the dataset family given, referenced by the date ranges they cover.
     * @param area The area to fetch information for
     * @param family The dataset family to download
     * @param callbacks A callback interface
     */
    public void datasetDump(final Area area, final DataSetFamily family, final DatasetDumpCallbacks callbacks) {
        new AsyncTask<Void, Void, HashMap<DateRange, Dataset>>(){
            @Override
            protected HashMap<DateRange, Dataset> doInBackground(Void... params) {
                HashMap<DateRange, Dataset> dateRangeDatasetHashMap = new HashMap<DateRange, Dataset>();

                DateRange[] ranges = family.getDateRanges();
                for(DateRange range : ranges){
                    try {
                        Set<Dataset> current = new GetTables()
                                .forArea(area)
                                .inFamily(family)
                                .inDateRange(range)
                                .execute();
                        Dataset currentDataset = current.iterator().next();
                        dateRangeDatasetHashMap.put(range, currentDataset);
                    } catch (Exception e) {
                        callbacks.onError(e);
                    }
                }

                return dateRangeDatasetHashMap;
            }

            @Override
            protected void onPostExecute(HashMap<DateRange, Dataset> dateRangeDatasetHashMap) {
                callbacks.datasetsDownloaded(dateRangeDatasetHashMap);
            }
        }.execute();
    }
}
