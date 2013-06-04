package lamparski.areabase;

import java.util.Locale;

import lamparski.areabase.dummy.mockup_classes.DummyData;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class AreaActivity extends SherlockFragmentActivity {

	AreaInfoPagerAdapter mAreaInfoPagerAdapter;
	ViewPager mPager;

	public static final String[] TAB_NAMES = { "Summary", "Demographics",
			"Indices", "Work", "Crime", "Environment", "Hierarchy", "Misc" };

	@Override
	@DummyData(why = "Testing ActionBarSherlock, etc.", replace_with = "Meaningful code for Areabase.")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.handset_area_activity);

		mAreaInfoPagerAdapter = new AreaInfoPagerAdapter(
				getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAreaInfoPagerAdapter);

		final ActionBar mActionBar = getSupportActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mActionBar.setTitle("Custom title");
		ActionBar.TabListener mTabListener = new ActionBar.TabListener() {

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mPager.setCurrentItem(tab.getPosition(), true);
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}
		};

		for (String tab_name : TAB_NAMES) {
			mActionBar.addTab(mActionBar.newTab()
					.setText(tab_name.toUpperCase(Locale.UK))
					.setTabListener(mTabListener));
		}

		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mActionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.areabase_opts_menu, menu);
		return true;
	}

}
