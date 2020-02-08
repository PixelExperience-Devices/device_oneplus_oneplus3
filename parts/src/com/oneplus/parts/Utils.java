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

package com.oneplus.parts;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseIntArray;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Utils {
    private static String TAG = "OnePlusParts-Utils";

    public static boolean BUTTONS_BOOTED = false;
    public static boolean TGESTURES_BOOTED = false;

    public static int ZEN_MODE_VIBRATION = 4;

    // Keycodes
    public static final int KEY_DOUBLE_TAP = 249;
    public static final int KEY_GESTURE_CIRCLE = 250;
    public static final int KEY_GESTURE_TWO_SWIPE = 251;
    public static final int KEY_GESTURE_DOWN_ARROW = 252;
    public static final int KEY_GESTURE_LEFT_V = 253;
    public static final int KEY_GESTURE_RIGHT_V = 254;

    public static final int KEY_SLIDER_MODE_TOTAL_SILENCE = 600;
    public static final int KEY_SLIDER_MODE_PRIORITY_ONLY = 601;
    public static final int KEY_SLIDER_MODE_VIBRATION = 602;
    public static final int KEY_SLIDER_MODE_NONE = 603;

    public static ArrayList<Integer> sSupportedTGestures = new ArrayList<>();
    static {
        sSupportedTGestures.add(KEY_DOUBLE_TAP);
        sSupportedTGestures.add(KEY_GESTURE_CIRCLE);
        sSupportedTGestures.add(KEY_GESTURE_TWO_SWIPE);
        sSupportedTGestures.add(KEY_GESTURE_DOWN_ARROW);
        sSupportedTGestures.add(KEY_GESTURE_LEFT_V);
        sSupportedTGestures.add(KEY_GESTURE_RIGHT_V);
    }

    public static SparseIntArray sSupportedSliderModes = new SparseIntArray();
    static {
        sSupportedSliderModes.put(KEY_SLIDER_MODE_TOTAL_SILENCE, Settings.Global.ZEN_MODE_NO_INTERRUPTIONS);
        sSupportedSliderModes.put(KEY_SLIDER_MODE_PRIORITY_ONLY, Settings.Global.ZEN_MODE_IMPORTANT_INTERRUPTIONS);
        sSupportedSliderModes.put(KEY_SLIDER_MODE_VIBRATION, ZEN_MODE_VIBRATION);
        sSupportedSliderModes.put(KEY_SLIDER_MODE_NONE, Settings.Global.ZEN_MODE_OFF);
    }

    // Buttons
    public static String BUTTONS_SWAP_KEY = "buttons_swap";
    public static String BUTTONS_SWAP_NODE = "/proc/s1302/key_rep";

    public static String BUTTONS_SLIDER_TOP_KEY = "buttons_slider_top";
    public static String BUTTONS_SLIDER_MIDDLE_KEY = "buttons_slider_middle";
    public static String BUTTONS_SLIDER_BOTTOM_KEY = "buttons_slider_bottom";

    public static String BUTTONS_SLIDER_TOP_NODE = "/proc/tri-state-key/keyCode_top";
    public static String BUTTONS_SLIDER_MIDDLE_NODE = "/proc/tri-state-key/keyCode_middle";
    public static String BUTTONS_SLIDER_BOTTOM_NODE = "/proc/tri-state-key/keyCode_bottom";

    // Touchscreen gestures
    public static String TGESTURES_CAMERA_KEY = "tgestures_camera";
    public static String TGESTURES_FLASHLIGHT_KEY = "tgestures_flashlight";
    public static String TGESTURES_MUSIC_KEY = "tgestures_music";
    public static String TGESTURES_HAPTIC_FEEDBACK_KEY = "tgestures_haptic_feedback";

    public static String TGESTURES_CAMERA_NODE = "/proc/touchpanel/letter_o_enable";
    public static String TGESTURES_FLASHLIGHT_NODE = "/proc/touchpanel/down_arrow_enable";
    public static String TGESTURES_MUSIC_NODE_1 = "/proc/touchpanel/double_swipe_enable";
    public static String TGESTURES_MUSIC_NODE_2 = "/proc/touchpanel/left_arrow_enable";
    public static String TGESTURES_MUSIC_NODE_3 = "/proc/touchpanel/right_arrow_enable";
    public static String[] TGESTURES_MUSIC_NODES = {TGESTURES_MUSIC_NODE_1, TGESTURES_MUSIC_NODE_2, TGESTURES_MUSIC_NODE_3};

    // <PREFERENCE_KEY> -> <PREFERENCE_NODE> mapping
    public static HashMap<String, String> sNodePreferenceMap = new HashMap<>();
    static {
        sNodePreferenceMap.put(BUTTONS_SWAP_KEY, BUTTONS_SWAP_NODE);
        sNodePreferenceMap.put(BUTTONS_SLIDER_TOP_KEY, BUTTONS_SLIDER_TOP_NODE);
        sNodePreferenceMap.put(BUTTONS_SLIDER_MIDDLE_KEY, BUTTONS_SLIDER_MIDDLE_NODE);
        sNodePreferenceMap.put(BUTTONS_SLIDER_BOTTOM_KEY, BUTTONS_SLIDER_BOTTOM_NODE);

        sNodePreferenceMap.put(TGESTURES_CAMERA_KEY, TGESTURES_CAMERA_NODE);
        sNodePreferenceMap.put(TGESTURES_FLASHLIGHT_KEY, TGESTURES_FLASHLIGHT_NODE);
        sNodePreferenceMap.put(TGESTURES_MUSIC_KEY, TGESTURES_MUSIC_NODE_1);
    }

    // <PREFERENCE_KEY> -> <DEFAULT_VALUE> mapping
    public static HashMap<String, Object> sNodeDefaultMap = new HashMap<>();
    static {
        sNodeDefaultMap.put(BUTTONS_SWAP_KEY, 0);
        sNodeDefaultMap.put(BUTTONS_SLIDER_TOP_KEY, "601");
        sNodeDefaultMap.put(BUTTONS_SLIDER_MIDDLE_KEY, "602");
        sNodeDefaultMap.put(BUTTONS_SLIDER_BOTTOM_KEY, "603");

        sNodeDefaultMap.put(TGESTURES_CAMERA_KEY, 0);
        sNodeDefaultMap.put(TGESTURES_FLASHLIGHT_KEY, 0);
        sNodeDefaultMap.put(TGESTURES_MUSIC_KEY, 0);
        sNodeDefaultMap.put(TGESTURES_HAPTIC_FEEDBACK_KEY, 1);
    }

    public static ArrayList<String> sButtonKeys = new ArrayList<>();
    static {
        sButtonKeys.add(BUTTONS_SWAP_KEY);
        sButtonKeys.add(BUTTONS_SLIDER_TOP_KEY);
        sButtonKeys.add(BUTTONS_SLIDER_MIDDLE_KEY);
        sButtonKeys.add(BUTTONS_SLIDER_BOTTOM_KEY);
    }

    public static ArrayList<String> sTGestureKeys = new ArrayList<>();
    static {
        sTGestureKeys.add(TGESTURES_CAMERA_KEY);
        sTGestureKeys.add(TGESTURES_FLASHLIGHT_KEY);
        sTGestureKeys.add(TGESTURES_MUSIC_KEY);
        sTGestureKeys.add(TGESTURES_HAPTIC_FEEDBACK_KEY);
    }

    public static Object getPreferenceValue(Context context, String key) {
        if (!key.equals(BUTTONS_SLIDER_TOP_KEY) && !key.equals(BUTTONS_SLIDER_MIDDLE_KEY) && !key.equals(BUTTONS_SLIDER_BOTTOM_KEY)) {
            int value = Settings.System.getInt(context.getContentResolver(), key, -1);
            if (value == -1) {
                value = (int) sNodeDefaultMap.get(key);
            }

            return value == 1;
        } else {
            String value = Settings.System.getString(context.getContentResolver(), key);
            if (value == null) {
                value = (String) sNodeDefaultMap.get(key);
            }

            return value;
        }
    }

    public static void setPreferenceToNode(String key, String value) {
        String node;
        if (!key.equals(TGESTURES_MUSIC_NODE_1) &&
            !key.equals(TGESTURES_MUSIC_NODE_2) &&
            !key.equals(TGESTURES_MUSIC_NODE_3)) {
            node = sNodePreferenceMap.get(key);
        } else {
            node = key;
        }

        if (!writeLine(node, value)) {
            Log.w(TAG, "Write " + value + " to node " + node + " failed");
        }
    }

    public static boolean writeLine(String fileName, String value) {
        BufferedWriter bufferedWriter = null;

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(fileName));
            bufferedWriter.write(value);
        } catch (FileNotFoundException ex) {
            Log.w(TAG, "No such file " + fileName + " for writing");

            return false;
        } catch (IOException ex) {
            Log.e(TAG, "Could not write to file " + fileName, ex);

            return false;
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                // Ignored, not much we can do anyway
            }
        }

        return true;
    }
}
