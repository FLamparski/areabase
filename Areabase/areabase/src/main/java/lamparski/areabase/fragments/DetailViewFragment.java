package lamparski.areabase.fragments;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import lamparski.areabase.AreaActivity;
import lamparski.areabase.R;
import lamparski.areabase.services.AreaDataService;
import lamparski.areabase.services.AreaDataService.AreaDataBinder;
import lamparski.areabase.services.AreaDataService.AreaLookupCallbacks;
import nde2.pull.types.Area;

import static lamparski.areabase.widgets.CommonDialogs.serviceCockupNotify;

/**
 * A base class for all fragments that deal with {@link nde2.pull.types.Area} objects and/or
 * @{link Subject}s but are not {@link lamparski.areabase.fragments.SummaryFragment}s. This offers
 * a way of connecting to the {@link lamparski.areabase.services.AreaDataService} and retains the
 * area information, however most implementations usually just go for getting the area from
 * AreaActivity.
 *
 * @author filip
 * @see lamparski.areabase.services.AreaDataService
 * @see lamparski.areabase.AreaActivity
 * @see lamparski.areabase.fragments.SummaryFragment
 */
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
            if(area != null && subjectName != null) {
                refreshContent();
            }
		}
	};

	protected AreaLookupCallbacks mAreaLookupCallbacks = new AreaLookupCallbacks() {

		@Override
		public void onError(Throwable err) {
			Log.e("DetailViewFragment", "AreaLookupCallbacks onError()", err);
		}

		@Override
		public void areaReady(Area area) {
            assert area != null;
			DetailViewFragment.this.area = area;
            if(getActivity() != null) {
                {
                    ((AreaActivity) getActivity()).setTitle(area.getName());
                }
            }
			refreshContent();
		}
	};

	@Override
	public abstract void refreshContent();

	@Override
	public void searchByText(String query) {
		if(isServiceBound) {
            {
                mService.areaForName(query, mAreaLookupCallbacks);
            }
        }
	}

    /**
     * Helper method to simplify toasts cross-platform
     * @param text the text to be displayed
     */
    protected void showCroutonCrossThread(final CharSequence text){
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Crouton.makeText(getActivity(), text, Style.INFO).show();
                }
            });
        }
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		is_live = true;
		Intent intent = new Intent(getActivity(), AreaDataService.class);
		try{
            getActivity().getApplicationContext().bindService(intent,
                    mServiceConnection, Context.BIND_AUTO_CREATE);
        } catch (NullPointerException npe) {
            Log.e("DetailViewFragment", "Cannot bind AreaDataService: NullPointerException" +
                    "on either getActivity() or getApplicationContext()", npe);
        }

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("saved-area")) {
				area = (Area) savedInstanceState.getSerializable("saved-area");
			}
			if (savedInstanceState.containsKey("saved-subject-name")) {
				subjectName = savedInstanceState
						.getString("saved-subject-name");
			}
		}

		if(getArguments() != null){
            if (getArguments().containsKey("argument-area")) {
                area = (Area) getArguments().getSerializable("argument-area");
            }
            if (getArguments().containsKey("argument-subject-name")) {
                subjectName = getArguments().getString("argument-subject-name");
            }
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
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), R.string.io_exception_generic_message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
