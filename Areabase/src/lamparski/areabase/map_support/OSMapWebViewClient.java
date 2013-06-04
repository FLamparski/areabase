package lamparski.areabase.map_support;

import android.webkit.WebView;
import android.webkit.WebViewClient;

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
