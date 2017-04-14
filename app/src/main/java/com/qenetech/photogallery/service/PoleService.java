package com.qenetech.photogallery.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by davescof on 4/14/17.
 */

public class PoleService extends IntentService {
    private static final String TAG = "PoleService";

    public static Intent newIntent (Context context){
        Intent intent = new Intent(context, PoleService.class);
        return intent;
    }

    public PoleService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected())
            return;
        Log.i(TAG, "Got Intent " +  intent);
    }

    private boolean isNetworkAvailableAndConnected (){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();

        return isConnected;
    }
}
