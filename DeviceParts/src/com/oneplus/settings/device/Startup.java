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

package com.oneplus.settings.device;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.ServiceManager;
import android.util.Log;

import java.io.File;

import com.oneplus.settings.device.utils.Constants;
import com.oneplus.settings.device.utils.FileUtils;

public class Startup extends BroadcastReceiver {

    private static final String TAG = Startup.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_PRE_BOOT_COMPLETED.equals(action)) {
            // Disable touchscreen gesture settings if needed
            if (!hasTouchscreenGestures()) {
                disableComponent(context, TouchscreenGestureSettings.class.getName());
            } else {
                enableComponent(context, TouchscreenGestureSettings.class.getName());
                // Restore nodes to saved preference values
                for (String pref : Constants.sGesturePrefKeys) {
                    boolean value = Constants.isPreferenceEnabled(context, pref);
                    String node = Constants.sBooleanNodePreferenceMap.get(pref);
                    // If music gestures are toggled, update values of all music gesture proc files
                    if (pref.equals(Constants.TOUCHSCREEN_MUSIC_GESTURE_KEY)) {
                        for (String music_nodes : Constants.TOUCHSCREEN_MUSIC_GESTURES_ARRAY) {
                            if (!FileUtils.writeLine(music_nodes, value ? "1" : "0")) {
                                Log.w(TAG, "Write to node " + music_nodes +
                                        " failed while restoring saved preference values");
                            }
                        }
                    } else if (!FileUtils.writeLine(node, value ? "1" : "0")) {
                        Log.w(TAG, "Write to node " + node +
                                " failed while restoring saved preference values");
                    }
                }
            }

            // Disable button settings if needed
            if (!hasButtonProcs()) {
                disableComponent(context, TouchscreenGestureSettings.class.getName());
            } else {
                enableComponent(context, TouchscreenGestureSettings.class.getName());
                // Restore nodes to saved preference values
                for (String pref : Constants.sButtonPrefKeys) {
                    String value;
                    String node;
                    if (Constants.sStringNodePreferenceMap.containsKey(pref)) {
                        value = Constants.getPreferenceString(context, pref);
                        node = Constants.sStringNodePreferenceMap.get(pref);
                    } else {
                        value = Constants.isPreferenceEnabled(context, pref) ?
                                "1" : "0";
                        node = Constants.sBooleanNodePreferenceMap.get(pref);
                    }
                    if (!FileUtils.writeLine(node, value)) {
                        Log.w(TAG, "Write to node " + node +
                                " failed while restoring saved preference values");
                    }
                }
            }
        }
    }

    static boolean hasTouchscreenGestures () {
        return new File(Constants.TOUCHSCREEN_CAMERA_NODE).exists() &&
                new File(Constants.TOUCHSCREEN_DOUBLE_SWIPE_NODE).exists() &&
                new File(Constants.TOUCHSCREEN_FLASHLIGHT_NODE).exists();
    }

    private boolean hasButtonProcs () {
        return (new File(Constants.NOTIF_SLIDER_TOP_NODE).exists() &&
                new File(Constants.NOTIF_SLIDER_MIDDLE_NODE).exists() &&
                new File(Constants.NOTIF_SLIDER_BOTTOM_NODE).exists()) ||
                new File(Constants.BUTTON_SWAP_NODE).exists();
    }

    private void disableComponent(Context context, String component) {
        ComponentName name = new ComponentName(context, component);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(name,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void enableComponent(Context context, String component) {
        ComponentName name = new ComponentName(context, component);
        PackageManager pm = context.getPackageManager();
        if (pm.getComponentEnabledSetting(name)
                == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            pm.setComponentEnabledSetting(name,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
