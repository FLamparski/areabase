package lamparski.areabase.services;

import java.util.Set;

import lamparski.areabase.cardproviders.DemographicsCardProvider;
import nde2.errors.ValueNotAvailable;
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

	// private BasicAreaInfoIface commlink;

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
		// this.commlink = commlink;
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
				Location loc = params[0];
				Geocoder gcoder = new Geocoder(getApplicationContext());
				try {
					long tGeocoder_s = System.currentTimeMillis();
					Address address = gcoder.getFromLocation(loc.getLatitude(),
							loc.getLongitude(), 1).get(0);
					long tGeocoder_e = System.currentTimeMillis();
					Log.i("AreaDataService", "[Geocoder] Took "
							+ (tGeocoder_e - tGeocoder_s)
							+ " ms to resolve address");

					long tAreas_s = System.currentTimeMillis();
					Set<Area> areaSet = new FindAreas()
							.ofLevelType(Area.LEVELTYPE_LOCAL_AUTHORITY)
							.inHierarchy(
									Area.HIERARCHY_2011_STATISTICAL_GEOGRAPHY)
							.forPostcode(address.getPostalCode()).execute();
					long tAreas_e = System.currentTimeMillis();
					Log.i("AreaDataService", "[FindAreas] Took "
							+ (tAreas_e - tAreas_s) + " ms to find NeSS Areas");
					// 1: Demographics
					try {
						CardModel demoCm = DemographicsCardProvider
								.demographicsCardForArea(areaSet.iterator()
										.next(), getResources());
						publishProgress(demoCm);
					} catch (Exception e) {
						Log.e("AreaDataService",
								"Error processing card for Demographics: "
										+ e.getClass().getSimpleName(), e);
						commlink.onError(e);
					}
					// 2: Crime

					// 3: Deprivation
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

}
