package lamparski.areabase.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import lamparski.areabase.AreaActivity;
import lamparski.areabase.R;
import lamparski.areabase.cards.BasicCard;
import lamparski.areabase.cards.ClickthroughCardUI;
import lamparski.areabase.map_support.OrdnanceSurveyMapView;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.fima.cardsui.views.CardUI;

public class SummaryFragment extends SherlockFragment implements
		IAreabaseFragment {

	private OrdnanceSurveyMapView mOpenSpaceView;
	private CardUI mCardUI;
	/**
	 * This saves cards that are supposed to be preserved across orientation
	 * changes, etc.
	 */

	private ArrayList<HashMap<String, Object>> cardCache;
	private Location mLocation;
	private boolean is_tablet, is_landscape;

	public SummaryFragment() {
		super();
		cardCache = new ArrayList<HashMap<String, Object>>();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			if (savedInstanceState.getSerializable("SummaryFragment-cardCache") != null) {
				// Oh how I wish for some more dynamic typing
				cardCache = (ArrayList<HashMap<String, Object>>) savedInstanceState
						.getSerializable("SummaryFragment-cardCache");
			}
		}

		if (!(cardCache.isEmpty())) {
			for (HashMap<String, Object> cardModel : cardCache) {
				// This is why I love Python
				if (cardModel.get("TYPE").equals(BasicCard.class)) {
					mCardUI.addCard(new BasicCard((String) cardModel
							.get("TITLE"), (String) cardModel
							.get("DESCRIPTION")));
				}
			}
		}

		refreshContent();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
			// In this mode, mCardUI is clickthrough.
			((ClickthroughCardUI) mCardUI).setViewBelow(mOpenSpaceView);
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
		mCardUI.setEnabled(false);

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
	 * @see
	 * android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// HACK: instead of doing it this way, modify CardsUI to support
		// MVC.
		outState.putSerializable("SummaryFragment-cardCache", cardCache);

		outState.putParcelable(AreaActivity.CURRENT_COORDS, mLocation);
	}

	@Override
	public void refreshContent() {
		mCardUI.clearCards();
		cardCache.clear();
		mCardUI.addCard(new BasicCard("Not fully implemented",
				"This is just a mock."));
		HashMap<String, Object> goddamnit = new HashMap<String, Object>();
		goddamnit.put("TITLE", "Not fully implemented");
		goddamnit.put("DESCRIPTION", "This is just a mock.");
		goddamnit.put("TYPE", BasicCard.class);
		cardCache.add(goddamnit);

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
	}

	@Override
	public void searchByText(String query) {
		Log.d("SummaryFragment", "search called with query: " + query);
	}

}
