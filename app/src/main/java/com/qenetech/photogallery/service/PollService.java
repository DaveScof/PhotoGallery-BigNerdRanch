package com.qenetech.photogallery.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
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

    public static Intent newIntent (Context context){
        Intent intent = new Intent(context, PollService.class);
        return intent;
    }

    public static void setServiceAlarm (Context context, boolean isOn){
        
    }
    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected())
            return;

        String query = QueryPreferences.getStoredQuery(this);
        String lastResultId = QueryPreferences.getLastResultId(this);

        List<GalleryItem> itemList;
        if (query.equals("")) {
            itemList = new FlickrFetchr().fetchRecentPhotos(0);
        }
        else {
            itemList = new FlickrFetchr().searchPhotos(query, 0);
        }

        if (itemList.size() == 0)
            return;

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
}
