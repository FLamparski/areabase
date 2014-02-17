package lamparski.areabase.map_support;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A custom {@link WebView} that loads a local copy of the Ordnance Survey map,
 * and then provides an interface so that the map can be interacted with from
 * Areabase.
 * <p>
 * OS has a lot of data about the UK, so it'd be silly not to use it.
 * Nevertheless, it takes extra effort to get it running.
 * 
 * @author filip
 * 
 */
@SuppressLint("SetJavaScriptEnabled")
public class OrdnanceSurveyMapView extends WebView {
	private OSMapWebViewClient my_client;
	private OSNativeInterface mNativeInterface;

	public OrdnanceSurveyMapView(Context context) {
		super(context);
		if (!(isInEditMode()))
			initOSView();
	}

	public OrdnanceSurveyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!(isInEditMode()))
			initOSView();
	}

	public OrdnanceSurveyMapView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		if (!(isInEditMode()))
			initOSView();
	}

	private void initOSView() {
		my_client = new OSMapWebViewClient();
		my_client
				.setOnIllegalURLListener(new OSMapWebViewClient.OnNonApplicationURLListener() {
					private Context context = getContext();

					/*
					 * So here's how it works: Theoretically, a malicious
					 * program may want to inject some code that would cause the
					 * WebView to navigate away from the slippymap and to a site
					 * pretending to be the app's UI element, requiring payment.
					 * In order to mitigate that, URLs that are not local
					 * (file:///) or OS API's are gutted and a dialogue warning
					 * the user is presented instead. Now, the user still has
					 * the option to visit the page, but in the system browser.
					 */
					@Override
					public void onIllegalUrl(final String url) {
						AlertDialog.Builder alert_bldr = new AlertDialog.Builder(
								context);
						alert_bldr
								.setTitle(0 /* R.string.error_illegal_url_title */)
								.setMessage(getResources().getString(0,
								// R.string.error_illegal_url_formatstring,
										url))
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setPositiveButton(0,
								// R.string.error_illegal_url_response_dontgo,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
											}
										}).setNegativeButton(0,
								// R.string.error_illegal_url_response_open,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												Uri uri = Uri.parse(url);
												Intent intent = new Intent(
														Intent.ACTION_VIEW, uri);
												context.startActivity(intent);
											}
										});
						alert_bldr.show();
					}

					@Override
					public void onOpenExternalSafeUrl(String url) {
						Uri uri = Uri.parse(url);
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						context.startActivity(intent);
					}
				});
		setWebViewClient(my_client);

		// Required, as Ordnance Survey is built on top of OpenLayers.
		getSettings().setJavaScriptEnabled(true);
		loadUrl("file:///android_asset/os_openspace/slippymap.html");

		mNativeInterface = new OSNativeInterface(getContext());
		addJavascriptInterface(mNativeInterface, "AreabaseNative");
	}

	public void setOnMapLoadedListener(
			OSNativeInterface.OnMapLoadedListener listener) {
		mNativeInterface._setOnMapLoadedListener(listener);
	}

	public void highlightArea(String areaId, String adminUnit,
			HoloCSSColourValues lineColour, HoloCSSColourValues fillColour) {
		loadUrl(String.format(
				"javascript:wrapper__highlightArea(%s, %s, %s, %s)", areaId,
				adminUnit, lineColour.getCssValue(), fillColour.getCssValue()));
	}

	public void highlightArea(String areaId, String adminUnit) {
		highlightArea(areaId, adminUnit, HoloCSSColourValues.AQUAMARINE,
				HoloCSSColourValues.CYAN);
	}

	/**
	 * Centre the map on a specific point given by its easting and northing, at
	 * the same zoom level.
	 * 
	 * @param easting
	 * @param northing
	 */
	public void setCentre_byEastingNorthing(Double easting, Double northing) {
		loadUrl(String
				.format("javascript:wrapper__setCentre_keepZoom_eastingNorthing(%f, %f)",
						easting, northing));
	}

	/**
	 * Centre the map on a specific point given by its longitude and latitude,
	 * at the same zoom level.
	 * 
	 * @param lon
	 *            Longitude to centre on
	 * @param lat
	 *            Latitude to centre on
	 */
	public void setCentre(Double lon, Double lat) {
		loadUrl(String.format(
				"javascript:wrapper__setCentre_keepZoom_WGS84lonlat(%f, %f)",
				lon, lat));
	}

	/**
	 * Centre the map on a specific {@link Location} at the same zoom level.
	 * 
	 * @param location
	 *            Location to centre on.
	 */
	public void setCentre(Location location) {
		setCentre(location.getLongitude(), location.getLatitude());
	}

	public void setZoom(int zoomLevel) {
		loadUrl(String.format("javascript:wrapper__setZoom(%d)", zoomLevel));
	}

	public void highlightBoundary(double[][] poly) {
		Gson gson = new GsonBuilder().create(); String jsonPoly =
		gson.toJson(poly);
		Log.v("highlightBoundary", jsonPoly);
		loadUrl(String.format("javascript:wrapper__drawPoly(\"%s\")", jsonPoly));
		
		return;
	}
}
