package lamparski.areabase;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class SettingsActivity extends PreferenceActivity {

	public static boolean ALWAYS_SIMPLE_PREFERENCES = false;

	private boolean isSimplePrefs() {
		return ALWAYS_SIMPLE_PREFERENCES
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceActivity#isMultiPane()
	 */
	@Override
	public boolean isMultiPane() {
		// TODO Auto-generated method stub
		return super.isMultiPane();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (isSimplePrefs()) {
			addPreferencesFromResource(R.xml.preferences);
		} else {
			createPreferencesFragment();
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void createPreferencesFragment() {
		FragmentManager mgr = getFragmentManager();
		mgr.beginTransaction()
				.replace(android.R.id.content, new MyPreferenceFragment())
				.commit();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class MyPreferenceFragment extends PreferenceFragment {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.preference.PreferenceFragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
		}

	}
}
