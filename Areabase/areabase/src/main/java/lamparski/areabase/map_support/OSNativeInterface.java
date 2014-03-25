package lamparski.areabase.map_support;
/** !license-block 
    This file is part of Areabase.

    Areabase is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Areabase is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Areabase.  If not, see <http://www.gnu.org/licenses/>.

    Areabase (C) 2013-2014 Filip Wieland <filiplamparski@gmail.com>
*/
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
