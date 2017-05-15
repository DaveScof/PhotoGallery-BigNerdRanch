package com.qenetech.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Created by davescof on 5/16/17.
 */

public class PhotoPageActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context, Uri photoPageUri) {
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(photoPageUri);
        return i;
    }

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }

    @Override
    public Fragment createFragment() {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for(Fragment fragment : fragmentList) {
            if (fragment instanceof OnBackPressedListener) {
                if (!((OnBackPressedListener) fragment).onBackPressed()) {
                    getFragmentManager().popBackStack();
                    super.onBackPressed();
                }
            }
        }
    }
}
