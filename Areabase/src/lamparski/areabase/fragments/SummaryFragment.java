package lamparski.areabase.fragments;

import java.util.ArrayList;
import java.util.List;

import lamparski.areabase.AreaActivity;
import lamparski.areabase.AreaDataService;
import lamparski.areabase.AreaDataService.AreaDataBinder;
import lamparski.areabase.AreaDataService.AreaFetchedCallbacks;
import lamparski.areabase.R;
import lamparski.areabase.cards.BasicCard;
import lamparski.areabase.cards.PlayCard;
import lamparski.areabase.map_support.HoloCSSColourValues;
import lamparski.areabase.map_support.OrdnanceSurveyMapView;
import nde2.types.discovery.Area;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.CardFactory;
import com.fima.cardsui.objects.CardModel;
import com.fima.cardsui.views.CardUI;

public class SummaryFragment extends SherlockFragment implements
		IAreabaseFragment {

	private OrdnanceSurveyMapView mOpenSpaceView;
	private CardUI mCardUI;
	/**
	 * This saves cards that are supposed to be preserved across orientation
	 * changes, etc.
	 */
	private ArrayList<CardModel> cardModels;
	private Location mLocation;
	private boolean is_tablet, is_landscape;

	private AreaDataService mService;
	private boolean isServiceBound;
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e("SummaryFragment",
					"The AreaDataService disconnected unexpectedly.");
			isServiceBound = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			AreaDataBinder binder = (AreaDataBinder) service;
			mService = binder.getService();
			isServiceBound = true;
		}
	};

	public SummaryFragment() {
		super();
		cardModels = new ArrayList<CardModel>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("SummaryFragment", "onActivityCreated() enter");

		getActivity().setTitle("Test Area");

		if (savedInstanceState != null) {
			mOpenSpaceView.setCentre((Location) savedInstanceState
					.getParcelable(AreaActivity.CURRENT_COORDS));
			mOpenSpaceView.setZoom(10);
			Object depickledCards = savedInstanceState
					.getSerializable("card-models");
			if (depickledCards != null) {
				cardModels = (ArrayList<CardModel>) depickledCards;
			} else {
				refreshContent();
			}
		} else {
			refreshContent();
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("SummaryFragment", "onCreateView() enter");

		// Delete for release
		Debug.startMethodTracing("summaryfragment.trace");

		View theView = inflater.inflate(R.layout.fragment_summary, container,
				false);

		is_tablet = ((AreaActivity) getActivity()).isTablet();
		is_landscape = ((AreaActivity) getActivity()).isLandscape();

		if (is_tablet) {
			if (is_landscape) {
				mOpenSpaceView = (OrdnanceSurveyMapView) theView
						.findViewById(R.id.fragment_summary_OSMapView_TABLET_LANDSCAPE);
				mCardUI = (CardUI) theView
						.findViewById(R.id.fragment_summary_CardsUI_TABLET_LANDSCAPE);
			} else {
				mOpenSpaceView = (OrdnanceSurveyMapView) theView
						.findViewById(R.id.fragment_summary_OSMapView_TABLET);
				mCardUI = (CardUI) theView
						.findViewById(R.id.fragment_summary_CardsUI_TABLET);
			}
		} else {
			if (is_landscape) {
				mOpenSpaceView = (OrdnanceSurveyMapView) theView
						.findViewById(R.id.fragment_summary_OSMapView_DEFAULT_LANDSCAPE);
				mCardUI = (CardUI) theView
						.findViewById(R.id.fragment_summary_CardsUI_DEFAULT_LAND);
			} else {
				mOpenSpaceView = (OrdnanceSurveyMapView) theView
						.findViewById(R.id.fragment_summary_OSMapView_DEFAULT);
				mCardUI = (CardUI) theView
						.findViewById(R.id.fragment_summary_CardsUI_DEFAULT);
			}
		}

		mCardUI.setSwipeable(true);

		if (getArguments() != null) {
			mLocation = (Location) getArguments().getParcelable(
					AreaActivity.CURRENT_COORDS);
			if (savedInstanceState != null) {
				mLocation = (Location) savedInstanceState
						.getParcelable(AreaActivity.CURRENT_COORDS);
			} else {
				mLocation = new Location("mock");
				mLocation.setLongitude(-0.0887);
				mLocation.setLatitude(51.5135);
			}
		} else {
			Log.w("SummaryFragment", "Called with no arguments!");
		}

		return theView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		Intent intent = new Intent(getActivity(), AreaDataService.class);
		getActivity().bindService(intent, mServiceConnection,
				Context.BIND_AUTO_CREATE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		Debug.stopMethodTracing();
		if (isServiceBound)
			getActivity().unbindService(mServiceConnection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelable(AreaActivity.CURRENT_COORDS, mLocation);
		outState.putSerializable("card-models", cardModels);
	}

	private void populateCards() {
		for (CardModel mdl : cardModels) {
			try {
				mCardUI.addCard((Card) CardFactory.createCard(mdl));
			} catch (java.lang.InstantiationException e) {
				Log.e("SummaryFragment", "Cannot instantiate a Card.", e);
			} catch (IllegalAccessException e) {
				Log.e("SummaryFragment", "Cannot instantiate a Card.", e);
			}
		}

		mCardUI.invalidate();
		mCardUI.refresh();
		mCardUI.forceLayout();
	}

	@Override
	public void refreshContent() {
		mCardUI.clearCards();

		getSherlockActivity()
				.setSupportProgressBarIndeterminateVisibility(true);

		if (isServiceBound) {
			mService.getAreas(mLocation, new AreaFetchedCallbacks() {

				@Override
				public void onSuccess(List<Area> resultList) {
					Area closest = resultList.get(0);
					CardModel cm = new CardModel(PlayCard.class);
					cm.setTitlePlay(String.format("This is %s (%d).",
							closest.getName(), closest.getAreaId()));
					cm.setTitleColor(HoloCSSColourValues.AQUAMARINE
							.getCssValue());
					cm.setColor(HoloCSSColourValues.AQUAMARINE.getCssValue());
					cm.setDescription(String
							.format("This area falls within %s. It exists in hierarchy %d at level %d.",
									resultList.get(1).getName(),
									closest.getHierarchyId(),
									closest.getLevelTypeId()));
					cardModels.clear();
					cardModels.add(cm);

					populateCards();
					getSherlockActivity()
							.setSupportProgressBarIndeterminateVisibility(false);
				}

				@Override
				public void onError(Throwable tr) {
					AlertDialog dlg = new AlertDialog.Builder(getActivity())
							.setTitle(R.string.error_cannot_fetch_area_data)
							.setMessage(
									getResources()
											.getString(
													R.string.error_cannot_fetch_area_data_body,
													tr.getMessage()))
							.setNeutralButton(android.R.string.ok,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									}).create();
					Log.e("SummaryActivity",
							"Error fetching areas, the user has been notified.",
							tr);
					dlg.show();
				}
			});
		}

		mOpenSpaceView.setCentre(mLocation);
		mOpenSpaceView.setZoom(10);
	}

	@Override
	public void updateGeo(Location location) {
		Log.d("SummaryFragment", "update location: " + location.toString());
		mLocation = location;
		mCardUI.addCardToLastStack(new BasicCard("Location updated",
				"New location: " + location.getLongitude() + "; "
						+ location.getLatitude()));
		mOpenSpaceView.setCentre(location);
		mOpenSpaceView.setZoom(10);
		refreshContent();
	}

	@Override
	public void searchByText(String query) {
		Log.d("SummaryFragment", "search called with query: " + query);
	}

}
