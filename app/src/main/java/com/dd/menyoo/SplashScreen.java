package com.dd.menyoo;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SplashScreen extends AppCompatActivity {
    ImageView ivSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ivSplash = (ImageView)findViewById(R.id.iv_splashimage);
        PackageInfo pInfo = null;
        String version="1.0";

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            Log.e("VErsion Number",version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        checkUpdate(version);
    }

    protected void checkUpdate(String versionNumber) {
        List<NameValuePair> nameValuePairs = new ArrayList<>(
                2);
        NetworkManagerOld httpMan = new NetworkManagerOld(this,
                NetworkManagerOld.EnCallType.POSTFORALL, NetworkManagerOld.ReturnTypeForReponse.String,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                // TODO Auto-generated method stub
                Log.e("Menyoo", "Some Error");
                AppHelper.showConnectionAlert(getApplicationContext());

                // / afterCatlogUpdate(null);
            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                Log.e("Menyoo", obj.toString());
                afterCheckForUpdate(obj);
            }
        });
        String url = String.format("UpdateToLatestVersion?version=%s",versionNumber);
        String[] params = {getString(R.string.url_offline) + url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    public void afterCheckForUpdate(Object obj){
        try {
            JSONObject jsonObject = new JSONObject(obj.toString());
            if(jsonObject.get("state").equals("Success")){
                boolean isUpdateRequired = jsonObject.getBoolean("isUpdateRequired");
                if(!isUpdateRequired)
                    toLoginActivity();
                else{
                    ivSplash.setVisibility(View.GONE);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toLoginActivity() {
        Intent intentAct = new Intent(SplashScreen.this,
                LoginActivity.class);
        intentAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentAct);
        finish();
    }


    public void toPlayStore(View view) {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}
