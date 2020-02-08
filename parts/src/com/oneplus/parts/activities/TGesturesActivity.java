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

package com.oneplus.parts.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.oneplus.parts.Utils;
import com.oneplus.parts.fragments.TGesturesFragment;

import java.io.File;

public class TGesturesActivity extends PreferenceActivity {
    public String TAG = "OnePlusParts-TGesturesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (!Utils.TGESTURES_BOOTED) {
                Log.i(TAG, "Setting/writing preference values to nodes");

                if (hasGestures()) {
                    enableComponent(getApplicationContext(), TGesturesActivity.class.getName());

                    for (String key : Utils.sTGestureKeys) {
                        if (!key.equals(Utils.TGESTURES_HAPTIC_FEEDBACK_KEY)) { // Node for TGESTURES_HAPTIC_FEEDBACK_KEY does not exist
                            Object objectValue = Utils.getPreferenceValue(getApplicationContext(), key);

                            if (objectValue instanceof Boolean) {
                                int value = (boolean) objectValue? 1 : 0;

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
                    disableComponent(getApplicationContext(), TGesturesActivity.class.getName());
                }

                Utils.TGESTURES_BOOTED = true;
            }

            getFragmentManager().beginTransaction().replace(android.R.id.content, new TGesturesFragment()).commit();
        }
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
