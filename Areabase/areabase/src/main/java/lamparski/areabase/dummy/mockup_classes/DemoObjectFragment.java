package lamparski.areabase.dummy.mockup_classes;
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
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lamparski.areabase.R;

/**
 * Dummy fragment. Shows an integer.
 * 
 * @author filip
 * 
 */
@Deprecated
public class DemoObjectFragment extends Fragment {
	public static final String ARGUMENT = "fragment-content";
	public static final String ARGUMENT2 = "fragment-title";

	public DemoObjectFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myView = inflater.inflate(R.layout.fragment_collection_object,
				container, false);
		Bundle args = getArguments();
		((TextView) myView.findViewById(R.id.objectIdTextView)).setText(args
				.getString(ARGUMENT));
		getActivity().setTitle(args.getString(ARGUMENT2));
		return myView;
	}

}
