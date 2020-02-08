/*
 * Copyright (c) 2015 The CyanogenMod Project
 * Copyright (c) 2017 The LineageOS Project
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

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.oneplus.parts.activities.ButtonsActivity;
import com.oneplus.parts.activities.TGesturesActivity;

import java.io.File;

public class BootCompletedReceiver extends BroadcastReceiver {
    public String TAG = "OnePlusParts-BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || Intent.ACTION_PRE_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.i(TAG, "Setting/writing preference values to nodes");

            if (!Utils.BUTTONS_BOOTED) {
                if (hasButtons()) {
                    enableComponent(context, ButtonsActivity.class.getName());

                    for (String key : Utils.sButtonKeys) {
                        Object objectValue = Utils.getPreferenceValue(context, key);

                        if (objectValue instanceof Boolean) {
                            int value = (boolean) objectValue ? 1 : 0;

                            Utils.setPreferenceToNode(key, Integer.toString(value));
                        } else if (objectValue instanceof String) {
                            String value = (String) objectValue;

                            Utils.setPreferenceToNode(key, value);
                        }
                    }
                } else {
                    disableComponent(context, ButtonsActivity.class.getName());
                }

                Utils.BUTTONS_BOOTED = true;
            }

            if (!Utils.TGESTURES_BOOTED) {
                if (hasGestures()) {
                    enableComponent(context, TGesturesActivity.class.getName());

                    for (String key : Utils.sTGestureKeys) {
                        if (!key.equals(Utils.TGESTURES_HAPTIC_FEEDBACK_KEY)) { // Node for TGESTURES_HAPTIC_FEEDBACK_KEY does not exist
                            Object objectValue = Utils.getPreferenceValue(context, key);

                            if (objectValue instanceof Boolean) {
                                int value = (boolean) objectValue ? 1 : 0;

                                if (!key.equals(Utils.TGESTURES_MUSIC_KEY)) {
                                    Utils.setPreferenceToNode(key, Integer.toString(value));
                                } else {
                                    for (String node : Utils.TGESTURES_MUSIC_NODES) {
                                        Utils.setPreferenceToNode(node, Integer.toString(value));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    disableComponent(context, TGesturesActivity.class.getName());
                }

                Utils.TGESTURES_BOOTED = true;
            }
        }
    }

    private boolean hasButtons () {
        return new File(Utils.BUTTONS_SWAP_NODE).exists() ||
              (new File(Utils.BUTTONS_SLIDER_TOP_NODE).exists() &&
               new File(Utils.BUTTONS_SLIDER_MIDDLE_NODE).exists() &&
               new File(Utils.BUTTONS_SLIDER_BOTTOM_NODE).exists());
    }

    private boolean hasGestures () {
        return new File(Utils.TGESTURES_CAMERA_NODE).exists() &&
               new File(Utils.TGESTURES_FLASHLIGHT_NODE).exists() &&
               new File(Utils.TGESTURES_MUSIC_NODE_1).exists();
    }

    private void disableComponent(Context context, String component) {
        ComponentName componentName = new ComponentName(context, component);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    private void enableComponent(Context context, String component) {
        ComponentName componentName = new ComponentName(context, component);
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.getComponentEnabledSetting(componentName) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }
}
