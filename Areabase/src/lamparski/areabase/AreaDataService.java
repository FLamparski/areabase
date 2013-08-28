package lamparski.areabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.discovery.FindAreasMethodCall;
import nde2.types.discovery.Area;
import nde2.types.discovery.Subject;

import org.xml.sax.SAXException;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Debug;
import android.os.IBinder;
import android.util.Log;

/**
 * For now, this will simply fetch the {@link Area} data for whatever
 * {@link Location} is passed to the getArea method.
 * 
 * @author filip
 * 
 */
public class AreaDataService extends Service {
	private final IBinder mBinder = new AreaDataBinder();

	public class AreaDataBinder extends Binder {
		public AreaDataService getService() {
			return AreaDataService.this;
		}
	}

	public interface AreaFetchedCallbacks {
		public void onError(Throwable tr);

		public void onSuccess(List<Area> resultList);
	}

	public interface TopicsFoundCallbacks {
		public void onError(Throwable tr);

		public void onSuccess(List<Map<Subject, Integer>> results);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("AreaDataService",
				"Binding! Received intent: " + intent.toString());
		return mBinder;
	}

	/**
	 * Asynchronously fetches a bunch of areas for the given location.
	 * 
	 * <p>
	 * Behind the scenes, this process looks like this:
	 * <ol>
	 * <li>A Geocoder is instantiated
	 * <li>The closest address is fetched from the Geocoder for the location
	 * <li>A postcode-based query is constructed for the call, and then executed
	 * <li>If successful, the query is passed to the onSuccess callback of
	 * iface. </ul>
	 * <p>
	 * Should any errors occur, the onError callback is fired instead with a
	 * Throwable which can be used to get the appropriate error message.
	 * 
	 * @param whatLocation
	 * @param iface
	 */
	public void getAreas(Location whatLocation, final AreaFetchedCallbacks iface) {
		Log.i("AreaDataService", "getAreas(): Launched a query for Location "
				+ whatLocation.toString());
		new AsyncTask<Location, Void, List<Area>>() {
			@Override
			protected List<Area> doInBackground(Location... params) {
				Debug.startMethodTracing("getAreasOperation.trace");

				Log.d("AreaDataService", "getAreas(): Started async query");
				List<Area> theAreas = new ArrayList<Area>();
				if (Geocoder.isPresent()) {
					Geocoder g = new Geocoder(getApplicationContext());
					try {
						List<Address> addresses = g.getFromLocation(
								params[0].getLatitude(),
								params[0].getLongitude(), 1);
						theAreas = new FindAreasMethodCall().addPostcode(
								addresses.get(0).getPostalCode()).findAreas();
						Log.d("AreaDataService",
								"getAreas(): Guys we have areas!!!");
					} catch (IOException e) {
						iface.onError(e);
						return null;
					} catch (XPathExpressionException e) {
						iface.onError(e);
						return null;
					} catch (NDE2Exception e) {
						iface.onError(e);
						return null;
					} catch (ParserConfigurationException e) {
						iface.onError(e);
						return null;
					} catch (SAXException e) {
						iface.onError(e);
						return null;
					} catch (ValueNotAvailable e) {
						iface.onError(e);
						return null;
					}
				} else {
					Log.e("AreaDataService",
							"OKAY WHAT. No Geocoder found. Abort.");
					iface.onError(new Exception("Geocoder not present."));
					return null;
				}

				Debug.stopMethodTracing();
				return theAreas;
			}

			@Override
			protected void onPostExecute(List<Area> result) {
				if (result != null)
					iface.onSuccess(result);
			}
		}.execute(whatLocation);
	}

	public void getCompatibleTopics(final TopicsFoundCallbacks iface,
			Area... areas) {
		new AsyncTask<Area, Void, List<Map<Subject, Integer>>>() {

			@Override
			protected List<Map<Subject, Integer>> doInBackground(Area... params) {
				List<Map<Subject, Integer>> theSubjectsForAreas = new ArrayList<Map<Subject, Integer>>();
				try {
					for (Area area : params) {
						theSubjectsForAreas.add(area.getCompatibleSubjects());
					}
				} catch (SAXException e) {
					iface.onError(e);
					return null;
				} catch (XPathExpressionException e) {
					iface.onError(e);
					return null;
				} catch (ParserConfigurationException e) {
					iface.onError(e);
					return null;
				} catch (IOException e) {
					iface.onError(e);
					return null;
				} catch (NDE2Exception e) {
					iface.onError(e);
					return null;
				}
				return theSubjectsForAreas;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(List<Map<Subject, Integer>> result) {
				if (result != null)
					iface.onSuccess(result);
			}

		}.execute(areas);
	}

}
