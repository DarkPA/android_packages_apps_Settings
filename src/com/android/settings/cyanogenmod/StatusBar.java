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
import android.preference.CheckBoxPreference;
import android.preference.ColorPickerPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.util.CMDProcessor;
import com.android.settings.util.Helpers;


public class StatusBar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String STATUS_BAR_CATEGORY_GENERAL = "status_bar_general";
    private static final String STATUS_BAR_CATEGORY_CLOCK = "status_bar_clock";
    private static final String STATUS_BAR_SHOW_AM_PM = "status_bar_show_am_pm";
    private static final String STATUS_BAR_AM_PM_SIZE = "status_bar_am_pm_size";
    private static final String STATUS_BAR_SHOW_WEEKDAY = "status_bar_show_weekday";
    private static final String STATUS_BAR_WEEKDAY_SIZE = "status_bar_weekday_size";
    private static final String STATUS_BAR_SHOW_DAYMONTH = "status_bar_show_daymonth";
    private static final String STATUS_BAR_DAYMONTH_SIZE = "status_bar_daymonth_size";
    private static final String STATUS_BAR_BATTERY = "status_bar_battery";
    private static final String NUMBER_NOT_ICONS = "status_bar_max_notifications";
    private static final String STATUS_BAR_CLOCK = "status_bar_show_clock";
    private static final String STATUS_BAR_CENTER_CLOCK = "status_bar_center_clock";
    private static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";
    private static final String STATUS_BAR_SIGNAL = "status_bar_signal";
    private static final String COMBINED_BAR_AUTO_HIDE = "combined_bar_auto_hide";
    private static final String STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
    private static final String STATUS_BAR_DONOTDISTURB = "status_bar_donotdisturb";
    private static final String PREF_STATUSBAR_BACKGROUND_COLOR = "statusbar_background_color";
    private static final String PREF_COLOR_PICKER = "clock_color";

    private ListPreference mStatusBarAmPmSize;
    private ListPreference mStatusBarBattery;
    private ListPreference mMaxNotIcons;
    private ColorPickerPreference mColorPicker;
    private ListPreference mStatusBarCmSignal;
    private ColorPickerPreference mStatusbarBgColor;
    private ListPreference mStatusBarWeekdaySize;
    private ListPreference mStatusBarDaymonthSize;
    private CheckBoxPreference mStatusBarClock;
    private CheckBoxPreference mStatusBarCenterClock;
    private CheckBoxPreference mStatusBarShowAmPm;
    private CheckBoxPreference mStatusBarShowWeekday;
    private CheckBoxPreference mStatusBarShowDaymonth;
    private CheckBoxPreference mStatusBarBrightnessControl;
    private CheckBoxPreference mCombinedBarAutoHide;
    private CheckBoxPreference mStatusBarNotifCount;
    private CheckBoxPreference mStatusBarDoNotDisturb;
    private PreferenceCategory mPrefCategoryGeneral;
    private PreferenceCategory mPrefCategoryClock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar);

        PreferenceScreen prefSet = getPreferenceScreen();

        mStatusBarClock = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_CLOCK);
        mStatusBarCenterClock = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_CENTER_CLOCK);
        mStatusBarBrightnessControl = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_BRIGHTNESS_CONTROL);
        mStatusBarShowAmPm = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_SHOW_AM_PM);
        mStatusBarAmPmSize = (ListPreference) prefSet.findPreference(STATUS_BAR_AM_PM_SIZE);
        mStatusBarBattery = (ListPreference) prefSet.findPreference(STATUS_BAR_BATTERY);
        mStatusBarShowWeekday = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_SHOW_WEEKDAY);
        mStatusBarWeekdaySize = (ListPreference) prefSet.findPreference(STATUS_BAR_WEEKDAY_SIZE);
        mStatusBarShowDaymonth = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_SHOW_DAYMONTH);
        mStatusBarDaymonthSize = (ListPreference) prefSet.findPreference(STATUS_BAR_DAYMONTH_SIZE);
        mMaxNotIcons = (ListPreference) prefSet.findPreference(NUMBER_NOT_ICONS);
        mCombinedBarAutoHide = (CheckBoxPreference) prefSet.findPreference(COMBINED_BAR_AUTO_HIDE);
        mStatusBarDoNotDisturb = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_DONOTDISTURB);
        mStatusBarCmSignal = (ListPreference) prefSet.findPreference(STATUS_BAR_SIGNAL);
        mColorPicker = (ColorPickerPreference) findPreference(PREF_COLOR_PICKER);
        mColorPicker.setOnPreferenceChangeListener(this);

        mStatusbarBgColor = (ColorPickerPreference) findPreference(PREF_STATUSBAR_BACKGROUND_COLOR);
        mStatusbarBgColor.setOnPreferenceChangeListener(this);

        mStatusBarClock.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_CLOCK, 1) == 1));
        mStatusBarCenterClock.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_CENTER_CLOCK, 0) == 1));
        mStatusBarCenterClock.setEnabled(mStatusBarClock.isChecked());
        mStatusBarShowAmPm.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_AM_PM, 0) == 1));
        mStatusBarShowWeekday.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_WEEKDAY, 0) == 1));
        mStatusBarShowDaymonth.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_DAYMONTH, 0) == 1));
        mStatusBarBrightnessControl.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1));
        mStatusBarDoNotDisturb.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_DONOTDISTURB, 0) == 1));

        try {
            if (Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(), 
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                mStatusBarBrightnessControl.setEnabled(false);
                mStatusBarBrightnessControl.setSummary(R.string.status_bar_toggle_info);
            }
        } catch (SettingNotFoundException e) {
        }

        try {
            if (Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.TIME_12_24) == 24) {
                mStatusBarShowAmPm.setEnabled(false);
                mStatusBarAmPmSize.setEnabled(false);
                mStatusBarShowAmPm.setSummary(R.string.status_bar_am_pm_info);
            }
        } catch (SettingNotFoundException e) {
        }

        int statusBarAmPmSize = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_AM_PM_SIZE, 1);
        mStatusBarAmPmSize.setValue(String.valueOf(statusBarAmPmSize));
        mStatusBarAmPmSize.setSummary(mStatusBarAmPmSize.getEntry());
        mStatusBarAmPmSize.setOnPreferenceChangeListener(this);

        int statusBarWeekdaySize = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_WEEKDAY_SIZE, 1);
        mStatusBarWeekdaySize.setValue(String.valueOf(statusBarWeekdaySize));
        mStatusBarWeekdaySize.setSummary(mStatusBarWeekdaySize.getEntry());
        mStatusBarWeekdaySize.setOnPreferenceChangeListener(this);

        int statusBarDaymonthSize = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_DAYMONTH_SIZE, 1);
        mStatusBarDaymonthSize.setValue(String.valueOf(statusBarDaymonthSize));
        mStatusBarDaymonthSize.setSummary(mStatusBarDaymonthSize.getEntry());
        mStatusBarDaymonthSize.setOnPreferenceChangeListener(this);

        int statusBarBattery = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY, 0);
        mStatusBarBattery.setValue(String.valueOf(statusBarBattery));
        mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());
        mStatusBarBattery.setOnPreferenceChangeListener(this);

        int maxNotIcons = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.MAX_NOTIFICATION_ICONS, 2);
        mMaxNotIcons.setValue(String.valueOf(maxNotIcons));
        mMaxNotIcons.setOnPreferenceChangeListener(this);

        int signalStyle = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);
        mStatusBarCmSignal.setValue(String.valueOf(signalStyle));
        mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntry());
        mStatusBarCmSignal.setOnPreferenceChangeListener(this);

        mCombinedBarAutoHide.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.COMBINED_BAR_AUTO_HIDE, 0) == 1));

        mStatusBarNotifCount = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_NOTIF_COUNT);
        mStatusBarNotifCount.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_NOTIF_COUNT, 0) == 1));

        mPrefCategoryGeneral = (PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_GENERAL);
        mPrefCategoryClock = (PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_CLOCK);

        if (Utils.isTablet()) {
            mPrefCategoryClock.removePreference(mStatusBarCenterClock);
            mPrefCategoryGeneral.removePreference(mStatusBarBrightnessControl);
            mPrefCategoryGeneral.removePreference(mStatusBarCmSignal);
        } else {
            mPrefCategoryGeneral.removePreference(mMaxNotIcons);
            mPrefCategoryGeneral.removePreference(mCombinedBarAutoHide);
        }
    }



    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mStatusBarAmPmSize) {
            int statusBarAmPmSize = Integer.valueOf((String) newValue);
            int index = mStatusBarAmPmSize.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_AM_PM_SIZE, statusBarAmPmSize);
            mStatusBarAmPmSize.setSummary(mStatusBarAmPmSize.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarWeekdaySize) {
            int statusBarWeekdaySize = Integer.valueOf((String) newValue);
            int index = mStatusBarWeekdaySize.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_WEEKDAY_SIZE, statusBarWeekdaySize);
            mStatusBarWeekdaySize.setSummary(mStatusBarWeekdaySize.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarDaymonthSize) {
            int statusBarDaymonthSize = Integer.valueOf((String) newValue);
            int index = mStatusBarDaymonthSize.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_DAYMONTH_SIZE, statusBarDaymonthSize);
            mStatusBarDaymonthSize.setSummary(mStatusBarDaymonthSize.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarBattery) {
            int statusBarBattery = Integer.valueOf((String) newValue);
            int index = mStatusBarBattery.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_BATTERY, statusBarBattery);
            mStatusBarBattery.setSummary(mStatusBarBattery.getEntries()[index]);
            return true;
        } else if (preference == mMaxNotIcons) {
            int maxNotIcons = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.MAX_NOTIFICATION_ICONS, maxNotIcons);
            return true;
        } else if (preference == mStatusbarBgColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hex);

            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BACKGROUND_COLOR, intHex);
            Log.e("CYANOGENMOD", intHex + "");
            return true;
    	} else if (preference == mStatusBarCmSignal) {
            int signalStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarCmSignal.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_SIGNAL_TEXT, signalStyle);
            mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntries()[index]);
            return true;
        } else if (preference == mColorPicker) {
           String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
           preference.setSummary(hex);
           
           int intHex = ColorPickerPreference.convertToColorInt(hex);
           Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_COLOR, intHex);
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mStatusBarClock) {
            value = mStatusBarClock.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_CLOCK, value ? 1 : 0);
            mStatusBarCenterClock.setEnabled(value);
            return true;
        } else if (preference == mStatusBarCenterClock) {
            value = mStatusBarCenterClock.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_CENTER_CLOCK, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarShowAmPm) {
            value = mStatusBarShowAmPm.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_AM_PM, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarShowWeekday) {
            value = mStatusBarShowWeekday.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_WEEKDAY, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarShowDaymonth) {
            value = mStatusBarShowDaymonth.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_DAYMONTH, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarDoNotDisturb) {
            value = mStatusBarDoNotDisturb.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_DONOTDISTURB, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarBrightnessControl) {
            value = mStatusBarBrightnessControl.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, value ? 1 : 0);
            return true;
        } else if (preference == mCombinedBarAutoHide) {
            value = mCombinedBarAutoHide.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.COMBINED_BAR_AUTO_HIDE, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarNotifCount) {
            value = mStatusBarNotifCount.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_NOTIF_COUNT, value ? 1 : 0);
            return true;
        }
        return false;
    }
}
