package com.qenetech.photogallery.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.qenetech.photogallery.service.PollService;

/**
 * Created by davescof on 5/6/17.
 */

public abstract class VisibleFragment extends Fragment {
    private static final String TAG = VisibleFragment.class.getSimpleName();

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mOnShowNotification,filter, PollService.PERM_PRIVATE, null);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mOnShowNotification);
    }

    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // If we receive this, we're visible so cancel the notification
            Log.i(TAG, "Cancel notification");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

}
