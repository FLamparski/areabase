package lamparski.areabase.dummy.mockup_classes;

import lamparski.areabase.AreaActivity;
import lamparski.areabase.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Dummy fragment. Shows an integer.
 * 
 * @author filip
 * 
 */
@DummyData(why = "Exists to provide test fragments for navigation testing", replace_with = "Do not use in live app.")
public class DemoObjectFragment extends Fragment {
	public static final String ARGUMENT = "object";

	public DemoObjectFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myView = inflater.inflate(R.layout.fragment_collection_object,
				container, false);
		Bundle args = getArguments();
		((TextView) myView.findViewById(R.id.objectIdTextView))
				.setText(AreaActivity.TAB_NAMES[args.getInt(ARGUMENT)]);
		getActivity().setTitle(AreaActivity.TAB_NAMES[args.getInt(ARGUMENT)]);
		return myView;
	}

}
