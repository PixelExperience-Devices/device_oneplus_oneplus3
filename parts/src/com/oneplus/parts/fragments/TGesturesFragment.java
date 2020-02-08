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

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import com.oneplus.parts.R;
import com.oneplus.parts.Utils;

public class TGesturesFragment extends PreferenceFragment {

    private SwitchPreference mGestureCamera;
    private SwitchPreference mGestureFlashlight;
    private SwitchPreference mGestureMusic;
    private SwitchPreference mHapticFeedback;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fragment_tgestures);

        mGestureCamera = (SwitchPreference) findPreference("tgestures_camera");
        mGestureCamera.setChecked((boolean) Utils.getPreferenceValue(getActivity().getApplicationContext(), Utils.TGESTURES_CAMERA_KEY));
        mGestureCamera.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objectValue) {
                int value = (boolean) objectValue ? 1 : 0;

                Settings.System.putInt(getActivity().getContentResolver(), Utils.TGESTURES_CAMERA_KEY, value);

                Utils.setPreferenceToNode(Utils.TGESTURES_CAMERA_KEY, Integer.toString(value));
                return true;
            }
        });

        mGestureFlashlight = (SwitchPreference) findPreference("tgestures_flashlight");
        mGestureFlashlight.setChecked((boolean) Utils.getPreferenceValue(getActivity().getApplicationContext(), Utils.TGESTURES_FLASHLIGHT_KEY));
        mGestureFlashlight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objectValue) {
                int value = (boolean) objectValue ? 1 : 0;

                Settings.System.putInt(getActivity().getContentResolver(), Utils.TGESTURES_FLASHLIGHT_KEY, value);

                Utils.setPreferenceToNode(Utils.TGESTURES_FLASHLIGHT_KEY, Integer.toString(value));
                return true;
            }
        });

        mGestureMusic = (SwitchPreference) findPreference("tgestures_music");
        mGestureMusic.setChecked((boolean) Utils.getPreferenceValue(getActivity().getApplicationContext(), Utils.TGESTURES_MUSIC_KEY));
        mGestureMusic.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objectValue) {
                int value = (boolean) objectValue ? 1 : 0;

                Settings.System.putInt(getActivity().getContentResolver(), Utils.TGESTURES_MUSIC_KEY, value);

                for (String node : Utils.TGESTURES_MUSIC_NODES) {
                    Utils.setPreferenceToNode(node, Integer.toString(value));
                }
                return true;
            }
        });

        mHapticFeedback = (SwitchPreference) findPreference("tgestures_haptic_feedback");
        mHapticFeedback.setChecked((boolean) Utils.getPreferenceValue(getActivity().getApplicationContext(), Utils.TGESTURES_HAPTIC_FEEDBACK_KEY));
        mHapticFeedback.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objectValue) {
                int value = (boolean) objectValue ? 1 : 0;

                Settings.System.putInt(getActivity().getContentResolver(), Utils.TGESTURES_HAPTIC_FEEDBACK_KEY, value);

                // Node for TGESTURES_HAPTIC_FEEDBACK_KEY does not exist
                return true;
            }
        });
    }
}
