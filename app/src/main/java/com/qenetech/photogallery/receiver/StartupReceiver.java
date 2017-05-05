package com.qenetech.photogallery.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.qenetech.photogallery.db.QueryPreferences;
import com.qenetech.photogallery.service.PollService;

/**
 * Created by davescof on 5/5/17.
 */

public class StartupReceiver extends BroadcastReceiver {
        public static final String TAG = StartupReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received Broadcast intent " + intent.getAction());
        boolean isOn = QueryPreferences.isAlarmOn(context);
        PollService.setServiceAlarm(context, isOn);
    }
}
