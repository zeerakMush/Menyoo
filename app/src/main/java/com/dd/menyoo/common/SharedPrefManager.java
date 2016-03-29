package com.dd.menyoo.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.dd.menyoo.R;
import com.dd.menyoo.model.UserModel;
import com.google.gson.Gson;

/**
 * Created by Administrator on 22-Feb-16.
 */
public class SharedPrefManager {
    SharedPreferences sharedpreferences;
    Context contxt;

    public SharedPrefManager(Context cont) {
        contxt = cont;
        sharedpreferences = contxt.getSharedPreferences("MENYOO", Context.MODE_PRIVATE);

    }
    public void setSproutAuth(String CokeAuth) {
        setValueForKey(CokeAuth, "MenyooAuth");
    }

    public String getSproutAuth() {
        return getStringByKey("MenyooAuth");
    }

    public void setValueForKey(String val, String key) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, val);
        editor.commit();
    }

    public String getStringByKey(String key) {
        if (sharedpreferences.contains(key)) {
            return sharedpreferences.getString(key, "");
        }

        return null;
    }
    public void SetUserIsLoggedIn(UserModel userToSave) {
        setBooleanForKey(
                true,"isUserLogin");
        SaveUserInSharedPref(userToSave);
    }

    private void SaveUserInSharedPref(UserModel userToSave) {
        Gson gson = new Gson();
        String userJson = gson.toJson(userToSave);
        setValueForKey(
                userJson,"LoginUser");

    }

    public Boolean getBooleanByKey(String key) {
        if (sharedpreferences.contains(key)) {
            return sharedpreferences.getBoolean(key, false);

        }
        return false;
    }

    public UserModel checkIfUserIsLoggedIn() {
        if (getBooleanByKey("isUserLogin")){
            return checkLoggedInUserInSession();
        } else
            return null;
    }

    private UserModel checkLoggedInUserInSession() {
        Gson gson = new Gson();
        String userJson = getStringByKey("LoginUser");
        if (!userJson.equals("")) {
            UserModel userModel = gson
                    .fromJson(userJson, UserModel.class);
            return userModel;
        } else
            return null;
    }

    public void setBooleanForKey(Boolean val, String key) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, val);
        editor.commit();
    }

    public long getLongbyKey(String key){
        if (sharedpreferences.contains(key)) {
            return sharedpreferences.getLong(key, 1);

        }
        return 0L;
    }

    public void setLongForKey(long val,String key){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong(key, val);
        editor.commit();
    }

    public int getIntByKey(String key){
        if (sharedpreferences.contains(key)) {
            return sharedpreferences.getInt(key, 1);

        }
        return -1;
    }

    public void setIntForKey(int val,String key){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(key, val);
        editor.commit();
    }
}
