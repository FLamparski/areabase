package lamparski.areabase.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

/**
 * This Service wraps the Android Location service and provides an interface
 * that is more suited to Areabase's needs. Location listening can be started
 * and stopped at any time, and the best possible location is cached here.
 * 
 * @author filip
 * 
 */
public class AreabaseLocatorService extends Service implements LocationListener {

	private LocationManager mLocationManager = (LocationManager) this
			.getSystemService(LOCATION_SERVICE);
	private Location previously_used_location;
	private Location location;
	private boolean listening;

	private final static int TWO_MINUTES = 2 * 60 * 1000;

	public class AreabaseLocatorBinder extends Binder {
		public AreabaseLocatorService getService() {
			return AreabaseLocatorService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onLocationChanged(android.location.
	 * Location)
	 */
	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String,
	 * int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public void startLocationListening() {
		// Let's freeload, by using location that was requested by another
		// process.
		mLocationManager.requestLocationUpdates(
				LocationManager.PASSIVE_PROVIDER, 0, 0, this);
		// Alternatively, use network geolocation. It might just place the phone
		// in the right Super Output Area.
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 1000, 0, this);
		// And let's pull out the big guns:
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 0, this);
		listening = true;
	}

	public void stopLocationListening() {
		mLocationManager.removeUpdates(this);
		listening = false;
	}

	public Location getLocation() {
		previously_used_location = location;
		return location;
	}

	public boolean isListening() {
		return listening;
	}

	public boolean hasBetterLocation() {
		return isBetterLocation(location, previously_used_location);
	}

	private boolean isBetterLocation(Location sample, Location reference) {
		// Anything is better than null.
		if (reference == null)
			return true;
		if (sample == null)
			return false;

		long timeDelta = sample.getTime() - reference.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		int accuracyDelta = (int) (sample.getAccuracy() - reference
				.getAccuracy());
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		boolean isSameOrigin = false;
		if (sample.getProvider() == null)
			isSameOrigin = (reference.getProvider() == null);
		else
			isSameOrigin = sample.getProvider().equals(reference.getProvider());

		if (isMoreAccurate)
			return true;
		else if (isNewer && !isLessAccurate)
			return true;
		else if (isNewer && !isSignificantlyLessAccurate && isSameOrigin)
			return true;
		else if (isSignificantlyNewer)
			return true;
		else if (isSignificantlyOlder)
			return false;
		else
			return false;
	}
}
