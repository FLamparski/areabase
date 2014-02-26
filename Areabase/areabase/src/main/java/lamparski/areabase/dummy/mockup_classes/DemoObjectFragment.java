package lamparski.areabase.dummy.mockup_classes;

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
