/**
 * ****************************************************************
 *
 * Copyright (C) SNDA Corporation. All rights reserved.
 *
 * FileName : BroadCastReceiver.java
 * Description : Receive broadcasts.
 *
 ******************************************************************
 */
package snda.in.ttsdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * A class that receives broadcast when TTS initialization finished. When TTS service is starting,
 * it will take several seconds to be ready to speak, during this time period, calling of speak
 * interface will cause unexpected result. You could use the same logic as the way showing by this
 * demo, to forbid the call of speak interface in unavailable time window.
 */
public class BroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = "SmartCar_2.0";
    private final String INSTALL_OK = "com.snda.message.installok";
    private final String INSTALL_FAIL = "com.snda.message.installfail";
    private final String PREFERENCE_NAME = "snda.in.ttsdemo";
    private final String SPEAK_READY = "SPEAK_READY";
    private SharedPreferences mPrefer = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        mPrefer = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

        if (intent.getAction().equals(INSTALL_OK)) {
            Log.i(TAG, "ttsservice install ok");
            Editor editor = mPrefer.edit();
            editor.putBoolean(SPEAK_READY, true);
            editor.commit();
        } else if (intent.getAction().equals(INSTALL_FAIL)) {
            Log.i(TAG, "ttsservice install fail");
            Editor editor = mPrefer.edit();
            editor.putBoolean(SPEAK_READY, false);
            editor.commit();
        }
    }
}
