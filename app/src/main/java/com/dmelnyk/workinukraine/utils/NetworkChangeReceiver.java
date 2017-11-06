package com.dmelnyk.workinukraine.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by d264 on 11/6/17.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    public static final String ACTION_NETWORK_STATE_CHANGED = "NetworkChangeReceiver.ACTION_NETWORK_STATE_CHANGED";
    public static final String EXTRA_NETWORK_IS_AVAILABLE = "NetworkChangeReceiver.EXTRA_NETWORK_IS_AVAILABLE";
    public static final String EXTRA_NETWORK_TYPE = "NetworkChangeReceiver.EXTRA_NETWORK_TYPE";

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;

    public static boolean sConnectionStatus;
    public static int sConnectionType;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        sConnectionStatus = NetUtils.isNetworkReachable(context);
        sConnectionType = NetUtils.getConnectionType(context);

        Log.e(getClass().getSimpleName(), "network is reachable=" + sConnectionStatus);
        Log.e(getClass().getSimpleName(), "connection_type=" + sConnectionType);

        sendBroadcast(context, sConnectionStatus, sConnectionType);
    }

    private void sendBroadcast(Context context, boolean isConnected, int connectionType) {
        Intent localBroadcast = new Intent(ACTION_NETWORK_STATE_CHANGED);
        localBroadcast.putExtra(EXTRA_NETWORK_IS_AVAILABLE, isConnected);
        localBroadcast.putExtra(EXTRA_NETWORK_TYPE, connectionType);

        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(localBroadcast);
    }
}