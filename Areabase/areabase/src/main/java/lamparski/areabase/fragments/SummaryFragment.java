package lamparski.areabase.fragments;

import android.app.AlertDialog;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.CardFactory;
import com.fima.cardsui.objects.CardModel;
import com.fima.cardsui.views.CardUI;

import java.io.IOException;

import lamparski.areabase.AreaActivity;
import lamparski.areabase.R;
import lamparski.areabase.cards.ErrorCard;
import lamparski.areabase.cards.EventfulArrayList;
import lamparski.areabase.cards.EventfulArrayList.OnItemAddedListener;
import lamparski.areabase.cards.PlayCard;
import lamparski.areabase.map_support.HoloCSSColourValues;
import lamparski.areabase.map_support.OrdnanceSurveyMapView;
import lamparski.areabase.services.AreaDataService;
import lamparski.areabase.services.AreaDataService.AreaDataBinder;
import lamparski.areabase.services.AreaDataService.BasicAreaInfoIface;
import lamparski.areabase.widgets.CommonDialogHandlers;
import nde2.errors.NDE2Exception;

import static lamparski.areabase.widgets.CommonDialogs.serviceCockupNotify;

public class SummaryFragment extends Fragment implements IAreabaseFragment {

	private OrdnanceSurveyMapView mOpenSpaceView;
	private CardUI mCardUI;
	/**
	 * This saves cards that are supposed to be preserved across orientation
	 * changes, etc.
	 */
	private EventfulArrayList<CardModel> cardModels;
	private Location mLocation;
	private boolean is_tablet, is_landscape, is_live;
	private double[][] polygon = null;

	private AreaDataService mService;
	private boolean isServiceBound;
	private ServiceConnection mServiceConnection = new ServiceConnection() {

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

	private BasicAreaInfoIface mAreaInfoCallbacks = new BasicAreaInfoIface() {

		@Override
		public void onError(final Throwable err) {
			SummaryFragment.this.getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Log.e("SummaryFragment", "Error processing NDE data", err);
					try {
						NDE2Exception cockup = (NDE2Exception) err;
						Log.w("SummaryFragment",
								String.format(
										"NDE2 error response %d: Title: %s; detail: %s",
										cockup.getNessCode(),
										cockup.getNessMessage(),
										cockup.getNessDetail()));
					} catch (Exception e) {
						Log.d("SummaryFragment", "Not a NDE2Exception, got: "
								+ err.getClass().getSimpleName());
					}
					if (err instanceof IOException) {
						Toast.makeText(getActivity(),
								R.string.summaryactivity_cardmaker_ioerror,
								Toast.LENGTH_SHORT).show();
						CardModel errmdl = new CardModel(ErrorCard.class);
						errmdl.setTitlePlay(getString(R.string.summaryactivity_cardmaker_ioerror));
						errmdl.setDescription(getString(R.string.summaryactivity_cardmaker_ioerror_body));
						errmdl.setImageRes(R.drawable.ic_network_error);
						cardModels.add(errmdl); // Errors are now
												// cards.
					} else {
						Toast.makeText(getActivity(),
								R.string.summaryactivity_cardmaker_onserror,
								Toast.LENGTH_SHORT).show();
						if (err instanceof NDE2Exception) {
							String msg = String.format("NDE error: %s -- %s",
									((NDE2Exception) err).getNessMessage(),
									((NDE2Exception) err).getNessDetail());
							Toast.makeText(getActivity(), msg, 0).show();
							CardModel errmdl = new CardModel(ErrorCard.class);
							errmdl.setTitlePlay(getString(R.string.error_cannot_resolve_postcode));
							errmdl.setDescription(getString(R.string.error_cannot_resolve_postcode_body));
							errmdl.setImageRes(R.drawable.ic_map_error);
							cardModels.add(errmdl);
						}
					}
				}
			});
		}

		@Override
		public void cardReady(final CardModel cm) {
			SummaryFragment.this.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					cardModels.add(cm);
				}
			});
		}

		@Override
		public void allDone() {
			getActivity().setProgressBarIndeterminateVisibility(false);
		}

		@Override
		public void onValueNotAvailable() {
			SummaryFragment.this.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					for (CardModel ecm : cardModels) {
						if (ecm.getTitlePlay()
								.equals(getResources()
										.getString(
												R.string.card_error_values_not_available_title))) {
							return;
						}
					}
					CardModel cm = new CardModel(
							getResources()
									.getString(
											R.string.card_error_values_not_available_title),
							getResources()
									.getString(
											R.string.card_error_values_not_available_body),
							HoloCSSColourValues.ORANGE.getCssValue(),
							HoloCSSColourValues.ORANGE.getCssValue(), false,
							true, PlayCard.class);
					cardModels.addSilent(cm);
					Card crd;
					try {
						crd = (Card) CardFactory.createCard(cm);
					} catch (java.lang.InstantiationException e) {
						e.printStackTrace();
						return;
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						return;
					}
					crd.setOnClickListener(sExplainMissingData);
					mCardUI.addCard(crd);
				}
			});
		}

		@Override
		public void onAreaNameFound(final String name) {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					((AreaActivity) getActivity()).setTitle(name);
				}
			});
		}

		@Override
		public void onAreaBoundaryFound(final double[][] poly) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					polygon = poly;
					mOpenSpaceView.highlightBoundary(poly);

				}
			});
		}
	};

	public SummaryFragment() {
		super();
		Log.i("SummaryFragment", "Fragment created");
		is_live = true;
		cardModels = new EventfulArrayList<CardModel>();
		cardModels.setOnItemAddedListener(new OnItemAddedListener() {

			@Override
			public boolean onItemAdded(Object item) {
				try {
					Toast.makeText(
							getActivity(),
							"Cards: adding object "
									+ item.getClass().getSimpleName(),
							Toast.LENGTH_SHORT).show();
					mCardUI.addCardToLastStack((Card) CardFactory
							.createCard((CardModel) item));
					mCardUI.refresh();
				} catch (Exception e) {
					Log.w("SummaryFragment", "Cannot create card.", e);
					return false;
				}
				return true;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("SummaryFragment", "onActivityCreated() enter");

		Intent intent = new Intent(getActivity(), AreaDataService.class);
		getActivity().getApplicationContext().bindService(intent,
				mServiceConnection, Context.BIND_AUTO_CREATE);

		getActivity().setTitle("Test Area");

		if (savedInstanceState != null) {
			Log.d("SummaryFragment",
					"savedInstanceState != null; reading state...");
			mLocation = (Location) savedInstanceState
					.getParcelable(AreaActivity.CURRENT_COORDS);

			Log.d("SummaryFragment", "    read current-coords -> mLocation; "
					+ mLocation.toString());

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					mOpenSpaceView.setCentre(mLocation);
					mOpenSpaceView.setZoom(10);
				}
			}, 750);

			Object depickledCards = savedInstanceState
					.getSerializable("card-models");

			if (depickledCards != null) {
				Log.d("SummaryFragment",
						"    read card-models -> depickledCards; "
								+ depickledCards.toString());

				cardModels = (EventfulArrayList<CardModel>) depickledCards;
				Toast.makeText(getActivity(),
						"Found saved instance state cards", Toast.LENGTH_SHORT)
						.show();

				Log.d("SummaryFragment",
						"    cast depickledCards -> cardModels; "
								+ cardModels.toString());

				populateCards();
			} else {
				Log.w("SummaryFragment",
						"depickledCards turns out to be null, what.");
				// refreshContent();
			}
		}
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

		mCardUI.setSwipeable(false);

		if (getArguments() != null) {
			mLocation = (Location) getArguments().getParcelable(
					AreaActivity.CURRENT_COORDS);
			if (savedInstanceState != null) {
				mLocation = (Location) savedInstanceState
						.getParcelable(AreaActivity.CURRENT_COORDS);
			} else {
				mLocation = new Location("mock");
				mLocation.setLongitude(-0.041229);
				mLocation.setLatitude(51.448800);
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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		Log.d("SummaryFragment", "Stopping AreaDataService.");
		is_live = false;
		if (isServiceBound)
			getActivity().getApplicationContext().unbindService(
					mServiceConnection);
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
		Log.d("SummaryFragment", "Saving instance state");

		outState.putParcelable(AreaActivity.CURRENT_COORDS, mLocation);
		outState.putSerializable("card-models", cardModels);
	}

	private void populateCards() {
		for (CardModel mdl : cardModels) {
			try {
				Card crd = (Card) CardFactory.createCard(mdl);
				if (mdl.getTitlePlay()
						.equals(getResources().getString(
								R.string.card_error_values_not_available_title))) {
					crd.setOnClickListener(sExplainMissingData);
				}
				mCardUI.addCardToLastStack(crd);
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
		Toast.makeText(getActivity(), "Refreshing view: clearing cards.",
				Toast.LENGTH_SHORT).show();
		cardModels.clear();

		mCardUI.clearCards();
		mCardUI.invalidate();
		mCardUI.refresh();
		mCardUI.forceLayout();

		getActivity().setProgressBarIndeterminateVisibility(true);

		if (isServiceBound) {
			mService.generateCardsForLocation(mLocation, mAreaInfoCallbacks);
		} else {
			getActivity().setProgressBarIndeterminateVisibility(false);
			Log.wtf("SummaryFragment",
					"refreshContent(): AreaService is unbound :(");
		}

		mOpenSpaceView.setCentre(mLocation);
		mOpenSpaceView.setZoom(10);
	}

	@Override
	public void updateGeo(Location location) {
		Log.d("SummaryFragment", "update location: " + location.toString());
		mLocation = location;

        if(mOpenSpaceView != null && mCardUI != null){
            mOpenSpaceView.setCentre(location);
            mOpenSpaceView.setZoom(10);
            refreshContent();
        }
	}

	@Override
	public void searchByText(String query) {
		Log.d("SummaryFragment", "search called with query: " + query);

		Toast.makeText(getActivity(), "Refreshing view: clearing cards.",
				Toast.LENGTH_SHORT).show();
		cardModels.clear();

		mCardUI.clearCards();
		mCardUI.invalidate();
		mCardUI.refresh();
		mCardUI.forceLayout();

		getActivity().setProgressBarIndeterminateVisibility(true);

		if (isServiceBound) {
			mService.generateCardsForAreaName(query, mAreaInfoCallbacks);
		} else {
			getActivity().setProgressBarIndeterminateVisibility(false);
			Log.wtf("SummaryFragment",
					"refreshContent(): AreaService is unbound :(");
		}

		mOpenSpaceView.setZoom(10);
	}

	private OnClickListener sExplainMissingData = new OnClickListener() {
		@Override
		public void onClick(View v) {
			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.card_error_values_not_available_title)
					.setMessage(
							R.string.summaryactivity_cardmaker_values_not_available)
					.setNeutralButton(android.R.string.ok,
							CommonDialogHandlers.JUST_DISMISS).show();
		}
	};

}
