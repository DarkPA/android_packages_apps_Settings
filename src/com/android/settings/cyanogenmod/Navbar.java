/*
 * Copyright (C) 2012 The AOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListFragment;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.ColorPickerPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.cyanogenmod.NavRingTargets;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.NavBarItemPreference;
import com.android.settings.widget.SeekBarPreference;
import com.android.settings.widget.TouchInterceptor;
import com.android.settings.util.Helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Navbar extends SettingsPreferenceFragment implements
            OnPreferenceChangeListener {

    private static final boolean DEBUG = false;

    private static final String PREF_NAVRING_AMOUNT = "pref_navring_amount";

    private Preference mNavRingTargets;
    private ListPreference mNavRingButtonQty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_navbar);

        PreferenceScreen prefs = getPreferenceScreen();

        mNavRingTargets = findPreference("navring_settings");

        mNavRingButtonQty = (ListPreference) findPreference(PREF_NAVRING_AMOUNT);
        mNavRingButtonQty.setOnPreferenceChangeListener(this);
        mNavRingButtonQty.setValue(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.SYSTEMUI_NAVRING_AMOUNT, 1) + "");

    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {

        if (preference == mNavRingTargets) {
            ((PreferenceActivity) getActivity())
                    .startPreferenceFragment(new NavRingTargets(), true);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNavRingButtonQty) {
            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SYSTEMUI_NAVRING_AMOUNT, val);
            resetNavRing();
            refreshSettings();
            Helpers.restartSystemUI();
            return true;
        }
        return false;
    }

    public void resetNavRing() {
            Settings.System.putString(getActivity().getContentResolver(),
                    Settings.System.SYSTEMUI_NAVRING_1, "none");
            Settings.System.putString(getActivity().getContentResolver(),
                    Settings.System.SYSTEMUI_NAVRING_2, "none");
            Settings.System.putString(getActivity().getContentResolver(),
                    Settings.System.SYSTEMUI_NAVRING_3, "assist");
            Settings.System.putString(getActivity().getContentResolver(),
                    Settings.System.SYSTEMUI_NAVRING_4, "none");
            Settings.System.putString(getActivity().getContentResolver(),
                    Settings.System.SYSTEMUI_NAVRING_5, "none");
    }

    public void refreshSettings() {

    }

}
