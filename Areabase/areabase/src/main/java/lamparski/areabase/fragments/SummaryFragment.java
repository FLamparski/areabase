package lamparski.areabase.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.AsyncTask;
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
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import lamparski.areabase.AreaActivity;
import lamparski.areabase.R;
import lamparski.areabase.cards.AreaRankCard;
import lamparski.areabase.cards.ErrorCard;
import lamparski.areabase.cards.EventfulArrayList;
import lamparski.areabase.cards.EventfulArrayList.OnItemAddedListener;
import lamparski.areabase.map_support.OrdnanceSurveyMapView;
import lamparski.areabase.services.AreaDataService;
import lamparski.areabase.services.AreaDataService.AreaDataBinder;
import lamparski.areabase.services.AreaDataService.AreaLookupCallbacks;
import lamparski.areabase.services.AreaDataService.BasicAreaInfoIface;
import lamparski.areabase.widgets.CommonDialogHandlers;
import nde2.errors.NDE2Exception;
import nde2.pull.methodcalls.discovery.GetAreaDetail;
import nde2.pull.types.Area;
import nde2.pull.types.DetailedArea;

import static lamparski.areabase.widgets.CommonDialogs.serviceDisconnectAlert;

/**
 * The lovely Summary Fragment.
 *
 * Displays the map of the area, and cards with information relating to the area. It uses
 * state-of-the-art adaptive Mad Libs generation technology to deliver human-readable descriptions
 * for even the most confusing statistical data... or, you know, might just crash horribly.
 *
 * In this case, one should report bugs.
 *
 * no but really this is what the user sees first so it should be good.
 *
 * @author filip
 * @see lamparski.areabase.fragments.IAreabaseFragment
 * @see lamparski.areabase.AreaActivity
 */
public class SummaryFragment extends Fragment implements IAreabaseFragment, BasicAreaInfoIface {

	private OrdnanceSurveyMapView mOpenSpaceView;
	private CardUI mCardUI;
	/**
	 * This saves cards that are supposed to be preserved across orientation
	 * changes, etc.
	 */
	private EventfulArrayList<CardModel> cardModels;
	private Location mLocation;
    private Area area;
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
				serviceDisconnectAlert(name, getActivity());
			}
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("SummaryFragment", "AreaDataService connected");
			AreaDataBinder binder = (AreaDataBinder) service;
			mService = binder.getService();
			isServiceBound = true;
            is_live = true;
		}
	};

    @Override
    public void onError(final Throwable tr) {
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Log.e("SummaryFragment", "Error processing NDE data", tr);
                    try {
                        NDE2Exception nde2Exception = (NDE2Exception) tr;
                        Log.w("SummaryFragment",
                                String.format(
                                        "NDE2 error response %d: Title: %s; detail: %s",
                                        nde2Exception.getNessCode(),
                                        nde2Exception.getNessMessage(),
                                        nde2Exception.getNessDetail()));
                    } catch (Exception e) {
                        Log.d("SummaryFragment", "Not a NDE2Exception, got: "
                                + tr.getClass().getSimpleName());
                    }
                    if (tr instanceof IOException) {
                        Crouton.makeText(getActivity(),
                                R.string.io_exception_generic_message,
                                Style.ALERT).show();
                        CardModel errmdl = new CardModel(ErrorCard.class);
                        errmdl.setTitlePlay(getString(R.string.io_exception_generic_message));
                        errmdl.setDescription(getString(R.string.summaryactivity_cardmaker_ioerror_body));
                        errmdl.setImageRes(R.drawable.ic_network_error);
                        cardModels.add(errmdl); // Errors are now
                        // cards.
                    } else {
                        Crouton.makeText(getActivity(),
                                R.string.summaryactivity_cardmaker_onserror,
                                Style.ALERT).show();
                        if (tr instanceof NDE2Exception) {
                            String msg = String.format("NDE error: %s -- %s",
                                    ((NDE2Exception) tr).getNessMessage(),
                                    ((NDE2Exception) tr).getNessDetail());
                            Crouton.makeText(getActivity(), msg, Style.INFO).show();
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
    }

    @Override
    public void cardReady(CardModel cm) {
        cardModels.add(cm);
    }

    @Override
    public void allDone(Float areaRank) {
        if(getActivity() != null) {
            getActivity().setProgressBarIndeterminateVisibility(false);
            if(areaRank != null){
                CardModel scoreCard = new CardModel(AreaRankCard.class);
                scoreCard.setTitlePlay(getString(R.string.card_arearank_text, area.getName()));
                scoreCard.setDescription(String.format("%.1f", areaRank));
                if(areaRank >= 75.0f){
                    scoreCard.setColor(String.format("#%08X",
                            getResources().getColor(R.color.arearank80)));
                } else if (areaRank >= 50.0f){
                    scoreCard.setColor(String.format("#%08X",
                            getResources().getColor(R.color.arearank60)));
                } else if (areaRank >= 25.0f){
                    scoreCard.setColor(String.format("#%08X",
                            getResources().getColor(R.color.arearank40)));
                } else {
                    scoreCard.setColor(String.format("#%08X",
                            getResources().getColor(R.color.arearank20)));
                }
                cardModels.add(scoreCard);
            }
        }
    }

    @Override
    public void onAreaNameFound(final String name) {
        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((AreaActivity) getActivity()).setTitle(name);
                }
            });
        }
    }

    @Override
    public void onAreaBoundaryFound(final double[][] poly) {
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    polygon = poly;
                    mOpenSpaceView.highlightBoundary(poly);

                }
            });
        }
    }

	public SummaryFragment() {
		super();
		Log.i("SummaryFragment", "Fragment created");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("SummaryFragment", "onActivityCreated() enter");

		if (savedInstanceState != null) {
			Log.d("SummaryFragment",
					"savedInstanceState != null; reading state...");
			mLocation = (Location) savedInstanceState
					.getParcelable(AreaActivity.CURRENT_COORDS);

			Log.d("SummaryFragment", "    read current-coords -> mLocation; "
					+ mLocation.toString());

			Object depickledCards = savedInstanceState
					.getSerializable("card-models");

			if (depickledCards != null) {
				Log.d("SummaryFragment",
						"    read card-models -> depickledCards; "
								+ depickledCards.toString());

				cardModels = (EventfulArrayList<CardModel>) depickledCards;
                cardModels.setOnItemAddedListener(sOnCardModelAdded); // context leak fix?
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

        cardModels = new EventfulArrayList<CardModel>();
        cardModels.setOnItemAddedListener(sOnCardModelAdded);

		View theView = inflater.inflate(R.layout.fragment_summary, container,
				false);

        assert theView != null;

        if(getActivity() != null){
            is_tablet = ((AreaActivity) getActivity()).isTablet();
		    is_landscape = ((AreaActivity) getActivity()).isLandscape();
        }

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
            if(getArguments().containsKey(AreaActivity.CURRENT_COORDS)){
			    mLocation = (Location) getArguments().getParcelable(AreaActivity.CURRENT_COORDS);
            }
            if(getArguments().containsKey("argument-area")){
                area = (Area) getArguments().getSerializable("argument-area");
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
        is_live = true;
        Intent intent = new Intent(getActivity(), AreaDataService.class);
        try{
            getActivity().getApplicationContext().bindService(intent,
                    mServiceConnection, Context.BIND_AUTO_CREATE);
        } catch (NullPointerException npe) {
            Log.e("SummaryFragment", "Cannot bind AreaDataService: NullPointerException" +
                    "on either getActivity() or getApplicationContext()", npe);
        }
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
        if(is_live && isServiceBound){
            try{
                new Handler().postDelayed(refreshContentAction, 100);
            } catch (NullPointerException npe){
                if(++refreshContentTries <= 10){
                    refreshContent();
                }
            }
        } else {
            if(++refreshContentTries <= 20){
                refreshContent();
            }
        }
	}

    private int refreshContentTries = 0;
    private Runnable refreshContentAction = new Runnable() {
        @Override
        public void run() {
            assert getActivity() != null;
            if(area == null){
                area = ((AreaActivity) getActivity()).getArea();
            }
            cardModels.clear();

            mCardUI.clearCards();
            mCardUI.invalidate();
            mCardUI.refresh();
            mCardUI.forceLayout();

            getActivity().setProgressBarIndeterminateVisibility(true);

            if (isServiceBound) {
                mService.generateCardsForArea(area, SummaryFragment.this);
            } else {
                getActivity().setProgressBarIndeterminateVisibility(false);
                Log.wtf("SummaryFragment",
                        "refreshContent(): AreaService is unbound :(");
            }

            setMapCentre(area);
            refreshContentTries = 0;
        }
    };

    private void setMapCentre(final Area area) {
        new AsyncTask<Void, Void, List<Double>>(){
            @Override
            protected List<Double> doInBackground(Void... params) {
                List<Double> coords = new ArrayList<Double>();
                try {
                    DetailedArea detailedArea = new GetAreaDetail(area).execute();
                    double minX, minY, maxX, maxY, midX, midY;
                    String[] rawcoords = detailedArea.getEnvelope().split(":");
                    minX = Double.parseDouble(rawcoords[0]);
                    minY = Double.parseDouble(rawcoords[1]);
                    maxX = Double.parseDouble(rawcoords[2]);
                    maxY = Double.parseDouble(rawcoords[3]);
                    midX = (minX + maxX) / 2;
                    midY = (minY + maxY) / 2;
                    coords.add(midX);
                    coords.add(midY);
                } catch (Exception ex) {
                    onError(ex);
                }
                return coords;
            }

            @Override
            protected void onPostExecute(List<Double> coords) {
                if(!coords.isEmpty()){
                    mOpenSpaceView.setCentre_byEastingNorthing(coords.get(0), coords.get(1));
                    mOpenSpaceView.setZoom(9);
                }
            }
        }.execute();
    }

    @Override
	public void searchByText(String query) {
		Log.d("SummaryFragment", "search called with query: " + query);

		if (isServiceBound) {
			mService.areaForName(query, new AreaLookupCallbacks() {
                @Override
                public void areaReady(Area area) {
                    SummaryFragment.this.area = area;
                    refreshContent();
                }

                @Override
                public void onError(Throwable tr) {
                    if(getActivity() != null) {
                        ((AreaActivity) getActivity()).onError(tr);
                    }
                }
            });
		} else {
			Log.wtf("SummaryFragment",
					"refreshContent(): AreaService is unbound :(");
		}

		mOpenSpaceView.setZoom(10);
	}

	private OnClickListener sExplainMissingData = new OnClickListener() {
		@Override
		public void onClick(View v) {
            if(getActivity() == null){
                Log.w("SummaryFragment", "Missing data (ValueNotAvailable) error, can't display " +
                        "as there is no Activity or Context to bind to");
                return;
            }
			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.card_error_values_not_available_title)
					.setMessage(
							R.string.summaryactivity_cardmaker_values_not_available)
					.setNeutralButton(android.R.string.ok,
							CommonDialogHandlers.JUST_DISMISS).show();
		}
	};

    private OnItemAddedListener sOnCardModelAdded = new OnItemAddedListener() {

        @Override
        public boolean onItemAdded(Object item) {
            try {
                Log.v("SummaryFragment", "A card model has been added, creating a card...");
                mCardUI.addCardToLastStack((Card) CardFactory
                        .createCard((CardModel) item));
                mCardUI.refresh();
            } catch (Exception e) {
                Log.w("SummaryFragment", "Cannot create card.", e);
                return false;
            }
            return true;
        }
    };

}
