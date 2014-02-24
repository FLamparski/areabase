package lamparski.areabase.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.fima.cardsui.objects.CardModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lamparski.areabase.cardproviders.CrimeCardProvider;
import lamparski.areabase.cardproviders.DemographicsCardProvider;
import lamparski.areabase.cardproviders.EconomyCardProvider;
import lamparski.areabase.cardproviders.EnvironmentCardProvider;
import nde2.helpers.CensusHelpers;
import nde2.helpers.Statistics;
import nde2.pull.methodcalls.delivery.GetTables;
import nde2.pull.methodcalls.discovery.FindAreas;
import nde2.pull.methodcalls.discovery.GetDatasetFamilies;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.Dataset;
import nde2.pull.types.Subject;

/**
 * For now, this will simply fetch the {@link Area} data for whatever
 * {@link Location} is passed to the getArea method.
 * 
 * @author filip
 * 
 */
public class AreaDataService extends Service {

    public interface BasicAreaInfoIface {
		public void allDone();

		public void cardReady(CardModel cm);

		public void onError(Throwable err);

		public void onValueNotAvailable();

		public void onAreaNameFound(String name);

		public void onAreaBoundaryFound(double[][] poly);
	}

	public interface AreaLookupCallbacks {
		public void areaReady(Area area);

		public void onError(Throwable err);
	}

    public interface SubjectDumpIface {
        public void subjectDumpReady(Map<DataSetFamily, Dataset> map);

        public void onProgress(int position, int size);

        public void onError(Throwable tr);
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

    public void generateCardsForArea(Area area, final BasicAreaInfoIface callbacks) {
        new AsyncTask<Area, CardModel, Void>(){
            @Override
            protected void onProgressUpdate(CardModel... values) {
                super.onProgressUpdate(values);
                callbacks.cardReady(values[0]);
            }

            @Override
            protected Void doInBackground(Area... params) {
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
                    if (polygon != null)
                        callbacks.onAreaBoundaryFound(polygon);
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
                    return null;
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
                    return null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                callbacks.allDone();
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
                    String postcode = com.uk_postcodes.api.Geocoder.postcodeForLocation(loc);

					Set<Area> areaSet = new FindAreas()
							.ofLevelType(Area.LEVELTYPE_MSOA)
							.inHierarchy(
									Area.HIERARCHY_2011_STATISTICAL_GEOGRAPHY)
							.forPostcode(postcode).execute();

					for (Area a : areaSet) {
						if (a.getLevelTypeId() == Area.LEVELTYPE_MSOA)
							theArea = a;
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
	 * Fetches an area whose name most closely matches the supplied string
	 * (partials accepted)
	 * 
	 * @param areaName
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
							.whoseNameContains(params[0]).execute();
					Area theArea = null;
					int topStringMatchScore = 0;
					for (Area a : areaSet) {
						int nameScore = Statistics.computeLevenshteinDistance(
								theArea.getName(), params[0]);
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
                        assert datasets.size() == 1;
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
}
