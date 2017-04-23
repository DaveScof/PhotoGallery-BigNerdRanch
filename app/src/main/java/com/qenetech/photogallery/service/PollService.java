package com.qenetech.photogallery.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.util.Log;

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
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        }
        else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
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
        if (resultId.equals(lastResultId))
            Log.i(TAG, "Got an old result: " + resultId);
        else
            Log.i(TAG, "Got a new result: " + resultId);

        QueryPreferences.setLastResultId(this, resultId);
    }

    private boolean isNetworkAvailableAndConnected (){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();

        return isConnected;
    }

    public boolean isAlarmServiceOn (Context context)
    {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);

        return pi != null;
    }
}
