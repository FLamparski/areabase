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

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import lamparski.areabase.AreaActivity;
import lamparski.areabase.R;
import lamparski.areabase.services.AreaDataService;
import lamparski.areabase.services.AreaDataService.AreaDataBinder;
import lamparski.areabase.services.AreaDataService.AreaLookupCallbacks;
import nde2.pull.types.Area;

import static lamparski.areabase.widgets.CommonDialogs.serviceDisconnectAlert;

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
				serviceDisconnectAlert(name, getActivity());
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
		public void onError(Throwable tr) {
			Log.e("DetailViewFragment", "AreaLookupCallbacks onError()", tr);
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

    /**
     * Do something with the area that has been fetched/the user has requsted a refresh/...
     *
     * Often the workflow would include continuously post a runnable to the queue until all the
     * execution conditions are met.
     */
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
                    Crouton.makeText(getActivity(), R.string.io_exception_generic_message, Style.ALERT).show();
                }
            });
        }
    }
}
