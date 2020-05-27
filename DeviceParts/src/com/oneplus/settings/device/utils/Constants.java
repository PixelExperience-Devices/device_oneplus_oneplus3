/*
 * Copyright (C) 2015 The CyanogenMod Project
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

package com.oneplus.settings.device.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Constants {

    // Gestures
    public static final String TOUCHSCREEN_CAMERA_GESTURE_KEY = "touchscreen_gesture_camera";
    public static final String TOUCHSCREEN_MUSIC_GESTURE_KEY = "touchscreen_gesture_music";
    public static final String TOUCHSCREEN_FLASHLIGHT_GESTURE_KEY = "touchscreen_gesture_flashlight";

    // Hw Buttons
    public static final String BUTTON_SWAP_KEY = "button_swap";

    // Alert slider
    public static final String NOTIF_SLIDER_TOP_KEY = "keycode_top_position";
    public static final String NOTIF_SLIDER_MIDDLE_KEY = "keycode_middle_position";
    public static final String NOTIF_SLIDER_BOTTOM_KEY = "keycode_bottom_position";

    // Gestures nodes
    public static final String TOUCHSCREEN_CAMERA_NODE = "/proc/touchpanel/letter_o_enable";
    public static final String TOUCHSCREEN_DOUBLE_SWIPE_NODE = "/proc/touchpanel/double_swipe_enable";
    public static final String TOUCHSCREEN_LEFT_ARROW = "/proc/touchpanel/left_arrow_enable";
    public static final String TOUCHSCREEN_RIGHT_ARROW = "/proc/touchpanel/right_arrow_enable";
    public static final String TOUCHSCREEN_FLASHLIGHT_NODE = "/proc/touchpanel/down_arrow_enable";

    // Array of music-related gestures
    public static final String[] TOUCHSCREEN_MUSIC_GESTURES_ARRAY = {TOUCHSCREEN_DOUBLE_SWIPE_NODE, TOUCHSCREEN_LEFT_ARROW, TOUCHSCREEN_RIGHT_ARROW};

    // Gestures nodes default values
    public static final boolean TOUCHSCREEN_CAMERA_DEFAULT = true;
    public static final boolean TOUCHSCREEN_MUSIC_DEFAULT = true;
    public static final boolean TOUCHSCREEN_FLASHLIGHT_DEFAULT = true;

    // Hw Buttons nodes
    public static final String BUTTON_SWAP_NODE = "/proc/s1302/key_rep";

    // Alert slider nodes
    public static final String NOTIF_SLIDER_TOP_NODE = "/proc/tri-state-key/keyCode_top";
    public static final String NOTIF_SLIDER_MIDDLE_NODE = "/proc/tri-state-key/keyCode_middle";
    public static final String NOTIF_SLIDER_BOTTOM_NODE = "/proc/tri-state-key/keyCode_bottom";

    // Holds <preference_key> -> <proc_node> mapping
    public static final Map<String, String> sBooleanNodePreferenceMap = new HashMap<>();
    public static final Map<String, String> sStringNodePreferenceMap = new HashMap<>();

    // Holds <preference_key> -> <default_values> mapping
    public static final Map<String, Object> sNodeDefaultMap = new HashMap<>();

    public static final String[] sGesturePrefKeys = {
        TOUCHSCREEN_CAMERA_GESTURE_KEY,
        TOUCHSCREEN_MUSIC_GESTURE_KEY,
        TOUCHSCREEN_FLASHLIGHT_GESTURE_KEY
    };

    public static final String[] sButtonPrefKeys = {
        BUTTON_SWAP_KEY,
        NOTIF_SLIDER_TOP_KEY,
        NOTIF_SLIDER_MIDDLE_KEY,
        NOTIF_SLIDER_BOTTOM_KEY
    };

    static {
        sBooleanNodePreferenceMap.put(TOUCHSCREEN_CAMERA_GESTURE_KEY, TOUCHSCREEN_CAMERA_NODE);
        sBooleanNodePreferenceMap.put(TOUCHSCREEN_MUSIC_GESTURE_KEY, TOUCHSCREEN_DOUBLE_SWIPE_NODE);
        sBooleanNodePreferenceMap.put(TOUCHSCREEN_FLASHLIGHT_GESTURE_KEY,
                TOUCHSCREEN_FLASHLIGHT_NODE);
        sBooleanNodePreferenceMap.put(BUTTON_SWAP_KEY, BUTTON_SWAP_NODE);
        sStringNodePreferenceMap.put(NOTIF_SLIDER_TOP_KEY, NOTIF_SLIDER_TOP_NODE);
        sStringNodePreferenceMap.put(NOTIF_SLIDER_MIDDLE_KEY, NOTIF_SLIDER_MIDDLE_NODE);
        sStringNodePreferenceMap.put(NOTIF_SLIDER_BOTTOM_KEY, NOTIF_SLIDER_BOTTOM_NODE);

        sNodeDefaultMap.put(TOUCHSCREEN_CAMERA_GESTURE_KEY, TOUCHSCREEN_CAMERA_DEFAULT);
        sNodeDefaultMap.put(TOUCHSCREEN_MUSIC_GESTURE_KEY, TOUCHSCREEN_MUSIC_DEFAULT);
        sNodeDefaultMap.put(TOUCHSCREEN_FLASHLIGHT_GESTURE_KEY, TOUCHSCREEN_FLASHLIGHT_DEFAULT);
        sNodeDefaultMap.put(BUTTON_SWAP_KEY, false);
        sNodeDefaultMap.put(NOTIF_SLIDER_TOP_KEY, "601");
        sNodeDefaultMap.put(NOTIF_SLIDER_MIDDLE_KEY, "602");
        sNodeDefaultMap.put(NOTIF_SLIDER_BOTTOM_KEY, "603");
    }

    public static boolean isPreferenceEnabled(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, (Boolean) sNodeDefaultMap.get(key));
    }

    public static String getPreferenceString(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, (String) sNodeDefaultMap.get(key));
    }
}
