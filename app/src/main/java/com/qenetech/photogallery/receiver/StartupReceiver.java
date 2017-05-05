package com.qenetech.photogallery.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by davescof on 5/5/17.
 */

public class StartupReceiver extends BroadcastReceiver {
        public static final String TAG = StartupReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received Broadcast intent " + intent.getAction());
    }
}
