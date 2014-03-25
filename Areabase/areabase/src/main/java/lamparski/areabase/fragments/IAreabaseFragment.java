package lamparski.areabase.fragments;
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
	 * @param query the query string entered by the user
	 */
	public void searchByText(String query);
}
