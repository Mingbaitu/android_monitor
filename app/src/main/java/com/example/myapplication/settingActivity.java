package com.example.myapplication;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class settingActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.server_settings);
    }
}
