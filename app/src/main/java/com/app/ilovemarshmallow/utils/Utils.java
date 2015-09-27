package com.app.ilovemarshmallow.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/**
 * Utils.java - This class provides some helpers/utility functions.
 *
 * @author Saurabh Patel
 *         skpatel@syr.edu
 * @version 1.0
 */
public class Utils {

    /**
     * Detect Internet Connection Status
     * @param context The UI Activity Context
     * @return true if connected, false if not connected
     */
    public static boolean isConnectingToInternet(final Context context) {
        final ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetworkInfo = connectivity.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
