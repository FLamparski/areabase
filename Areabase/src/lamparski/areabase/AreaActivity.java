package lamparski.areabase;

import java.util.ArrayList;
import java.util.List;

import lamparski.areabase.dummy.mockup_classes.DemoObjectFragment;
import lamparski.areabase.dummy.mockup_classes.DummyData;
import lamparski.areabase.fragments.IAreabaseFragment;
import lamparski.areabase.services.AreabaseLocatorService;
import lamparski.areabase.services.AreabaseLocatorService.AreabaseLocatorBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class AreaActivity extends SherlockFragmentActivity {

	// private AreaInfoPagerAdapter mAreaInfoPagerAdapter;
	protected NavDrawerListAdapter mNavDrawerAdapter;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private CharSequence mTitle;
	private Location mGeoPoint = null;
	private IAreabaseFragment mContentFragment = null;
	private AreabaseLocatorService mLocatorService;

	protected boolean is_tablet = false;
	protected boolean is_landscape = false;
	private boolean is_locator_bound;

	public static final int SUMMARY = 0;
	public static final int CRIME = 1;
	public static final int ECONOMY = 2;
	public static final int ENVIRONMENT = 3;
	public static final int EXPLORE_ONS = 10;
	public static final int EXPLORE_POLICE = 11;
	public static final int AREA_HIERARCHY = 15;
	public static final int AREA_COMPARE = 16;
	public static final String CURRENT_COORDS = "current-coords";

	private static final String SIS_LOADED_VIEW = "currently-loaded-view";
	private static final String SIS_LOADED_COORDS = "currently-loaded-coordinates";

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			NavDrawerListItemModel item = (NavDrawerListItemModel) mNavDrawerAdapter
					.getItem(position);
			changeFragment(item.getFragId());

			mDrawerList.setItemChecked(position, true);
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	@Override
	@DummyData(why = "Testing ActionBarSherlock, etc.", replace_with = "Meaningful code for Areabase.")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.area_activity);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		ActionBar mActionBar = getSupportActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		mActionBar.setTitle("Custom title");
		mActionBar.setHomeButtonEnabled(true);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.tablet_area_activity_drawerLayout);
		if (mDrawerLayout != null) {
			mDrawerList = (ListView) findViewById(R.id.tablet_area_activity_navDrawer_listView);
			is_tablet = true;
			Log.i(getClass().getName(),
					"Loading tablet version of AreaActivity");
		} else {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.handset_area_activity_drawerLayout_DEFAULT);
			mDrawerList = (ListView) findViewById(R.id.handset_area_activity_navDrawer_listView_DEFAULT);
			is_tablet = false;
			Log.i(getClass().getName(),
					"Loading handset version of AreaActivity");
		}

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		NavDrawerListAdapter navigationAdapter = setUpNavDrawer();
		mDrawerList.setAdapter(navigationAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				getSupportActionBar().setTitle(mTitle);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle("Areabase");
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mActionBar.setDisplayHomeAsUpEnabled(true);
		mDrawerLayout.closeDrawer(mDrawerList);

		if (savedInstanceState != null) {
			mGeoPoint = savedInstanceState.getParcelable(SIS_LOADED_COORDS);
			changeFragment(savedInstanceState.getInt(SIS_LOADED_VIEW));
		} else {
			mGeoPoint = null;
			changeFragment(SUMMARY);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		Intent intent = new Intent(this, AreabaseLocatorService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		if (getSharedPreferences("AreabasePrefs", 0).getBoolean(
				"pref_location_autolocate", true)) {
			mLocatorService.startLocationListening();
			sOnUpdateLocation.execute(5); // Fast fix -- 5 tries, no target
											// accuracy.
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (is_locator_bound) {
			if (mLocatorService.isListening())
				mLocatorService.stopLocationListening();
			unbindService(mConnection);
			is_locator_bound = false;
		}
	}

	private AsyncTask<Integer, Integer, Location> sOnUpdateLocation = new AsyncTask<Integer, Integer, Location>() {

		@Override
		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(true);
		};

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Location doInBackground(Integer... params) {
			Location bestLocation = null;
			int tries = params[0];
			int targetAccuracy = Integer.MAX_VALUE;
			if (params.length > 1)
				targetAccuracy = params[1];
			int currentTry = 0;
			int currentAccuracy = targetAccuracy + 1000;
			while (currentTry < tries && currentAccuracy > targetAccuracy) {
				// This loop quits when it's exceeded the number of allotted
				// tries, or if it has found an accurate fix.
				if (is_locator_bound) {
					currentTry++;
					if (bestLocation == null)
						bestLocation = mLocatorService.getLocation();

					if (mLocatorService.hasBetterLocation())
						bestLocation = mLocatorService.getLocation();

					currentAccuracy = (int) bestLocation.getAccuracy();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						continue;
					}
				}
			}
			return bestLocation;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Location result) {
			mGeoPoint = result;
			if (!(getSharedPreferences("AreabasePrefs", 0).getBoolean(
					"pref_location_backgroundlocate", true))) {
				mLocatorService.stopLocationListening();
			}
			setSupportProgressBarIndeterminateVisibility(false);
			mContentFragment.updateGeo(result);
			mContentFragment.refreshContent();
		}

	};

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			is_locator_bound = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			AreabaseLocatorBinder lBinder = (AreabaseLocatorBinder) service;
			mLocatorService = lBinder.getService();
			is_locator_bound = true;
		}
	};

	private NavDrawerListAdapter setUpNavDrawer() {
		mNavDrawerAdapter = new NavDrawerListAdapter(this);

		// section: "THIS AREA"
		List<NavDrawerListItemModel> thisAreaItems = new ArrayList<NavDrawerListItemModel>();
		thisAreaItems.add(new NavDrawerListItemModel(R.string.caption_summary,
				R.drawable.navicon_summary, SUMMARY));
		thisAreaItems.add(new NavDrawerListItemModel(R.string.caption_crime,
				R.drawable.navicon_crime, CRIME));
		thisAreaItems.add(new NavDrawerListItemModel(R.string.caption_economy,
				R.drawable.navicon_economy, ECONOMY));
		thisAreaItems.add(new NavDrawerListItemModel(
				R.string.caption_environment, R.drawable.navicon_environment,
				ENVIRONMENT));
		NavDrawerSectionAdapter thisAreaSecAdapter = new NavDrawerSectionAdapter(
				this, thisAreaItems);
		mNavDrawerAdapter.addSection(
				getString(R.string.navdrawer_secheader_basic_info),
				thisAreaSecAdapter);

		// section: "EXPLORE DATA"
		List<NavDrawerListItemModel> exploreDataItems = new ArrayList<NavDrawerListItemModel>();
		exploreDataItems.add(new NavDrawerListItemModel(R.string.caption_ONS,
				R.drawable.action_search, EXPLORE_ONS));
		exploreDataItems.add(new NavDrawerListItemModel(
				R.string.caption_Police, R.drawable.action_search,
				EXPLORE_POLICE));
		NavDrawerSectionAdapter exploreDataSecAdapter = new NavDrawerSectionAdapter(
				this, exploreDataItems);
		mNavDrawerAdapter.addSection(
				getString(R.string.navdrawer_secheader_more_data),
				exploreDataSecAdapter);

		// section: "MISCELLANEOUS"
		List<NavDrawerListItemModel> miscItems = new ArrayList<NavDrawerListItemModel>();
		miscItems.add(new NavDrawerListItemModel(
				R.string.caption_area_hierarchy, R.drawable.navicon_hierarchy,
				AREA_HIERARCHY));
		miscItems.add(new NavDrawerListItemModel(R.string.caption_area_compare,
				R.drawable.navicon_compare, AREA_COMPARE));
		NavDrawerSectionAdapter miscSecAdapter = new NavDrawerSectionAdapter(
				this, miscItems);
		mNavDrawerAdapter.addSection(
				getString(R.string.navdrawer_secheader_misc), miscSecAdapter);

		return mNavDrawerAdapter;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.areabase_opts_menu, menu);

		/*
		 * Search action: Handle expanding the search box and starting the
		 * search procedure.
		 */
		final EditText mSearchBox = (EditText) menu
				.findItem(R.id.action_search).getActionView();
		MenuItem searchMenuItem = menu.findItem(R.id.action_search);
		searchMenuItem
				.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

					@Override
					public boolean onMenuItemActionCollapse(MenuItem item) {
						// Clear the search box
						mSearchBox.clearFocus();
						mSearchBox.setText("");
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
							/*
							 * Only execute this if the user has pressed the
							 * 'Search' Enter-like key on the soft keyboard, or
							 * the hardware Enter key.
							 */
							searchAreasByText(v.getText().toString());
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
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else if (!(mDrawerLayout.isDrawerOpen(mDrawerList))) {
				mDrawerLayout.openDrawer(mDrawerList);
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
		mContentFragment.refreshContent();
	}

	private void updateLocation() {
		// Max. 30 tries(secs), target accuracy = 50m.
		sOnUpdateLocation.execute(30, 50);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Checks if the locator service has new location, then saves it and returns
	 * it.
	 * 
	 * @return User's location.
	 */
	public Location getLocation() {
		if (is_locator_bound) {
			if (mLocatorService.hasBetterLocation())
				mGeoPoint = mLocatorService.getLocation();
		}
		return mGeoPoint;
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
		}
	}

	private void performFragmentTransaction(Fragment frag) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.handset_area_activity_frameLayout_DEFAULT, frag)
				.addToBackStack("AreaActivity").commit();
		if (frag instanceof IAreabaseFragment) {
			mContentFragment = (IAreabaseFragment) frag;
		}
	}

}
