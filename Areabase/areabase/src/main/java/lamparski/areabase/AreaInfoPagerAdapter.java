package lamparski.areabase;

import lamparski.areabase.dummy.mockup_classes.DemoObjectFragment;
import lamparski.areabase.dummy.mockup_classes.DummyData;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

public class AreaInfoPagerAdapter extends FragmentStatePagerAdapter {

	public AreaInfoPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	@DummyData(why = "Need to set up the tabs correctly", replace_with = "Areabase tabs such as Summary, Demographics, etc.")
	public Fragment getItem(int i) {
		Fragment frag = new DemoObjectFragment();
		Bundle args = new Bundle();
		args.putInt(DemoObjectFragment.ARGUMENT, i);
		frag.setArguments(args);
		return frag;
	}

	@Override
	@DummyData(why = "Need to set up the tabs correctly", replace_with = "Areabase tabs such as Summary, Demographics, etc.")
	public int getCount() {
		// Number of demo stupid pages
		return 0;
	}

	@SuppressLint("DefaultLocale")
	@Override
	@DummyData(why = "Need to set up the tabs correctly", replace_with = "Areabase tabs such as Summary, Demographics, etc.")
	public CharSequence getPageTitle(int position) {
		return null;
	}

}
