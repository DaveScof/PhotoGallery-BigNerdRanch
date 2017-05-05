package com.qenetech.photogallery.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.qenetech.photogallery.PhotoGalleryActivity;
import com.qenetech.photogallery.R;
import com.qenetech.photogallery.backend.FlickrFetchr;
import com.qenetech.photogallery.db.QueryPreferences;
import com.qenetech.photogallery.model.GalleryItem;

import java.util.List;

/**
 * Created by davescof on 4/14/17.
 */

public class PollService extends IntentService {
    private static final String TAG = "PollService";
    private static final int POLL_INTERVAL = 1000 * 60;

    public static Intent newIntent (Context context){
        Intent intent = new Intent(context, PollService.class);
        return intent;
    }

    public static void setServiceAlarm (Context context, boolean isOn){
        Intent i = newIntent(context);
        PendingIntent pi = PendingIntent.getService(context,0,i,0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (isOn) {
            Log.i(TAG, "Alarm set on PendingIntent " + pi + ". With a Poll Interval of " + POLL_INTERVAL);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        }
        else {
            Log.i(TAG, "Alarm is canceled!");
            alarmManager.cancel(pi);
            pi.cancel();
        }

        QueryPreferences.setAlarmOn(context, isOn);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            Log.i(TAG, "Connection not available");
            return;
        }

        String query = QueryPreferences.getStoredQuery(this);
        String lastResultId = QueryPreferences.getLastResultId(this);

        List<GalleryItem> itemList;
        if (query.equals("")) {
            itemList = new FlickrFetchr().fetchRecentPhotos(0);
        }
        else {
            itemList = new FlickrFetchr().searchPhotos(query, 0);
        }

        if (itemList.size() == 0) {
            Log.i(TAG, "Gallery Items list is empty");
            return;
        }

        String resultId = itemList.get(0).getId();
        if (resultId.equals(lastResultId)) {
            Log.i(TAG, "Got an old result: " + resultId);
        }
        else {
            Log.i(TAG, "Got a new result: " + resultId);
            Intent i = PhotoGalleryActivity.newIntent(this);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
            Resources resources = getResources();

            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(0, notification);
        }
        QueryPreferences.setLastResultId(this, resultId);
    }

    private boolean isNetworkAvailableAndConnected (){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();

        return isConnected;
    }

    public static boolean isAlarmServiceOn (Context context)
    {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);

        return pi != null;
    }
}
