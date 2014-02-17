package lamparski.areabase.fragments;

import static lamparski.areabase.widgets.CommonDialogs.serviceCockupNotify;
import lamparski.areabase.services.AreaDataService;
import lamparski.areabase.services.AreaDataService.AreaDataBinder;
import lamparski.areabase.services.AreaDataService.DetailViewAreaInfoIface;
import nde2.pull.types.Area;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class DetailViewFragment extends Fragment implements
		IAreabaseFragment {

	protected Area area;
	protected String subjectName;

	protected AreaDataService mService;
	protected boolean isServiceBound, is_live;
	protected ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			if (is_live) {
				Log.e("SummaryFragment",
						"The AreaDataService disconnected unexpectedly.");
				isServiceBound = false;
				serviceCockupNotify(name, getActivity());
			}
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("SummaryFragment", "AreaDataService connected");
			AreaDataBinder binder = (AreaDataBinder) service;
			mService = binder.getService();
			isServiceBound = true;
		}
	};

	protected DetailViewAreaInfoIface mDetailViewAreaInfoIface = new DetailViewAreaInfoIface() {

		@Override
		public void onError(Throwable err) {
			// Do something about it
		}

		@Override
		public void areaReady(Area area) {
			DetailViewFragment.this.area = area;
			refreshContent();
		}
	};

	@Override
	public abstract void refreshContent();

	@Override
	public void updateGeo(Location location) {
		mService.areaForLocation(location, mDetailViewAreaInfoIface);
	}

	@Override
	public void searchByText(String query) {
		mService.areaForName(query, mDetailViewAreaInfoIface);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		is_live = true;
		Intent intent = new Intent(getActivity(), AreaDataService.class);
		getActivity().getApplicationContext().bindService(intent,
				mServiceConnection, Context.BIND_AUTO_CREATE);

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("saved-area")) {
				area = (Area) savedInstanceState.getSerializable("saved-area");
			}
			if (savedInstanceState.containsKey("saved-subject-name")) {
				subjectName = savedInstanceState
						.getString("saved-subject-name");
			}
		}

		if (getArguments().containsKey("argument-area")) {
			area = (Area) getArguments().getSerializable("argument-area");
		}
		if (getArguments().containsKey("argument-subject-name")) {
			subjectName = getArguments().getString("argument-subject-name");
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		refreshContent();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putSerializable("saved-area", area);
		outState.putString("saved-subject-name", subjectName);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return null;
	}

}
