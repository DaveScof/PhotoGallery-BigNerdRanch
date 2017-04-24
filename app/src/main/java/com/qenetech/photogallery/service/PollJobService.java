package com.qenetech.photogallery.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
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
 * Created by davescof on 4/24/17.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PollJobService extends JobService {

    private static final String TAG = "PollJobService";

    private Context mContext;
    private AsyncTask mCurrentTask;

    public PollJobService (Context context)
    {
        mContext = context;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mCurrentTask = new PollTask();
        mCurrentTask.execute(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mCurrentTask != null)
        {
            mCurrentTask.cancel(true);
        }
        return true;
    }

    private class PollTask extends AsyncTask <JobParameters, Void, Void> {

        @Override
        protected Void doInBackground(JobParameters... params) {
            JobParameters parameters = params[0];

            String query = QueryPreferences.getStoredQuery(mContext);
            String lastResultId = QueryPreferences.getLastResultId(mContext);

            List<GalleryItem> itemList;
            if (query.equals("")) {
                itemList = new FlickrFetchr().fetchRecentPhotos(0);
            }
            else {
                itemList = new FlickrFetchr().searchPhotos(query, 0);
            }

            if (itemList.size() == 0) {
                Log.i(TAG, "Gallery Items list is empty");
                jobFinished(parameters, true);
                return null;
            }

            String resultId = itemList.get(0).getId();
            if (resultId.equals(lastResultId)) {
                Log.i(TAG, "Got an old result: " + resultId);
            }
            else {
                Log.i(TAG, "Got a new result: " + resultId);
                Intent i = PhotoGalleryActivity.newIntent(mContext);
                PendingIntent pi = PendingIntent.getActivity(mContext, 0, i, 0);
                Resources resources = getResources();

                Notification notification = new NotificationCompat.Builder(mContext)
                        .setTicker(resources.getString(R.string.new_pictures_title))
                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
                        .setContentTitle(resources.getString(R.string.new_pictures_title))
                        .setContentText(resources.getString(R.string.new_pictures_text))
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .build();

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
                notificationManagerCompat.notify(0, notification);
            }

            QueryPreferences.setLastResultId(mContext, resultId);
            jobFinished(parameters, false);
            return null;
        }
    }
}
