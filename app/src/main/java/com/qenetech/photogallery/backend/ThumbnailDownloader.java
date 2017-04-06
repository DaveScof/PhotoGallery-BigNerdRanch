package com.qenetech.photogallery.backend;

import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by davescof on 4/6/17.
 */

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";

    public ThumbnailDownloader() {
        super(TAG);
    }

    public void queueThumbnail (T target, String url){
        Log.i(TAG, "Got URL " + url);
    }
}
