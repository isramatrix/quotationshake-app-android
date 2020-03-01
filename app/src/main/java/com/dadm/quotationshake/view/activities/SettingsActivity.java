package com.dadm.quotationshake.view.activities;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.dadm.quotationshake.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        createSettingsFragment(R.id.settings_view);
    }

    private void createSettingsFragment(@IdRes int viewId)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(viewId, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat
    {
        /**
         * Called during {@link #onCreate(Bundle)} to supply the preferences for this fragment.
         * Subclasses are expected to call {@link #setPreferenceScreen(PreferenceScreen)} either
         * directly or via helper methods such as {@link #addPreferencesFromResource(int)}.
         *
         * @param savedInstanceState If the fragment is being re-created from a previous saved state,
         *                           this is the state.
         * @param rootKey            If non-null, this preference fragment should be rooted at the
         *                           {@link PreferenceScreen} with this key.
         */
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_settings, rootKey);
        }
    }
}
