package lamparski.areabase;

import lamparski.areabase.dummy.mockup_classes.DemoObjectFragment;
import lamparski.areabase.fragments.IAreabaseFragment;
import lamparski.areabase.fragments.SummaryFragment;
import lamparski.areabase.widgets.RobotoLightTextView;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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

public class AreaActivity extends Activity {

	// private AreaInfoPagerAdapter mAreaInfoPagerAdapter;
	protected NavDrawerListAdapter mNavDrawerAdapter;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private LinearLayout mDrawer;
	private CharSequence mTitle;
	private Location mGeoPoint = null;
	private IAreabaseFragment mContentFragment = null;
	private int mFragmentHostId;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	protected boolean is_tablet = false;
	protected boolean is_landscape = false;

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

	private static final String SIS_LOADED_VIEW = "currently-loaded-view";
	private static final String SIS_LOADED_COORDS = "currently-loaded-coordinates";

	private OnClickListener sDrawerLinkListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() != GET_HELP) {
				changeFragment(v.getId());
			} else {
				// TODO: Launch a browser for help
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("AreaActivity", "onCreate() called");
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.area_activity);

		ActionBar mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		mActionBar.setTitle("Areabase");
		mActionBar.setHomeButtonEnabled(true);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.tablet_area_activity_drawerLayout);
		if (mDrawerLayout != null) {
			mDrawer = (LinearLayout) findViewById(R.id.tablet_area_activity_navDrawer_listView);
			is_tablet = true;
			mFragmentHostId = R.id.tablet_area_activity_frameLayout;
			Log.i(getClass().getName(),
					"Loading tablet version of AreaActivity");
		} else {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.handset_area_activity_drawerLayout_DEFAULT);
			mDrawer = (LinearLayout) findViewById(R.id.handset_area_activity_navDrawer_listView_DEFAULT);
			is_tablet = false;
			mFragmentHostId = R.id.handset_area_activity_frameLayout_DEFAULT;
			Log.i(getClass().getName(),
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

		if (savedInstanceState != null) {
			// Log.d("AreaActivity", "  > savedInstanceState != null");
			// mGeoPoint = savedInstanceState.getParcelable(SIS_LOADED_COORDS);
			// changeFragment(savedInstanceState.getInt(SIS_LOADED_VIEW));
		} else {
			mGeoPoint = new Location("mock");
			mGeoPoint.setLongitude(-0.041229);
			mGeoPoint.setLatitude(51.448800);
			changeFragment(SUMMARY);
			doRefreshFragment();
		}
	}

	private void setUpNavDrawer() {
		int drawerChildrenCount = mDrawer.getChildCount();
		for (int i = 0; i < drawerChildrenCount; i++) {
			View v_at_i = mDrawer.getChildAt(i);
			if (v_at_i instanceof RobotoLightTextView) {
				v_at_i.setOnClickListener(sDrawerLinkListener);
			} else if (v_at_i instanceof TextView) {
				if (v_at_i.isFocusable()) {
					v_at_i.setOnClickListener(sDrawerLinkListener);
				}
			}
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 * TODO: This is to prepare this Activity to communicate with the Google
		 * Play Location Services.
		 */
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			Log.w("AreaActivity",
					"Error resolution request. Intent: " + data.toString());
			break;
		default:
			break;
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
			updateLocation();
			break;
		case R.id.action_refresh:
			doRefreshFragment();
			break;
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void searchAreasByText(String searchQuery) {
		mContentFragment.searchByText(searchQuery);
	}

	private void doRefreshFragment() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					getContentFragment().updateGeo(mGeoPoint);
					getContentFragment().refreshContent();
				} catch (NullPointerException e) {
					doRefreshFragment();
				}
			}
		}, 500);
	}

	private void updateLocation() {
		// Max. 30 tries(secs), target accuracy = 50m.
		// sOnUpdateLocation.execute(30, 50);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
		doRefreshFragment();
	}

	/**
	 * Checks if the locator service has new location, then saves it and returns
	 * it.
	 * 
	 * @return User's location.
	 */
	public Location getLocation() {
		// if (is_locator_bound) {
		// if (mLocatorService.hasBetterLocation())
		// mGeoPoint = mLocatorService.getLocation();
		// }

		// return mGeoPoint;

		Location fakeLocation = new Location("MDMA");
		fakeLocation.setLongitude(-0.0879756);
		fakeLocation.setLatitude(51.5132473);
		return fakeLocation;
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
		switch (fragId) {
		// "THIS AREA"
		case SUMMARY:
			replacementFragment = new SummaryFragment();
			if (mGeoPoint != null) {
				args.putParcelable(CURRENT_COORDS, mGeoPoint);
			}
			replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment);
			break;
		case CRIME:
			replacementFragment = new DemoObjectFragment();
			args.putString(DemoObjectFragment.ARGUMENT,
					"Collated crime data will be shown here.");
			args.putString(DemoObjectFragment.ARGUMENT2, "Crime");
			replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment);
			break;
		case ECONOMY:
			replacementFragment = new DemoObjectFragment();
			args.putString(DemoObjectFragment.ARGUMENT,
					"Collated economy data will be shown here.");
			args.putString(DemoObjectFragment.ARGUMENT2, "Economy");
			replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment);
			break;
		case ENVIRONMENT:
			replacementFragment = new DemoObjectFragment();
			args.putString(DemoObjectFragment.ARGUMENT,
					"Collated environmental data will be shown here.");
			args.putString(DemoObjectFragment.ARGUMENT2, "Environment");
			replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment);
			break;
		// "EXPLORE DATA"
		case EXPLORE_ONS:
			replacementFragment = new DemoObjectFragment();
			args.putString(DemoObjectFragment.ARGUMENT,
					"A list of area-compatible topics will be shown here");
			args.putString(DemoObjectFragment.ARGUMENT2, "ONS Data");
			replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment);
			break;
		case EXPLORE_POLICE:
			replacementFragment = new DemoObjectFragment();
			args.putString(DemoObjectFragment.ARGUMENT,
					"Various Police-related information will be shown here");
			args.putString(DemoObjectFragment.ARGUMENT2, "Police Data");
			replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment);
			break;
		// "MISC."
		case AREA_HIERARCHY:
			replacementFragment = new DemoObjectFragment();
			args.putString(DemoObjectFragment.ARGUMENT,
					"ONS-extracted hierarchy of this area will be shown");
			args.putString(DemoObjectFragment.ARGUMENT2, "Hierarchy");
			replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment);
			break;
		case AREA_COMPARE:
			replacementFragment = new DemoObjectFragment();
			args.putString(DemoObjectFragment.ARGUMENT,
					"Here are options to compare two areas based on selected datasets.");
			args.putString(DemoObjectFragment.ARGUMENT2, "Compare");
			replacementFragment.setArguments(args);
			performFragmentTransaction(replacementFragment);
			break;
		default:
			Toast.makeText(this, String.format("Unknown link id %d", fragId),
					Toast.LENGTH_SHORT).show();
			break;
		}
	}

	private void performFragmentTransaction(Fragment frag) {
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(mFragmentHostId, frag)
				.commit();
		if (frag instanceof IAreabaseFragment) {
			mContentFragment = (IAreabaseFragment) frag;
		}
	}

}
