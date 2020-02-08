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

import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.session.MediaSessionLegacyHelper;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.os.DeviceKeyHandler;
import com.android.internal.util.ArrayUtils;

public class KeyHandler implements DeviceKeyHandler {
    private String TAG = "OnePlusParts-KeyHandler";

    private int GESTURE_REQUEST = 1;

    private String ACTION_DISMISS_KEYGUARD = "com.android.keyguard.action.DISMISS_KEYGUARD_SECURELY";

    private String GESTURE_WAKEUP_REASON = "keyhandler-gesture-wakeup";
    private int GESTURE_WAKELOCK_DURATION = 3000;

    private Context mContext;
    private boolean mProximityCheckOnWake;
    private int mProximityCheckTimeout;
    private AudioManager mAudioManager;
    private CameraManager mCameraManager;
    private NotificationManager mNotificationManager;
    private PowerManager mPowerManager;
    private SensorManager mSensorManager;
    private Vibrator mVibrator;
    private EventHandler mEventHandler;
    private WakeLock mGestureWakeLock;
    private Sensor mProximitySensor;
    private WakeLock mProximityWakeLock;
    private KeyguardManager mKeyguardManager;
    private String mRearCameraId;
    private boolean mTorchEnabled;

    public KeyHandler(Context context) {
        mContext = context;

        Resources resources = mContext.getResources();
        mProximityCheckOnWake = resources.getBoolean(com.android.internal.R.bool.config_proximityCheckOnWake);
        mProximityCheckTimeout = resources.getInteger(com.android.internal.R.integer.config_proximityCheckTimeout);

        mAudioManager = mContext.getSystemService(AudioManager.class);
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        mNotificationManager = mContext.getSystemService(NotificationManager.class);
        mPowerManager = mContext.getSystemService(PowerManager.class);
        mSensorManager = mProximityCheckOnWake ? mContext.getSystemService(SensorManager.class) : null;
        mVibrator = mContext.getSystemService(Vibrator.class);
        if (mVibrator == null || !mVibrator.hasVibrator()) {
            mVibrator = null;
        }

        mEventHandler = new EventHandler();
        mCameraManager.registerTorchCallback(new MyTorchCallback(), mEventHandler);

        mGestureWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GestureWakeLock");

        if (mProximityCheckOnWake) {
            mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            mProximityWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ProximityWakeLock");
        }
    }

    private void ensureKeyguardManager() {
        if (mKeyguardManager == null) {
            mKeyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        }
    }

    private class MyTorchCallback extends CameraManager.TorchCallback {

        @Override
        public void onTorchModeChanged(String cameraId, boolean enabled) {
            if (!cameraId.equals(mRearCameraId)) {
                return;
            }

            mTorchEnabled = enabled;
        }

        @Override
        public void onTorchModeUnavailable(String cameraId) {
            if (!cameraId.equals(mRearCameraId)) {
                return;
            }

            mTorchEnabled = false;
        }
    }

    private String getRearCameraId() {
        if (mRearCameraId == null) {
            try {
                for (String cameraId : mCameraManager.getCameraIdList()) {
                    CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
                    int lensFacing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                    if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                        mRearCameraId = cameraId;
                        break;
                    }
                }
            } catch (CameraAccessException e) {}
        }

        return mRearCameraId;
    }

    private class EventHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
            switch (message.arg1) {
                case Utils.KEY_DOUBLE_TAP:
                case Utils.KEY_GESTURE_CIRCLE:
                    ensureKeyguardManager();
                    mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);

                    String action;
                    if (mKeyguardManager.isKeyguardSecure() && mKeyguardManager.isKeyguardLocked()) {
                        action = MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE;
                    } else {
                        mContext.sendBroadcastAsUser(new Intent(ACTION_DISMISS_KEYGUARD), UserHandle.CURRENT);
                        action = MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA;
                    }

                    mPowerManager.wakeUp(SystemClock.uptimeMillis(), GESTURE_WAKEUP_REASON);
                    Intent intent = new Intent(action, null);
                    startActivitySafely(intent);

                    doHapticFeedback();
                    break;
                case Utils.KEY_GESTURE_TWO_SWIPE:
                    dispatchMediaKeyWithWakeLockToMediaSession(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);

                    doHapticFeedback();
                    break;
                case Utils.KEY_GESTURE_DOWN_ARROW:
                    String rearCameraId = getRearCameraId();

                    if (rearCameraId != null) {
                        mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);

                        try {
                            mCameraManager.setTorchMode(rearCameraId, !mTorchEnabled);
                            mTorchEnabled = !mTorchEnabled;
                        } catch (CameraAccessException ex) {}

                        doHapticFeedback();
                    }
                    break;
                case Utils.KEY_GESTURE_LEFT_V:
                    dispatchMediaKeyWithWakeLockToMediaSession(KeyEvent.KEYCODE_MEDIA_PREVIOUS);

                    doHapticFeedback();
                    break;
                case Utils.KEY_GESTURE_RIGHT_V:
                    dispatchMediaKeyWithWakeLockToMediaSession(KeyEvent.KEYCODE_MEDIA_NEXT);

                    doHapticFeedback();
                    break;
            }
        }
    }

    public KeyEvent handleKeyEvent(KeyEvent event) {
        int scanCode = event.getScanCode();

        boolean isKeySupported = ArrayUtils.contains(Utils.sSupportedTGestures, scanCode);
        boolean isSliderModeSupported = Utils.sSupportedSliderModes.indexOfKey(scanCode) >= 0;
        if (!isKeySupported && !isSliderModeSupported) {
            return event;
        }

        // We only want ACTION_UP event, except KEY_DOUBLE_TAP
        if (scanCode == Utils.KEY_DOUBLE_TAP) {
            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                return null;
            }
        } else if (event.getAction() != KeyEvent.ACTION_UP) {
            return null;
        }

        if (isSliderModeSupported) {
            if (scanCode == Utils.KEY_SLIDER_MODE_VIBRATION) {
                mNotificationManager.setZenMode(Settings.Global.ZEN_MODE_OFF, null, TAG);
                mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_VIBRATE);
            } else {
                mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
                mNotificationManager.setZenMode(Utils.sSupportedSliderModes.get(scanCode), null, TAG);
            }

            doHapticFeedback();
        } else if (!mEventHandler.hasMessages(GESTURE_REQUEST)) {
            Message message = getMessageForKeyEvent(scanCode);
            boolean defaultProximity = mContext.getResources().getBoolean(com.android.internal.R.bool.config_proximityCheckOnWakeEnabledByDefault);
            boolean proximityWakeCheckEnabled = Settings.System.getInt(mContext.getContentResolver(), Settings.System.PROXIMITY_ON_WAKE, defaultProximity ? 1 : 0) == 1;

            if (mProximitySensor != null && proximityWakeCheckEnabled && mProximityCheckOnWake) {
                mEventHandler.sendMessageDelayed(message, mProximityCheckTimeout);

                processEvent(scanCode);
            } else {
                mEventHandler.sendMessage(message);
            }
        }

        return null;
    }

    private Message getMessageForKeyEvent(int scancode) {
        Message message = mEventHandler.obtainMessage(GESTURE_REQUEST);
        message.arg1 = scancode;

        return message;
    }

    private void processEvent(final int scancode) {
        mProximityWakeLock.acquire();

        mSensorManager.registerListener(new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                mProximityWakeLock.release();
                mSensorManager.unregisterListener(this);

                if (!mEventHandler.hasMessages(GESTURE_REQUEST)) {
                    return; // The sensor took too long to respond, ignoring.
                }

                mEventHandler.removeMessages(GESTURE_REQUEST);
                if (event.values[0] == mProximitySensor.getMaximumRange()) {
                    Message message = getMessageForKeyEvent(scancode);

                    mEventHandler.sendMessage(message);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        }, mProximitySensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void dispatchMediaKeyWithWakeLockToMediaSession(int keycode) {
        MediaSessionLegacyHelper mediaSessionLegacyHelper = MediaSessionLegacyHelper.getHelper(mContext);

        if (mediaSessionLegacyHelper != null) {
            KeyEvent keyEvent = new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN, keycode, 0);
            mediaSessionLegacyHelper.sendMediaButtonEvent(keyEvent, true);

            keyEvent = KeyEvent.changeAction(keyEvent, KeyEvent.ACTION_UP);
            mediaSessionLegacyHelper.sendMediaButtonEvent(keyEvent, true);
        } else {
            Log.w(TAG, "Unable to send media key event");
        }
    }

    private void startActivitySafely(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            UserHandle userHandle = new UserHandle(UserHandle.USER_CURRENT);
            mContext.startActivityAsUser(intent, null, userHandle);
        } catch (ActivityNotFoundException e) {}
    }

    private void doHapticFeedback() {
        if (mVibrator == null) {
            return;
        }

        boolean enabled = (boolean) Utils.getPreferenceValue(mContext.getApplicationContext(), Utils.TGESTURES_HAPTIC_FEEDBACK_KEY);
        if (enabled) {
            mVibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }
}
