package com.dd.menyoo.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.dd.menyoo.TabActivity;
import com.dd.menyoo.common.AppController;

/**
 * Created by Administrator on 16-Mar-16.
 */
public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        //baseActivity.showUploadFailedError();
        if (netInfo != null &&
                (netInfo.getType() == ConnectivityManager.TYPE_WIFI || netInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                && AppController.isActivityVisible() &&
                AppController.getCurrentActivity() instanceof TabActivity){
            ((TabActivity)AppController.getCurrentActivity()).registerToRestrauant();
        }else{
            SignalRManager.getInstance().forceDisconnect();
        }

    }
}
