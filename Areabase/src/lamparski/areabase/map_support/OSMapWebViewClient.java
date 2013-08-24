package lamparski.areabase.map_support;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A custom {@link WebViewClient} that is used by {@link OrdnanceSurveyMapView}
 * to restrict the URLs that the latter can load to those belonging to Ordnance
 * Survey's OpenSpace map system only (though file:/// URLs have to be permitted
 * as well). This is to prevent rogue JavaScript being injected into the widget,
 * which could for instance ask for login details to a Google account.
 * <p>
 * Of course, if the attacker spoofs DNS as well, they could probably pull it
 * off, but there's a limit to what I can do to stop that.
 * 
 * @author filip
 * 
 */
public class OSMapWebViewClient extends WebViewClient {
	public interface OnNonApplicationURLListener {
		public void onIllegalUrl(String url);

		public void onOpenExternalSafeUrl(String url);
	}

	private OnNonApplicationURLListener mCallback;

	public OSMapWebViewClient() {
		super();
	}

	public void setOnIllegalURLListener(OnNonApplicationURLListener listener) {
		mCallback = listener;
	}

	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (!(url.startsWith("http://openspace.ordnancesurvey.co.uk/") | url
				.startsWith("file:///"))) {
			Log.i("OSMapView client", "Caught non-application url: " + url);
			if (mCallback != null) {
				if (url.startsWith("http://www.ordnancesurvey.co.uk/")) {
					/*
					 * http://www.ordnancesurvey.co.uk/oswebsite/web-services/os-
					 * openspace/developer-agreement.html is safe and should
					 * just open a browser window
					 */
					Log.d("OSMapView client",
							" > Safe URL, firing onOpenExternalSafeUrl.");
					mCallback.onOpenExternalSafeUrl(url);
				} else {
					Log.d("OSMapView client",
							" > Unsafe URL, firing onIllegalUrl");
					mCallback.onIllegalUrl(url);
				}
			}
			return true;
		} else {
			return false;
		}
	}

}
