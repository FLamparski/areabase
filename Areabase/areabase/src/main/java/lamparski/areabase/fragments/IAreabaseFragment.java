package lamparski.areabase.fragments;

import lamparski.areabase.AreaActivity;
import android.location.Location;
import android.support.v4.app.Fragment;

/**
 * Specifies the methods that any {@link Fragment} that wants to call itself an
 * Areabase content fragment must implement, even as no-ops. Also means that the
 * fragments can have some degree of autonomy, and {@link AreaActivity} only has
 * to know that they're self-respecting content fragments.
 * 
 * @author filip
 * 
 */
public interface IAreabaseFragment {
	/**
	 * On fragments that support it, refresh the current content. Called when
	 * the user selects the "Refresh" action on the parent {@link AreaActivity}.
	 */
	public void refreshContent();

	/**
	 * On fragments that support it, update the geographic location that the
	 * user is in. Called when the user selects the "Locate" action on the
	 * parent {@link AreaActivity}.
	 * 
	 * @param result
	 */
	public void updateGeo(Location result);

	/**
	 * On fragments that support it, perform a text-based search. Called when
	 * the user performs the "Search" action on the parent {@link AreaActivity}.
	 * 
	 * @param query
	 */
	public void searchByText(String query);
}
