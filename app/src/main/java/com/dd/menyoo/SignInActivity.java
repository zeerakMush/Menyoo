package com.dd.menyoo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class SignInActivity extends BaseClass {
    EditText mEmailAddress, mPassword;
    Button mSignIn;
    SharedPrefManager sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initView();
    }

    public void initView() {
        mEmailAddress = (EditText) findViewById(R.id.et_emailAddress);
        mPassword = (EditText) findViewById(R.id.et_password);
        mPassword.setTransformationMethod(new PasswordTransformationMethod());
        mSignIn = (Button) findViewById(R.id.btn_signIn);
        sharedPreferences = new SharedPrefManager(this);

    }

    public void signIn(View view) {
        if (AppHelper.isEmailValid(mEmailAddress.getText().toString())) {
            if (AppHelper.isPasswordValid(mPassword.getText().toString()))
                signInOldMethode();
            else {
                Toast.makeText(this, "Password is invalid", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Email Address is invalid", Toast.LENGTH_LONG).show();
        }
    }

    public void signInOldMethode() {
        AppHelper.getInstance().showProgressDialog("Please Wait", SignInActivity.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>(
                2);
        nameValuePairs.add(new BasicNameValuePair("email",
                mEmailAddress.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("pass",
                mPassword.getText().toString()));

        NetworkManagerOld httpMan = new NetworkManagerOld(this,
                NetworkManagerOld.EnCallType.POSTFORLOGIN, NetworkManagerOld.ReturnTypeForReponse.JSON,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                AppHelper.getInstance().hideProgressDialog();
                AppHelper.showConnectionAlert(SignInActivity.this);
                // TODO Auto-generated method stub
            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                Log.e("Menyoo", obj.toString());
                afterUserLogin(obj);
            }
        });

        String[] params = {this.getString(R.string.url_offline)
                + "EmailLogin"};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    public void afterUserLogin(Object object) {
        UserModel user;
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            AppHelper.getInstance().hideProgressDialog();
            if (!jsonObject.has("Status")) {
                int id = jsonObject.getInt("Id");
                boolean isVerificationRequired = jsonObject.getBoolean("VericationRequired");
                String firstName = jsonObject.getString("FirstName");
                String lastName = jsonObject.getString("LastName");
                String email = jsonObject.getString("Email");
                String phoneNumber = jsonObject.getString("PhoneNumber");
                String displayPicture = jsonObject.getString("DisplayPictureFile");
                /*int loyalityStamps = jsonObject.getInt("LoyalityStamps");*/
                user = new UserModel(id, firstName, lastName, email, phoneNumber,
                        displayPicture, isVerificationRequired/*, loyalityStamps*/);
                AppController.setLoginUser(user);
                sharedPreferences.SetUserIsLoggedIn(user);
                if (!isVerificationRequired)
                    showRestaurantActivity();
                else
                    startActivity(new Intent(this, VerifyAccount.class));
            } else {

                String msg = jsonObject.getString("Message");
                Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_LONG).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showRestaurantActivity() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        Intent intentAct = new Intent(SignInActivity.this,
                MenuActivity.class);
        intentAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentAct);
        finish();
    }

    public void guestLogin(View view) {
        int id = 0;
        String firstName = "Guest";
        String lastName = "";
        String email = "";
        String phoneNumber = "";
        String displayPicture = "";
        boolean isVerificationRequired = false;
        UserModel user = new UserModel(id, firstName, lastName, email, phoneNumber, displayPicture, isVerificationRequired);
        user.setGuest(true);
        AppController.setLoginUser(user);
        sharedPreferences.SetUserIsLoggedIn(user);
        showRestaurantActivity();
    }

    public void toForgetPwd(View view) {
        startActivity(new Intent(this, ForgetPassword.class));

    }
}
