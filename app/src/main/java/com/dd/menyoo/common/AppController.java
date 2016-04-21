package com.dd.menyoo.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.dd.menyoo.model.GuestRequestModel;
import com.dd.menyoo.model.RestaurantModel;
import com.dd.menyoo.model.UserModel;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 22-Feb-16.
 */


public class AppController extends Application {

    private static AppController mInstance;
    private static UserModel loginUser;
    private static RestaurantModel currentRestaurent;
    private static String TableId;
    private static int billId;
    private static SharedPrefManager sharedPrefManager;
    private static Activity mCurrentActivity = null;
    private static Context mContext;
    private static boolean isHost;
    private static GuestRequestModel currentGuest;
    private static volatile boolean activityVisible;
    private static boolean isTableOccupiedInAnyRes;
    private static boolean isAppDisable;
    private static boolean isFirstTimeOrderAdded=false;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mInstance = this;
        mContext = getApplicationContext();
        MultiDex.install(this);
        sharedPrefManager = new SharedPrefManager(this);
    }

    public static Resources getCustResources() {
        return mContext.getResources();
    }

    public static Context getContext(){
        return mContext;
    }

    public static Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public  void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static UserModel getLoginUser() {
        if(loginUser!=null)
            return loginUser;
        else{
            return  sharedPrefManager.checkIfUserIsLoggedIn();
        }
    }

    public static void setLoginUser(UserModel loginUser)
    {
        AppController.loginUser = loginUser;
    }

    public static RestaurantModel getCurrentRestaurent() {
        return currentRestaurent;
    }

    public static void setCurrentRestaurent(RestaurantModel currentRestaurent) {
        AppController.currentRestaurent = currentRestaurent;
    }

    public static String getTableId() {
        return TableId;
    }

    public static void setTableId(String tableId) {
        TableId = tableId;
    }

    public static Integer getBillId() {
        return billId;
    }

    public static void setBillId(int billId) {
        AppController.billId = billId;
    }

   /* public static boolean isJoinTableAsGuest() {
        return isJoinTableAsGuest;
    }

    public static void setIsJoinTableAsGuest(boolean isJoinTableAsGuest) {
        AppController.isJoinTableAsGuest = isJoinTableAsGuest;
    }*/

    public static boolean isHost() {
        return isHost;
    }

    public static void setIsHost(boolean isHost) {
        AppController.isHost = isHost;
    }

    public static GuestRequestModel getCurrentGuest() {
        return currentGuest;
    }

    public static void setCurrentGuest(GuestRequestModel currentGuest) {
        AppController.currentGuest = currentGuest;
    }

    public static boolean isTableOccupiedInAnyRes() {
        return isTableOccupiedInAnyRes;
    }

    public static void setIsTableOccupiedInAnyRes(boolean isTableOccupiedInAnyRes) {
        AppController.isTableOccupiedInAnyRes = isTableOccupiedInAnyRes;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    public static boolean isAppDisable() {
        return isAppDisable;
    }

    public static void setIsAppDisable(boolean isAppDisable) {
        AppController.isAppDisable = isAppDisable;
    }

    public static boolean isFirstTimeOrderAdded() {
        return isFirstTimeOrderAdded;
    }

    public static void setIsFirstTimeOrderAdded(boolean isFirstTimeOrderAdded) {
        AppController.isFirstTimeOrderAdded = isFirstTimeOrderAdded;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
