package lamparski.areabase.dummy.mockup_classes;

import lamparski.areabase.R;
import android.app.Fragment;
import android.os.Bundle;
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
