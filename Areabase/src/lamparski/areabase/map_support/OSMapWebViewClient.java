package lamparski.areabase.map_support;

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
	public interface OnIllegalURLListener {
		public void onIllegalUrl(String url);
	}

	private OnIllegalURLListener mCallback;

	public OSMapWebViewClient() {
		super();
	}

	public void setOnIllegalURLListener(OnIllegalURLListener listener) {
		mCallback = listener;
	}

	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (!(url.startsWith("http://openspace.ordnancesurvey.co.uk/") | url
				.startsWith("file:///"))) {
			if (mCallback != null) {
				mCallback.onIllegalUrl(url);
			}
			return true;
		} else {
			return false;
		}
	}

}
