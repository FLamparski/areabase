package lamparski.areabase.map_support;

import android.content.Context;

/**
 * This is an interface that allows the Ordnance Survey slippy map to call
 * native methods.
 * 
 * @author filip
 * 
 */
public class OSNativeInterface {
	private Context context;

	public OSNativeInterface(Context context) {
		this.context = context;
	}

}
