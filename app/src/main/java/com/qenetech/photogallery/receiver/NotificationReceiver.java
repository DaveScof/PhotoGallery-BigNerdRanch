package com.qenetech.photogallery.receiver;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.qenetech.photogallery.service.PollService;

/**
 * Created by davescof on 5/6/17.
 */

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = NotificationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received result:" + getResultCode());
        if (getResultCode() != Activity.RESULT_OK){
            // A foreground activity cancelled the broadcast
            return;
        }

        int requestCode = intent.getIntExtra(PollService.REQUEST_CODE, 0);
        Notification notification = intent.getParcelableExtra(PollService.NOTIFICATION);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(requestCode, notification);
    }
}
