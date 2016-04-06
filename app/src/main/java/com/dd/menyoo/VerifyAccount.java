package com.dd.menyoo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.common.SharedPrefManager;
import com.dd.menyoo.gcm.RegistrationIntentService;
import com.dd.menyoo.model.UserModel;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VerifyAccount extends AppCompatActivity {
    EditText etVerification;
    int userID = 0;
    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);
        etVerification = (EditText) findViewById(R.id.et_verification);
        sharedPrefManager = new SharedPrefManager(this);
        Bundle bundle = getIntent().getExtras();


        if (bundle != null) {
            userID = bundle.getInt("UserID");
        }

    }

    private void verifyAccount(int userID, String verficationCode) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("UserID", "" + userID));
        nameValuePairs.add(new BasicNameValuePair("verificationCode", verficationCode));
        NetworkManagerOld httpMan = new NetworkManagerOld(this,
                NetworkManagerOld.EnCallType.POSTFORALL,
                NetworkManagerOld.ReturnTypeForReponse.String, nameValuePairs,
                null, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                // TODO Auto-generated method stub
                AppHelper.getInstance().hideProgressDialog();
            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                Log.e("MEnyoo", obj.toString());
                afterUserLogin(obj);
            }
        }, true);

        String[] params = {this.getString(R.string.url_offline) + "VerifiEmail"};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    public void verifyEmail(View view) {
        if(!etVerification.getText().toString().trim().isEmpty()){
            verifyAccount(userID, etVerification.getText().toString());
            AppHelper.getInstance().showProgressDialog("Verifying ...", this);
        }else{
            Toast.makeText(this,"Verification code can not be empty",Toast.LENGTH_LONG).show();
        }

    }

    public void afterUserLogin(Object object) {
        UserModel user;
        try {
            AppHelper.getInstance().hideProgressDialog();
            JSONObject jsonObj = new JSONObject(object.toString());
            String state = "";
            if (jsonObj.has("state"))
                state = jsonObj.getString("state");
            if(state.equals("error")){
                Toast.makeText(this,jsonObj.getString("Message"),Toast.LENGTH_LONG).show();
            }else{
                JSONObject jsonObject = new JSONObject(object.toString());
                AppHelper.getInstance().hideProgressDialog();
                int id = jsonObject.getInt("Id");
                boolean isVerificationRequired = jsonObject.getBoolean("VericationRequired");
                String firstName = jsonObject.getString("FirstName");
                String lastName = jsonObject.getString("LastName");
                String email = jsonObject.getString("Email");
                String phoneNumber = jsonObject.getString("PhoneNumber");
                String displayPicture = jsonObject.getString("DisplayPictureFile");
                user = new UserModel(id, firstName, lastName, email, phoneNumber, displayPicture, isVerificationRequired);
                AppController.setLoginUser(user);
                sharedPrefManager.SetUserIsLoggedIn(user);
                if(!isVerificationRequired)
                    showRestaurantActivity();
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showRestaurantActivity() {
        AppHelper.getInstance().hideProgressDialog();
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        Intent intentAct = new Intent(this,
                MenuActivity.class);
        intentAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentAct);
        finish();
    }
}
