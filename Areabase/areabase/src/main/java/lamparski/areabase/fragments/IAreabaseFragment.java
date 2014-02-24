package lamparski.areabase.fragments;

import android.support.v4.app.Fragment;

import lamparski.areabase.AreaActivity;

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
	 * On fragments that support it, perform a text-based search. Called when
	 * the user performs the "Search" action on the parent {@link AreaActivity}.
	 * 
	 * @param query
	 */
	public void searchByText(String query);
}
