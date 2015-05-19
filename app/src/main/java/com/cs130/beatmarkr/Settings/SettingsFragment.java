package com.cs130.beatmarkr.Settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;


import com.cs130.beatmarkr.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
