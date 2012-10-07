/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.IWindowManager;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class NavigationBar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String NAV_BAR_CATEGORY = "nav_bar_category";
    private static final String NAV_BAR_STATUS = "nav_bar_status";
    private static final String NAV_BAR_TRANSPARENCY = "nav_bar_transparency";
    private static final String NAV_BAR_EDITOR = "nav_bar_editor";
    private static final String NAV_BAR_TABUI_MENU = "nav_bar_tabui_menu";

    private CheckBoxPreference mNavigationBarShow;
    private ListPreference mNavigationBarTransparency;
    private PreferenceScreen mNavigationBarEditor;
    private CheckBoxPreference mMenuButtonShow;
    private PreferenceCategory mPrefCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.navigation_bar);

        PreferenceScreen prefSet = getPreferenceScreen();

        mNavigationBarShow = (CheckBoxPreference) prefSet.findPreference(NAV_BAR_STATUS);
        mNavigationBarEditor = (PreferenceScreen) prefSet.findPreference(NAV_BAR_EDITOR);
        mMenuButtonShow = (CheckBoxPreference) prefSet.findPreference(NAV_BAR_TABUI_MENU);
        mNavigationBarTransparency = (ListPreference) prefSet.findPreference(NAV_BAR_TRANSPARENCY);

        IWindowManager wm = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
        try {
            mNavigationBarShow.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NAV_BAR_STATUS, !wm.hasHardwareKeys() ? 1 : 0) == 1));
        } catch (RemoteException ex) {
            // too bad, so sad, oh mom, oh dad
        }

        mMenuButtonShow.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.NAV_BAR_TABUI_MENU, 0) == 1));

        mNavigationBarEditor.setEnabled(mNavigationBarShow.isChecked());
        mMenuButtonShow.setEnabled(mNavigationBarShow.isChecked());

        int navBarTransparency = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.NAV_BAR_TRANSPARENCY, 100);
        mNavigationBarTransparency.setValue(String.valueOf(navBarTransparency));
        mNavigationBarTransparency.setOnPreferenceChangeListener(this);

        mPrefCategory = (PreferenceCategory) findPreference(NAV_BAR_CATEGORY);

        if (!Utils.isTablet()) {
            mPrefCategory.removePreference(mMenuButtonShow);
        } else {
            mPrefCategory.removePreference(mNavigationBarTransparency);
            mPrefCategory.removePreference(mNavigationBarEditor);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNavigationBarTransparency) {
            int navBarTransparency = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NAV_BAR_TRANSPARENCY, navBarTransparency);
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mNavigationBarShow) {
            value = mNavigationBarShow.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NAV_BAR_STATUS, value ? 1 : 0);
            mNavigationBarEditor.setEnabled(value);
            mMenuButtonShow.setEnabled(value);
            return true;
        }
        else if (preference == mMenuButtonShow) {
            value = mMenuButtonShow.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NAV_BAR_TABUI_MENU, value ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
