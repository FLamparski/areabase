package lamparski.areabase.map_support;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lamparski.areabase.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.WebView;

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
				.setOnIllegalURLListener(new OSMapWebViewClient.OnIllegalURLListener() {
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
								.setTitle(R.string.error_illegal_url_title)
								.setMessage(
										getResources()
												.getString(
														R.string.error_illegal_url_formatstring,
														url))
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setPositiveButton(
										R.string.error_illegal_url_response_dontgo,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
											}
										})
								.setNegativeButton(
										R.string.error_illegal_url_response_open,
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
				});
		setWebViewClient(my_client);
		// Required, as Ordnance Survey is built on top of OpenLayers.
		getSettings().setJavaScriptEnabled(true);
		loadUrl("file:///android_assets/os_openspace.slippymap.html");
		addJavascriptInterface(new OSNativeInterface(getContext()),
				"AreabaseNative");
	}

	private void reloadMap(Map<String, String> qParams) {
		/*
		 * Build a URL which for the method call O HAI code from
		 * nde2.methodcalls.BaseMethodCall
		 */
		StringBuilder methodCallStrBuilder = new StringBuilder(
				"file:///android_assets/os_openspace.slippymap.html");
		methodCallStrBuilder.append("?");
		Set<Entry<String, String>> paramEntries = qParams.entrySet();
		for (Entry<String, String> param : paramEntries) {
			methodCallStrBuilder
					.append(param.getKey() + "=" + param.getValue())
					.append("&");
		}
		String callUrlStr = methodCallStrBuilder.toString();
		// Cut the trailing & if present.
		if (callUrlStr.endsWith("&"))
			callUrlStr = callUrlStr.substring(0, callUrlStr.length() - 1);

		loadUrl(callUrlStr);
	}

	public void highlightArea(String areaId, String adminUnit,
			HoloCSSColourValues lineColour, HoloCSSColourValues fillColour) {
		Hashtable<String, String> q = new Hashtable<String, String>();
		q.put("AREA_ID", areaId);
		q.put("ADM_UNIT", adminUnit);
		q.put("COLOURS",
				String.format("%s:%s", lineColour.getCssValue(),
						fillColour.getCssValue()));
		reloadMap(q);
	}

	public void highlightArea(String areaId, String adminUnit) {
		highlightArea(areaId, adminUnit, HoloCSSColourValues.AQUAMARINE,
				HoloCSSColourValues.CYAN);
	}
}
