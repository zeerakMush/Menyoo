package com.dd.menyoo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.model.UserModel;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends BaseClass {

    EditText mFirstName,mLastName,mEmailAddress,mPhoneNumber,mPassword1,mPAssword2;
    Toast toast;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initView();

    }

    public void initView(){
        mFirstName = (EditText)findViewById(R.id.et_first_name);
        mLastName = (EditText)findViewById(R.id.et_last_name);
        mEmailAddress = (EditText)findViewById(R.id.et_emailAddress);
        mPhoneNumber = (EditText)findViewById(R.id.et_phoneNumber);
        mPassword1 = (EditText)findViewById(R.id.et_password_1);
        mPAssword2 = (EditText)findViewById(R.id.et_password_2);
        mPhoneNumber.setHint(Html.fromHtml("<font size=\"16\">" +
                "Phone Number*      " + "</font>" + "<small>" +
                "(e.g 0123456789)" + "</small>" ));
        mPassword1.setHint(Html.fromHtml("<font size=\"16\">" +
                "Password*     " + "</font>" + "<small>" +
                "(min 6 characters)" + "</small>" ));

        toast = Toast.makeText(this, "message", Toast.LENGTH_SHORT);

    }

    public void register(View view) {
        registerUser();
    }

    public void registerUser(){
        if(isFieldFill()){
            if(validateField()){
                AppHelper.getInstance().showProgressDialog("Please wait",this);
                List<NameValuePair> nameValuePairs = new ArrayList<>(
                        2);
                nameValuePairs.add(new BasicNameValuePair("firstName",
                        mFirstName.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("lastName",
                        mLastName.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("email",
                        mEmailAddress.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("password",
                        mPassword1.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("phoneNumber",
                        mPhoneNumber.getText().toString()));

                NetworkManagerOld httpMan = new NetworkManagerOld(this,
                        NetworkManagerOld.EnCallType.POSTFORLOGIN, NetworkManagerOld.ReturnTypeForReponse.JSON,
                        nameValuePairs, new TaskCompleted() {

                    @Override
                    public void onTaskFailed() {
                        // TODO Auto-generated method stub
                        AppHelper.showConnectionAlert(RegistrationActivity.this);
                        AppHelper.getInstance().hideProgressDialog();
                    }

                    @Override
                    public void onTaskCompletedSuccessfully(Object obj) {
                        // TODO Auto-generated method stub
                        Log.e("Menyoo",obj.toString());
                        afterAccountRegister(obj);
                        AppHelper.getInstance().hideProgressDialog();
                    }
                });

                String[] params = {this.getString(R.string.url_offline)
                        + "Register"};
                httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
            }else{
                toast.setText(message);
                toast.show();
            }
        }else{
            toast.setText(message);
            toast.show();
        }

    }

    private void afterAccountRegister(Object obj) {
        try {
            JSONObject jsonObject = new JSONObject(obj.toString());
            Toast.makeText(this,jsonObject.getString("Message"),Toast.LENGTH_LONG).show();
            int userID;
            if(jsonObject.getString("Status").equals("Success")){
                userID = jsonObject.getInt("UserId");
                Intent intent = new Intent(this,VerifyAccount.class);
                intent.putExtra("UserID",userID);
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isPwdMatch(){
        if(mPassword1.getText().toString().equals(mPAssword2.getText().toString()))
            return  true;
        else
            return false;
    }
    private boolean isFieldFill(){
        if (!mFirstName.getText().toString().trim().isEmpty())
            if (!mLastName.getText().toString().trim().isEmpty())
                if (!mEmailAddress.getText().toString().trim().isEmpty())
                    if (!mPhoneNumber.getText().toString().trim().isEmpty())
                        if (!mPassword1.getText().toString().trim().isEmpty())
                            if (!mPAssword2.getText().toString().trim().isEmpty())
                                return true;
                            else {
                                message = "Repeat Password is empty";
                                return false;
                            }
                        else {
                            message = "Password is empty";
                            return false;
                        }
                    else {
                        message = "Phone Number is empty";
                        return false;
                    }
                else{
                    message = "Email Address is empty";
                    return false;
                }
            else{
                message = "Last Name is empty";
                return false;
            }
        else{
            message = "First Name is empty";
            return false;
        }
    }

    public boolean validateField(){
        if (AppHelper.isEmailValid(mEmailAddress.getText().toString()))
            if (AppHelper.isPasswordValid(mPassword1.getText().toString()))
                if (isPwdMatch())
                    return true;
                else
                    message = "Password do not match";
            else
                message = "Password must be of 6 character";
        else
            message = "Email Address is invalid";

        return false;
    }


}
