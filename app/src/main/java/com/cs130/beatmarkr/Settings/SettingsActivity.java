package com.cs130.beatmarkr.Settings;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Justin on 5/19/15.
 */
public class SettingsActivity extends Activity {
    public static String KEY_PREF_GAP_INTERVAL = "pref_gapInterval";
    public static String KEY_PREF_GAP_SOUND = "pref_gapSound";
    public static String KEY_PREF_NAME = "pref_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
