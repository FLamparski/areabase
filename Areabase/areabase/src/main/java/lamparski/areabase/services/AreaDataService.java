package lamparski.areabase.services;

import java.util.Set;

import lamparski.areabase.cardproviders.CrimeCardProvider;
import lamparski.areabase.cardproviders.DemographicsCardProvider;
import lamparski.areabase.cardproviders.EconomyCardProvider;
import lamparski.areabase.cardproviders.EnvironmentCardProvider;
import nde2.errors.ValueNotAvailable;
import nde2.helpers.Statistics;
import nde2.pull.methodcalls.discovery.FindAreas;
import nde2.pull.types.Area;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.fima.cardsui.objects.CardModel;

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

	public interface DetailViewAreaInfoIface {
		public void areaReady(Area area);

		public void onError(Throwable err);
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
	 * This function will spin off a background thread that updates cards as new
	 * data is coming in from ONS.
	 * 
	 * @param location
	 *            The location to search for.
	 * @param commlink
	 *            A communication interface between this service and host
	 *            Activity or Fragment.
	 */
	public void generateCardsForLocation(Location location,
			final BasicAreaInfoIface commlink) {
		Log.d("AreaDataService",
				"generateCardsForLocation(" + location.toString() + ")");
		new AsyncTask<Location, CardModel, Void>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				/*
				 * A Geocoder is required for this operation to complete. If
				 * there is no Geocoder present, we won't even bother with
				 * spinning off a new thread (performance, resources, but mostly
				 * the new thread would have nothing to do).
				 */
				if (!(Geocoder.isPresent())) {
					commlink.onError(new Exception(
							"Geocoder not found. Missing Play Services library?"));
					cancel(true);
				}
			}

			@Override
			protected Void doInBackground(Location... params) {
				Thread.currentThread().setName("AreaDataService thread");
				Location loc = params[0];
				Geocoder gcoder = new Geocoder(getApplicationContext());
				try {
					long tGeocoder_s = System.currentTimeMillis();
					Address address = gcoder.getFromLocation(loc.getLatitude(),
							loc.getLongitude(), 10).get(0);
					long tGeocoder_e = System.currentTimeMillis();
					Log.i("AreaDataService", "[Geocoder] Took "
							+ (tGeocoder_e - tGeocoder_s)
							+ " ms to resolve address");

					for (int i = 0; i < 11; i++) {
						String msg = String
								.format("The #%d address is %s, %s %s", i + 1,
										address.getAddressLine(0),
										address.getPostalCode(),
										address.getAdminArea());
						Log.v("AreaDataService", msg);
					}

					long tAreas_s = System.currentTimeMillis();
					Set<Area> areaSet = new FindAreas()
							.ofLevelType(Area.LEVELTYPE_MSOA)
							.inHierarchy(
									Area.HIERARCHY_2011_STATISTICAL_GEOGRAPHY)
							.forPostcode(address.getPostalCode()).execute();
					long tAreas_e = System.currentTimeMillis();
					Log.i("AreaDataService", "[FindAreas] Took "
							+ (tAreas_e - tAreas_s) + " ms to find NeSS Areas");
					// 1: Demographics
					Area theArea = null;
					for (Area a : areaSet) {
						if (a.getLevelTypeId() == Area.LEVELTYPE_MSOA)
							theArea = a;
					}

					commlink.onAreaNameFound(theArea.getName());

					try {
						CardModel demoCm = DemographicsCardProvider
								.demographicsCardForArea(theArea,
										getResources());
						publishProgress(demoCm);
					} catch (Exception e) {
						Log.e("AreaDataService",
								"Error processing card for Demographics: "
										+ e.getClass().getSimpleName(), e);
						commlink.onError(e);
					}
					// 2: Crime
					try {
						CardModel crimeCm = CrimeCardProvider.crimeCardForArea(
								theArea, getResources());
						double[][] polygon = (double[][]) crimeCm.getData();
						if (polygon != null)
							commlink.onAreaBoundaryFound(polygon);
						publishProgress(crimeCm);
					} catch (Exception e) {
						Log.e("AreaDataService",
								"Error processing card for Crime: "
										+ e.getClass().getSimpleName(), e);
						commlink.onError(e);
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
						commlink.onError(e);
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
						commlink.onError(e);
						return null;
					}
				} catch (ValueNotAvailable e) {
					commlink.onValueNotAvailable();
					return null;
				} catch (Exception e) {
					commlink.onError(e);
					return null;
				}
				return null;
			}

			@Override
			protected void onProgressUpdate(CardModel... values) {
				for (CardModel m : values) {
					commlink.cardReady(m);
				}
			}

			protected void onPostExecute(Void result) {
				commlink.allDone();
			}

		}.execute(location);
	}

	/**
	 * Spins off a thread that will generate cards for the area whose name is
	 * the closest match for areaName
	 * 
	 * @param areaName
	 *            the name of the area to look for (partials accepted)
	 * @param commlink
	 *            A communication interface between this serivce and the host
	 */
	public void generateCardsForAreaName(String areaName,
			final BasicAreaInfoIface commlink) {
		new AsyncTask<String, CardModel, Void>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();

			}

			@Override
			protected Void doInBackground(String... params) {
				Thread.currentThread().setName("AreaDataService thread");
				try {
					Set<Area> areaSet = new FindAreas()
							.inHierarchy(
									Area.HIERARCHY_2011_STATISTICAL_GEOGRAPHY)
							.whoseNameContains(params[0]).execute();
					// 1: Demographics
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

					commlink.onAreaNameFound(theArea.getName());

					try {
						CardModel demoCm = DemographicsCardProvider
								.demographicsCardForArea(theArea,
										getResources());
						publishProgress(demoCm);
					} catch (Exception e) {
						Log.e("AreaDataService",
								"Error processing card for Demographics: "
										+ e.getClass().getSimpleName(), e);
						commlink.onError(e);
					}
					// 2: Crime
					try {
						CardModel crimeCm = CrimeCardProvider.crimeCardForArea(
								theArea, getResources());
						double[][] polygon = (double[][]) crimeCm.getData();
						if (polygon != null)
							commlink.onAreaBoundaryFound(polygon);
						publishProgress(crimeCm);
					} catch (Exception e) {
						Log.e("AreaDataService",
								"Error processing card for Crime: "
										+ e.getClass().getSimpleName(), e);
						commlink.onError(e);
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
						commlink.onError(e);
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
						commlink.onError(e);
						return null;
					}
				} catch (ValueNotAvailable e) {
					commlink.onValueNotAvailable();
					return null;
				} catch (Exception e) {
					commlink.onError(e);
					return null;
				}
				return null;
			}

			@Override
			protected void onProgressUpdate(CardModel... values) {
				for (CardModel m : values) {
					commlink.cardReady(m);
				}
			}

			protected void onPostExecute(Void result) {
				commlink.allDone();
			}

		}.execute(areaName);
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
			final DetailViewAreaInfoIface commlink) {
		new AsyncTask<Location, Void, Area>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				/*
				 * A Geocoder is required for this operation to complete. If
				 * there is no Geocoder present, we won't even bother with
				 * spinning off a new thread (performance, resources, but mostly
				 * the new thread would have nothing to do).
				 */
				if (!(Geocoder.isPresent())) {
					commlink.onError(new Exception(
							"Geocoder not found. Missing Play Services library?"));
					cancel(true);
				}
			}

			@Override
			protected Area doInBackground(Location... params) {
				Location loc = params[0];
				Geocoder gcoder = new Geocoder(getApplicationContext());
				Area theArea = null;
				try {
					Address address = gcoder.getFromLocation(loc.getLatitude(),
							loc.getLongitude(), 10).get(0);

					for (int i = 0; i < 11; i++) {
						String msg = String
								.format("The #%d address is %s, %s %s", i + 1,
										address.getAddressLine(0),
										address.getPostalCode(),
										address.getAdminArea());
						Log.v("AreaDataService", msg);
					}

					Set<Area> areaSet = new FindAreas()
							.ofLevelType(Area.LEVELTYPE_MSOA)
							.inHierarchy(
									Area.HIERARCHY_2011_STATISTICAL_GEOGRAPHY)
							.forPostcode(address.getPostalCode()).execute();

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
			final DetailViewAreaInfoIface commlink) {
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
}
