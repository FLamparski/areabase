package lamparski.areabase;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Handles settings.
 */
public class SettingsActivity extends PreferenceActivity {

	public static boolean ALWAYS_SIMPLE_PREFERENCES = false;
    private static final long UNIX_30_DAYS = 1000l * 60 * 60 * 24 * 30;

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
            Preference clearOldData = (Preference) findPreference("pref_storage_remove_old_data");
            assert clearOldData != null;
            clearOldData.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if(getActivity() != null){
                        String[] whereArgs = new String[] { Long.toString(System.currentTimeMillis() - UNIX_30_DAYS) };
                        getActivity().getContentResolver().delete(CacheContentProvider.AREARANK_CACHE_URI, "computedOn < ?", whereArgs);
                        getActivity().getContentResolver().delete(CacheContentProvider.ONS_CACHE_URI, "retrievedOn < ?", whereArgs);
                        getActivity().getContentResolver().delete(CacheContentProvider.MAPIT_CACHE_URI, "retrievedOn < ?", whereArgs);
                        getActivity().getContentResolver().delete(CacheContentProvider.POLICE_CACHE_URI, "retrievedOn < ?", whereArgs);
                        Crouton.makeText(getActivity(), "All done!", Style.CONFIRM).show();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            Preference clearScores = (Preference) findPreference("pref_storage_reset_scores");
            assert clearScores != null;
            clearScores.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if(getActivity() != null){
                        /**
                         * DELETE FROM areaRank;
                         */
                        getActivity().getContentResolver().delete(CacheContentProvider.AREARANK_CACHE_URI, "", new String[]{});
                        Crouton.makeText(getActivity(), "All done!", Style.CONFIRM).show();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
		}

	}
}
