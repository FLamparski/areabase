package lamparski.areabase.fragments;

import java.util.ArrayList;

import lamparski.areabase.AreaActivity;
import lamparski.areabase.R;
import lamparski.areabase.cards.BasicCard;
import lamparski.areabase.map_support.HoloCSSColourValues;
import lamparski.areabase.map_support.OrdnanceSurveyMapView;
import android.location.Location;
import android.os.Bundle;
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
				if (depickledCards instanceof ArrayList<?>) {
					cardModels = (ArrayList<CardModel>) depickledCards;
				}
			} else {
				populateCards();
			}
		} else {
			populateCards();
		}
	}

	private void populateCards() {
		CardModel mdl1 = new CardModel(
				"Bank is located in the City of London, and serves a nice puropse as a mock.",
				"This is Bank", BasicCard.class);
		CardModel mdl2 = new CardModel(
				"Reflection FTW!",
				"This card was created using a MVC-based architecture and a lot of reflection.",
				HoloCSSColourValues.GREEN.getCssValue(),
				HoloCSSColourValues.GREEN.getCssValue(), false, false,
				BasicCard.class);
		cardModels.add(mdl1);
		cardModels.add(mdl2);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("SummaryFragment", "onCreateView() enter");
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
	 * @see
	 * android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelable(AreaActivity.CURRENT_COORDS, mLocation);
		outState.putSerializable("card-models", cardModels);
	}

	@Override
	public void refreshContent() {
		mCardUI.clearCards();

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
