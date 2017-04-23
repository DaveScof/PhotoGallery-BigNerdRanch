package com.qenetech.photogallery.db;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by davescof on 4/7/17.
 */

public class QueryPreferences {

    private static final String PREF_SEARCH_QUERY = "SearchQuery";
    private static final String PREF_LAST_RESULT_ID = "LastResultId";

    public static String getStoredQuery (Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY, "");
    }

    public static void setStoredQuery (Context context, String query){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY, query)
                .apply();
    }

    public static String getLastResultId (Context contex) {
        return PreferenceManager.getDefaultSharedPreferences(contex)
                .getString(PREF_LAST_RESULT_ID, null);
    }

    public static void setLastResultId (Context context, String lastResultId){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_RESULT_ID, lastResultId)
                .apply();
    }
}
