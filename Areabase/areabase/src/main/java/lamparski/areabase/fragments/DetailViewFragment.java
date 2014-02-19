package lamparski.areabase.fragments;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import lamparski.areabase.AreaActivity;
import lamparski.areabase.R;
import lamparski.areabase.services.AreaDataService;
import lamparski.areabase.services.AreaDataService.AreaDataBinder;
import lamparski.areabase.services.AreaDataService.DetailViewAreaInfoIface;
import nde2.pull.types.Area;

import static lamparski.areabase.widgets.CommonDialogs.serviceCockupNotify;

public abstract class DetailViewFragment extends Fragment implements
		IAreabaseFragment {

	protected Area area = null;
	protected String subjectName = null;

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
            if(area != null && subjectName != null)
                refreshContent();
		}
	};

	protected DetailViewAreaInfoIface mDetailViewAreaInfoIface = new DetailViewAreaInfoIface() {

		@Override
		public void onError(Throwable err) {
			err.printStackTrace();
		}

		@Override
		public void areaReady(Area area) {
            assert area != null;
			DetailViewFragment.this.area = area;
            if(getActivity() != null) ((AreaActivity) getActivity()).setTitle(area.getName());
			refreshContent();
		}
	};

	@Override
	public abstract void refreshContent();

    int retries = 0;
	@Override
	public void updateGeo(Location location) {
		if(isServiceBound) mService.areaForLocation(location, mDetailViewAreaInfoIface);
        else {
            final Location LOCATION = location;
            if(retries++ < 10) new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateGeo(LOCATION);
                }
            }, 500);
        }
	}

	@Override
	public void searchByText(String query) {
		if(isServiceBound) mService.areaForName(query, mDetailViewAreaInfoIface);
	}

    /**
     * Helper method to simplify toasts cross-platform
     * @param text
     * @param duration
     */
    protected void showToastCrossThread(final CharSequence text, final int duration){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), text, duration).show();
            }
        });
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
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
		outState.putSerializable("saved-area", area);
		outState.putString("saved-subject-name", subjectName);
	}

	@Override
	public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState);

    protected void onIOError(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), R.string.io_exception_generic_message, 0).show();
            }
        });
    }
}
