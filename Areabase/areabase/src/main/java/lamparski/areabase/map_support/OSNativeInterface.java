package lamparski.areabase.map_support;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * This is an interface that allows the Ordnance Survey slippy map to call
 * native methods.
 * 
 * @author filip
 * 
 */
public class OSNativeInterface {
	public interface OnMapLoadedListener {
		public void onMapLoaded();
	}

	private Context context;
	private OnMapLoadedListener loadedCallback;

	public OSNativeInterface(Context context) {
		this.context = context;
	}

	/**
	 * <b>Call from Java code</b>: Sets a callback to be executed when the maps
	 * finish loading.
	 * 
	 * @param listener
	 *            the callback
	 */
	public void _setOnMapLoadedListener(OnMapLoadedListener listener) {
		loadedCallback = listener;
	}

	/**
	 * <b>Call from JavaScript code</b>: Notifies the program that the map has
	 * finished loading.
	 */
	@JavascriptInterface
	public void onMapLoaded() {
		Log.d("OSNativeInterface", "onMapLoaded() -> calling the listener");
		if (loadedCallback != null) {
            loadedCallback.onMapLoaded();
        } else {
            Log.w("OSNativeInterface",
                    "onMapLoaded() -> no registered listener");
        }
	}

	@JavascriptInterface
	public void consoleLog(String msg) {
		Log.d("slippymap.html [JS]", msg);
	}

	@JavascriptInterface
	public void consoleInfo(String msg) {
		Log.i("slippymap.html [JS]", msg);
	}

	@JavascriptInterface
	public void consoleWarn(String msg) {
		Log.w("slippymap.html [JS]", msg);
	}

	@JavascriptInterface
	public void consoleErr(String msg) {
		Log.e("slippymap.html [JS]", msg);
	}

}
