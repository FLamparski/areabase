package lamparski.areabase;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import lamparski.areabase.dummy.mockup_classes.DemoObjectFragment;
import lamparski.areabase.fragments.ErrorDialogFragment;
import lamparski.areabase.fragments.IAreabaseFragment;
import lamparski.areabase.fragments.PoliceDataFragment;
import lamparski.areabase.fragments.SubjectListFragment;
import lamparski.areabase.fragments.SubjectViewFragment;
import lamparski.areabase.fragments.SummaryFragment;
import lamparski.areabase.services.AreaDataService;
import lamparski.areabase.services.AreaDataService.AreaDataBinder;
import lamparski.areabase.services.AreaDataService.AreaListCallbacks;
import lamparski.areabase.services.AreaDataService.AreaLookupCallbacks;
import lamparski.areabase.widgets.CommonDialogs;
import lamparski.areabase.widgets.RobotoLightTextView;
import nde2.pull.types.Area;
import nde2.pull.types.DataSetFamily;

import static lamparski.areabase.widgets.CommonDialogs.serviceDisconnectAlert;

/**
 * The main screen of the application. It contains the fragment host for all of the components which
 * use the area data provided by the ONS and so on. It contains the reference to the Area object,
 * and links to display different data about this Area. At present, it only loads the current MSOA
 * for where the user is standing and has limited support to load other areas based on the text
 * search, but this could be alleviated...
 * TODO: Create an issue on this; should be able to view other areas & navigate hierarchy.
 * ...but not right now.
 *
 * @author filip
 * @see nde2.pull.types.Area
 * @see lamparski.areabase.fragments.IAreabaseFragment
 */
public class AreaActivity extends Activity implements LocationListener,
		ConnectionCallbacks,
		OnConnectionFailedListener, OnBackStackChangedListener, AreaLookupCallbacks, AreaListCallbacks {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private LinearLayout mDrawer;
	private CharSequence mTitle;
	private Location mGeoPoint = null;
	private IAreabaseFragment mContentFragment = null;
	private int mFragmentHostId;

	SharedPreferences mPref;
	SharedPreferences.Editor mPrefEditor;

	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private Area mArea = null;
    private AreaDataService areaDataService;
    protected boolean isAreaDataServiceBound, is_live;
    private boolean restoring_from_savestate = false;
    protected ServiceConnection mAreaDataServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (is_live) {
                Log.e("AreaActivity",
                        "The AreaDataService disconnected unexpectedly.");
                isAreaDataServiceBound = false;
                serviceDisconnectAlert(name, AreaActivity.this);
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("AreaActivity", "AreaDataService connected");
            AreaDataBinder binder = (AreaDataBinder) service;
            areaDataService = binder.getService();
            isAreaDataServiceBound = true;
            if(areaDataService != null) {
                is_live = true;
            }
        }
    };

	protected boolean is_tablet = false;
	protected boolean is_landscape = false;
	protected boolean request_location_updates = false;
	protected boolean is_requesting_updates = false;

	public static final int SUMMARY = R.id.navdrawer_link_areabaseSummary;
	public static final int CRIME = R.id.navdrawer_link_areabaseCrime;
	public static final int ECONOMY = R.id.navdrawer_link_areabaseEconomy;
	public static final int ENVIRONMENT = R.id.navdrawer_link_areabaseEnvironment;
	public static final int EXPLORE_ONS = R.id.navdrawer_link_areabaseONSBrowser;
	public static final int EXPLORE_POLICE = R.id.navdrawer_link_areabasePoliceDataBrowser;
	public static final int AREA_HIERARCHY = 15;
	public static final int AREA_COMPARE = 16;
	public static final int GET_HELP = R.id.navdrawer_link_areabaseHelp;
	public static final String CURRENT_COORDS = "current-coords";

	private static final String SIS_LOADED_FRAGMENT = "currently-loaded-fragment";
	private static final String SIS_LOADED_COORDS = "currently-loaded-coordinates";
    private static final String SIS_LOADED_AREA = "currently-loaded-area";

	private static Context appCtx;

	private OnClickListener sDrawerLinkListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() != GET_HELP) {
				changeFragment(v.getId());
			}
			if (v.getId() == GET_HELP) {
				Log.i("AreaActivity navdrawer", "We are getting help!");
				Intent helpIntent = new Intent(Intent.ACTION_VIEW);
				helpIntent.setData(Uri
						.parse("http://flamparski.github.io/areabase/"));
				startActivity(helpIntent);
			}
			mDrawerLayout.closeDrawer(GravityCompat.START);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		appCtx = getApplicationContext();

		Log.d("AreaActivity", "onCreate() called");
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.area_activity);

		mPref = getSharedPreferences("lamparski.areabase.SHARED_PREFERENCES",
				Context.MODE_PRIVATE);
		mPrefEditor = mPref.edit();
		mLocationClient = new LocationClient(this, this, this);

		ActionBar mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		mActionBar.setTitle(R.string.app_name);
		mActionBar.setHomeButtonEnabled(true);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.tablet_area_activity_drawerLayout);
		if (mDrawerLayout != null) {
			mDrawer = (LinearLayout) findViewById(R.id.navdrawer_layout);
			is_tablet = true;
			mFragmentHostId = R.id.tablet_area_activity_frameLayout;
			Log.i("AreaActivity",
					"Loading tablet version of AreaActivity");
		} else {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.handset_area_activity_drawerLayout_DEFAULT);
			mDrawer = (LinearLayout) findViewById(R.id.navdrawer_layout);
			is_tablet = false;
			mFragmentHostId = R.id.handset_area_activity_frameLayout_DEFAULT;
			Log.i("AreaActivity",
					"Loading handset version of AreaActivity");
		}

		setUpNavDrawer();

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				getActionBar().setTitle(mTitle);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle("Areabase");
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mActionBar.setDisplayHomeAsUpEnabled(true);
		mDrawerLayout.closeDrawer(mDrawer);

        getFragmentManager().addOnBackStackChangedListener(this);

		if (savedInstanceState != null) {
			Log.d("AreaActivity", "  > savedInstanceState != null");
			Log.d("AreaActivity",
					"Restoring the reference to currently loaded fragment: "
							+ savedInstanceState.getString(SIS_LOADED_FRAGMENT));
			mContentFragment = (IAreabaseFragment) getFragmentManager()
					.findFragmentByTag(
							savedInstanceState.getString(SIS_LOADED_FRAGMENT));
			mGeoPoint = (Location) savedInstanceState
					.getParcelable(SIS_LOADED_COORDS);
            mArea = (Area) savedInstanceState.getSerializable(SIS_LOADED_AREA);
            restoring_from_savestate = true;
		}
	}

	private void setUpNavDrawer() {
		int drawerChildrenCount = mDrawer.getChildCount();
		for (int i = 0; i < drawerChildrenCount; i++) {
			View v_at_i = mDrawer.getChildAt(i);
			// Find me all children that are the links
			if (v_at_i instanceof RobotoLightTextView) {
				v_at_i.setOnClickListener(sDrawerLinkListener);
			}
		}
		// Special case for the "help" link, which is further down the
		// hierarchy:
		View helplink = mDrawer.findViewById(R.id.navdrawer_link_areabaseHelp);
		if (helplink != null) {
            helplink.setOnClickListener(sDrawerLinkListener);
        }
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mPref.contains("pref_location_backgroundlocate")) {
            request_location_updates = mPref.getBoolean("pref_location_backgroundlocate", false);
        }
        Intent intent = new Intent(this, AreaDataService.class);
        getApplicationContext().bindService(intent, mAreaDataServiceConnection, BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mLocationClient.disconnect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (is_requesting_updates) {
			is_requesting_updates = false;
			mLocationClient.removeLocationUpdates(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocationClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connResult) {
		Log.w("Google Play services",
				"Service connection failed. Attempting to resolve...");
		if (connResult.hasResolution()) {
			try {
				connResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (IntentSender.SendIntentException e) {
				Log.e("AreaActivity", "Error when resolving Play Services issue", e);
			}
		} else {
			showErrorDialog(connResult.getErrorCode());
		}
	}

	@Override
	public void onConnected(Bundle stuff) {
		Log.i("Google Play services", "Service connected.");
		mGeoPoint = mLocationClient.getLastLocation();

		if (request_location_updates) {
			is_requesting_updates = true;

			if (gServicesConnected()) {
				mLocationRequest = LocationRequest.create();
				mLocationRequest.setFastestInterval(5 * 1000l);
				mLocationRequest
						.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
				mLocationClient.requestLocationUpdates(mLocationRequest, this);
			}
		}

        if(is_live && mArea == null){
            beginAreaFetch(mGeoPoint);
        } else if (restoring_from_savestate) {
            doRefreshFragment();
        }
	}

	@Override
	public void onDisconnected() {
		Log.i("Google Play services", "Service disconnected.");
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.v("LocationListener",
				"New location received: " + location.toString());
		mGeoPoint = location;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			Log.w("AreaActivity",
					"Error resolution request. Intent: " + data.toString());
			switch (resultCode) {
			case Activity.RESULT_OK:
				// try again
				break;
			}
			break;
		default:
			break;
		}
	}

	private boolean gServicesConnected() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());
		if (resultCode == ConnectionResult.SUCCESS) {
			Log.d("AreaActivity", "We have Google Services!");
			return true;
		} else {
			return false;
		}
	}

	private void showErrorDialog(int errorCode) {
		Dialog dlg = GooglePlayServicesUtil.getErrorDialog(errorCode, this,
				CONNECTION_FAILURE_RESOLUTION_REQUEST);

		if (dlg != null) {
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();
			errorFragment.setDialog(dlg);
			errorFragment.show(getFragmentManager(), errorFragment.getClass()
					.getName());
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.areabase_opts_menu, menu);

		/*
		 * Search action: Handle expanding the search box and starting the
		 * search procedure.
		 */
		final EditText mSearchBox = (EditText) menu
				.findItem(R.id.action_search).getActionView();
		final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        // pretty sure this has no real effect but it gets rid of the ugly yellow highlight
        assert mSearchBox != null && searchMenuItem != null;
		searchMenuItem
				.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

					@Override
					public boolean onMenuItemActionCollapse(MenuItem item) {
						// Clear the search box
						mSearchBox.clearFocus();
						mSearchBox.setText("");
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.toggleSoftInput(
								InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						return true;
					}

					@Override
					public boolean onMenuItemActionExpand(MenuItem item) {
						// Place cursor in the search box
						mSearchBox.requestFocus();
						// Make sure the keyboard is displayed.
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
						return true;
					}
				});
		mSearchBox
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_NULL
								|| actionId == EditorInfo.IME_ACTION_SEARCH) {
							if (event != null) {
								if (event.getAction() != KeyEvent.ACTION_DOWN) {
									return false; // Prevents code below from
													// running twice.
								}
							}
							/*
							 * Only execute this if the user has pressed the
							 * 'Search' Enter-like key on the soft keyboard, or
							 * the hardware Enter key.
							 */
							searchAreasByText(v.getText().toString());
							searchMenuItem.collapseActionView();
							return true;
						} else {
							/*
							 * If the key was anything else, we don't want to do
							 * anything, so just return false.
							 */
							return false;
						}
					}
				});

		/*
		 * Other actions are much simpler, so they'll be handled in
		 * onOptionsItemSelected.
		 */

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawer)) {
				mDrawerLayout.closeDrawer(mDrawer);
			} else if (!(mDrawerLayout.isDrawerOpen(mDrawer))) {
				mDrawerLayout.openDrawer(mDrawer);
			}
			break;
		case R.id.action_locate:
			if (gServicesConnected()) {
				getLocation();
                changeFragment(SUMMARY);
			}
			break;
		case R.id.action_refresh:
			Log.d("AreaActivity", "Action: refresh");
			doRefreshFragment();
			break;
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		case R.id.action_dump_db:
			dump_db();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void searchAreasByText(String searchQuery) {
		beginAreaFetch(searchQuery);
	}

	int doRefreshFragment_retries = 0;

	private void doRefreshFragment() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Log.d("AreaActivity",
						"Attempting to refresh the content fragment...");
				try {
					getContentFragment().refreshContent();
					Log.d("AreaActivity", "Attempt successful!");
					doRefreshFragment_retries = 0;
				} catch (NullPointerException e) {
					Log.w("AreaActivity",
							"Null pointer exception when trying to refresh the fragment, will try again in 500ms");
					if (++doRefreshFragment_retries <= 10) {
						doRefreshFragment();
					} else {
						Log.w("AreaActivity",
								"Exceeded maximum number of tries, aborting refresh. Here's what's going on:");
						Log.d("AreaActivity",
								String.format(
										"\tgetContentFragment() => %s\n\tmGeoPoint => %s",
										(getContentFragment() == null ? "null"
												: getContentFragment()
														.getClass()
														.getSimpleName()),
										(mGeoPoint == null ? "null" : mGeoPoint
												.toString())));
					}
				}
			}
		}, 500);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
		doRefreshFragment();
	}

    private void beginAreaFetch (final Location location){
        areaDataService.areaForLocation(location, this);
    }

    private void beginAreaFetch (final String query){
        if(com.uk_postcodes.api.Postcode.isValid(query)){
            Log.d("AreaActivity", "Text query: " + query + " is a postcode, using Area For Postcode");
            areaDataService.areaForPostcode(query, this);
        } else {
            Log.d("AreaActivity", "Text query: " + query + " is not a postcode, using Areas For Name");
            areaDataService.areasForName(query, this);
        }
    }

	/**
	 * Checks if the locator service has new location, then saves it and returns
	 * it.
	 * 
	 * @return User's location.
	 */
	public Location getLocation() {
		if(gServicesConnected()){
            mGeoPoint = mLocationClient.getLastLocation();
        }
        return mGeoPoint;
	}

    public Area getArea() {
        return mArea;
    }

	public boolean isTablet() {
		return is_tablet;
	}

	public boolean isLandscape() {
		return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	private IAreabaseFragment getContentFragment() {
		return mContentFragment;
	}

	private void changeFragment(int fragId) {
		Fragment replacementFragment;
		Bundle args = new Bundle();
        args.putSerializable("argument-area", mArea);
		switch (fragId) {
		// "THIS AREA"
		case SUMMARY:
			replacementFragment = new SummaryFragment();
			if (mGeoPoint != null) {
				args.putParcelable(CURRENT_COORDS, mGeoPoint);
			}
			replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment, false);
			break;
		case CRIME:
			replacementFragment = new SubjectViewFragment();
            args.putString("argument-subject-name", "Crime and Safety");
			replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment, false);
			break;
		case ECONOMY:
			replacementFragment = new SubjectViewFragment();
            args.putSerializable("argument-subject-name", "Economic Deprivation");
            replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment, false);
			break;
		case ENVIRONMENT:
			replacementFragment = new SubjectViewFragment();
            args.putString("argument-subject-name", "Physical Environment");
            replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment, false);
			break;
		// "EXPLORE DATA"
		case EXPLORE_ONS:
			replacementFragment = new SubjectListFragment();
			replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment, false);
			break;
		case EXPLORE_POLICE:
			replacementFragment = new PoliceDataFragment();
			replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment, false);
			break;
		// "MISC." UNUSED
		case AREA_HIERARCHY:
			replacementFragment = new DemoObjectFragment();
			args.putString(DemoObjectFragment.ARGUMENT,
					"ONS-extracted hierarchy of this area will be shown");
			args.putString(DemoObjectFragment.ARGUMENT2, "Hierarchy");
			replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment, false);
			break;
		case AREA_COMPARE:
			replacementFragment = new DemoObjectFragment();
			args.putString(DemoObjectFragment.ARGUMENT,
					"Here are options to compare two areas based on selected datasets.");
			args.putString(DemoObjectFragment.ARGUMENT2, "Compare");
			replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment, false);
			break;
		default:
			Toast.makeText(this, String.format("Unknown link id %d", fragId),
					Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
        String fragclass = getContentFragment() != null ? getContentFragment().getClass().getName() : null;
		super.onSaveInstanceState(outState);
		Log.d("AreaActivity",
				"Saving instance state: currently loaded fragment is "
						+ fragclass != null ? fragclass : "null");
		outState.putString(SIS_LOADED_FRAGMENT, fragclass);
		outState.putParcelable(SIS_LOADED_COORDS, mGeoPoint);
        outState.putSerializable(SIS_LOADED_AREA, mArea);
	}

	private void performFragmentTransaction(Fragment frag, boolean addToBackStack) {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction()
				.replace(mFragmentHostId, frag, frag.getClass().getName());
        if(addToBackStack) {
            transaction.addToBackStack("Areabase");
        }
        transaction.commit();
		if (frag instanceof IAreabaseFragment) {
			mContentFragment = (IAreabaseFragment) frag;
            doRefreshFragment();
		}
	}

	public static Context getAreabaseApplicationContext() {
		return appCtx;
	}

	/**
	 * A helper method that dumps the database contents into a file on the sd
	 * card
	 */
	private void dump_db() {
		new AsyncTask<Void, Integer, Void>() {

			private ProgressDialog pdialog;
			private File f;

			@Override
			protected Void doInBackground(Void... params) {
				FileChannel finch = null;
				FileChannel fonch = null;

				try {
					finch = new FileInputStream(f).getChannel();
					fonch = new FileOutputStream(Environment
							.getExternalStoragePublicDirectory(
									Environment.DIRECTORY_DOWNLOADS)
							.getAbsolutePath()
							+ "/AreabaseCache.db").getChannel();
					fonch.transferFrom(finch, 0, finch.size());
				} catch (Exception e) {
					cancel(true);
				} finally {
					try {
						finch.close();
						fonch.close();
					} catch (Exception e) {
					}
				}
				return null;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pdialog = new ProgressDialog(AreaActivity.this);
				f = getDatabasePath("CacheDb");
				pdialog.setIndeterminate(true);
				pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pdialog.setTitle("Dumping database to sdcard");
				pdialog.show();
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
				// pdialog.setProgress(values[0]);
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				pdialog.dismiss();
			}

			@Override
			protected void onCancelled() {
				super.onCancelled();
				Toast.makeText(appCtx, "DB dump error", Toast.LENGTH_SHORT)
						.show();
				pdialog.dismiss();
			}
		}.execute();
	}

    public void showSubjectView(Area area, String subjectName) {
        Bundle args = new Bundle();
        Fragment replacementFragment = new SubjectViewFragment();
        args.putString("argument-subject-name", subjectName);
        args.putSerializable("argument-area", area);
        replacementFragment.setArguments(args);
        performFragmentTransaction(replacementFragment, true);
    }

    @Override
    public void onBackStackChanged() {
        // Ensure the correct fragment is referenced
        mContentFragment = (IAreabaseFragment) getFragmentManager().findFragmentById(mFragmentHostId);
        if(mGeoPoint != null && mContentFragment != null) {
            mContentFragment.refreshContent();
        }
    }

    @Override
    public void areaReady(Area area) {
        if(area == null){
            Crouton.makeText(this, R.string.error_cannot_fetch_area_data, Style.ALERT).show();
            return;
        }
        mArea = area;
        setTitle(area.getName());
        changeFragment(SUMMARY);
    }

    @Override
    public void areasReady(final List<Area> areas) {
        Log.d("AreaActivity", "Text query: " + areas.size() + " areas to choose from.");
        String[] areaNames = new String[areas.size()];
        for(int i = 0; i < areas.size(); i++){
            areaNames[i] = areas.get(i).getName();
        }
        AlertDialog.Builder bld = new Builder(this);
        bld.setTitle(R.string.select_area_from_list);
        bld.setItems(areaNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Area area = areas.get(which);
                Log.d("AreaActivity",
                        String.format("Text query: choice %d (=> %s)", which, area.getName()));
                areaReady(area);
            }
        });
        bld.show();
    }

    @Override
    public void onError(final Throwable tr) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommonDialogs.areaDataServiceError(tr, AreaActivity.this);
            }
        });
    }

    public void startGraphActivity(DataSetFamily family, Area area){
        Intent intent = new Intent(this, GraphActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(GraphActivity.GRAPH_AREA, area);
        args.putSerializable(GraphActivity.GRAPH_DATASET_FAMILY, family);
        intent.putExtras(args);
        startActivity(intent);
    }
}
