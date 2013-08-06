package lamparski.areabase;

import java.util.ArrayList;
import java.util.List;

import lamparski.areabase.dummy.mockup_classes.DemoObjectFragment;
import lamparski.areabase.dummy.mockup_classes.DummyData;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class AreaActivity extends SherlockFragmentActivity {

	// private AreaInfoPagerAdapter mAreaInfoPagerAdapter;
	protected NavDrawerListAdapter mNavDrawerAdapter;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private CharSequence mTitle;
	private double[] mGeoPoint = null;

	protected boolean is_tablet = false;
	protected boolean is_landscape = false;

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
			mGeoPoint = savedInstanceState.getDoubleArray(SIS_LOADED_COORDS);
			changeFragment(savedInstanceState.getInt(SIS_LOADED_VIEW));
		} else {
			mGeoPoint = null;
			changeFragment(SUMMARY);
		}
	}

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
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else if (!(mDrawerLayout.isDrawerOpen(mDrawerList))) {
				mDrawerLayout.openDrawer(mDrawerList);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public double[] getGeoPoint() {
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
				args.putDoubleArray(CURRENT_COORDS, mGeoPoint);
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
	}

}
