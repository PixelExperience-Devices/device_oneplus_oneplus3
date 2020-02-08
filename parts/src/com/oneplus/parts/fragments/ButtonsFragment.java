/*
 * Copyright (c) 2016 The CyanogenMod Project
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

package com.oneplus.parts.fragments;

import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import com.oneplus.parts.R;
import com.oneplus.parts.Utils;

public class ButtonsFragment extends PreferenceFragment {

    private SwitchPreference mSwap;

    private ListPreference mSliderPositionTop;
    private ListPreference mSliderPositionMiddle;
    private ListPreference mSliderPositionBottom;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fragment_buttons);

        mSwap = (SwitchPreference) findPreference("buttons_swap");
        mSwap.setChecked((boolean) Utils.getPreferenceValue(getActivity().getApplicationContext(), Utils.BUTTONS_SWAP_KEY));
        mSwap.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objectValue) {
                int value = (boolean) objectValue ? 1 : 0;

                Settings.System.putInt(getActivity().getContentResolver(), Utils.BUTTONS_SWAP_KEY, value);

                Utils.setPreferenceToNode(Utils.BUTTONS_SWAP_KEY, Integer.toString(value));
                return true;
            }
        });

        mSliderPositionTop = (ListPreference) findPreference("buttons_slider_position_top");
        mSliderPositionTop.setValue((String) Utils.getPreferenceValue(getActivity().getApplicationContext(), Utils.BUTTONS_SLIDER_TOP_KEY));
        mSliderPositionTop.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objectValue) {
                String value = (String) objectValue;

                Settings.System.putString(getActivity().getContentResolver(), Utils.BUTTONS_SLIDER_TOP_KEY, value);

                Utils.setPreferenceToNode(Utils.BUTTONS_SLIDER_TOP_KEY, value);
                return true;
            }
        });

        mSliderPositionMiddle = (ListPreference) findPreference("buttons_slider_position_middle");
        mSliderPositionMiddle.setValue((String) Utils.getPreferenceValue(getActivity().getApplicationContext(), Utils.BUTTONS_SLIDER_MIDDLE_KEY));
        mSliderPositionMiddle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objectValue) {
                String value = (String) objectValue;

                Settings.System.putString(getActivity().getContentResolver(), Utils.BUTTONS_SLIDER_MIDDLE_KEY, value);

                Utils.setPreferenceToNode(Utils.BUTTONS_SLIDER_MIDDLE_KEY, value);
                return true;
            }
        });

        mSliderPositionBottom = (ListPreference) findPreference("buttons_slider_position_bottom");
        mSliderPositionBottom.setValue((String) Utils.getPreferenceValue(getActivity().getApplicationContext(), Utils.BUTTONS_SLIDER_BOTTOM_KEY));
        mSliderPositionBottom.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objectValue) {
                String value = (String) objectValue;

                Settings.System.putString(getActivity().getContentResolver(), Utils.BUTTONS_SLIDER_BOTTOM_KEY, value);

                Utils.setPreferenceToNode(Utils.BUTTONS_SLIDER_BOTTOM_KEY, value);
                return true;
            }
        });
    }
}
