package lamparski.areabase;

import lamparski.areabase.map_support.OrdnanceSurveyMapView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class SummaryFragment extends SherlockFragment {

	private OrdnanceSurveyMapView mOpenSpaceView;

	public SummaryFragment() {
		super();
	}

	@SuppressLint("SetJavaScriptEnabled")
	// ^ This is to tell ADK that I know what I'm doing
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View theView = inflater.inflate(R.layout.fragment_summary, container,
				false);

		mOpenSpaceView = new OrdnanceSurveyMapView(getActivity());

		return theView;
	}

}
