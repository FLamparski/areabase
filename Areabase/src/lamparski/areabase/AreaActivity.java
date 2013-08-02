package lamparski.areabase;

import lamparski.areabase.dummy.mockup_classes.DemoObjectFragment;
import lamparski.areabase.dummy.mockup_classes.DummyData;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class AreaActivity extends SherlockFragmentActivity {

	protected AreaInfoPagerAdapter mAreaInfoPagerAdapter;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private CharSequence mTitle;

	public static final String[] TAB_NAMES = { "Summary", "Demographics",
			"Indices", "Work", "Crime", "Environment", "Hierarchy", "Misc" };

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			setParentContentView(position);
		}

		private void setParentContentView(int which) {
			Fragment demoFragment = new DemoObjectFragment();
			Bundle args = new Bundle();
			args.putInt(DemoObjectFragment.ARGUMENT, which);
			demoFragment.setArguments(args);

			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager
					.beginTransaction()
					.replace(R.id.handset_area_activity_frameLayout,
							demoFragment).addToBackStack("AreaActivity")
					.commit();

			mDrawerList.setItemChecked(which, true);
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	@Override
	@DummyData(why = "Testing ActionBarSherlock, etc.", replace_with = "Meaningful code for Areabase.")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.handset_area_activity);

		ActionBar mActionBar = getSupportActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		mActionBar.setTitle("Custom title");
		mActionBar.setHomeButtonEnabled(true);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.handset_area_activity_drawerLayout);
		mDrawerList = (ListView) findViewById(R.id.handset_area_activity_navDrawer_listView);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, TAB_NAMES));
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
}
