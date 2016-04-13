package com.dd.menyoo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForgetPassword extends AppCompatActivity {
    EditText etEmail, etVerificationCode, etNewPwd;
    TextView tvVerficationInstruction;
    Button btnSend;
    String emailAddress;
    int userID;
    boolean isToSendPassword = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        setView();
    }

    public void setView() {
        etEmail = (EditText) findViewById(R.id.et_Email_address);
        etVerificationCode = (EditText) findViewById(R.id.et_verification_code);
        etNewPwd = (EditText) findViewById(R.id.et_new_password);
        tvVerficationInstruction = (TextView) findViewById(R.id.tv_verification_txt);
        btnSend = (Button) findViewById(R.id.btn_change_pwd);
        etNewPwd.setHint(Html.fromHtml("<font size=\"16\">" +
                "New Password*     " + "</font>" + "<small>" +
                "(min 6 characters)" + "</small>" ));
    }

    private void checkEmail(String email) {
        AppHelper.getInstance().showProgressDialog("Please wait", this);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("email", "" + email));
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
                afterSendEmail(obj);
            }
        }, true);

        String[] params = {this.getString(R.string.url_offline) + "CheckEmailForPassword"};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    private void afterSendEmail(Object obj) {
        try {
            AppHelper.getInstance().hideProgressDialog();
            JSONObject jsonObj = new JSONObject(obj.toString());
            String state = jsonObj.getString("state");
            if (state.equals("Success")) {
                userID = jsonObj.getInt("UserId");
                setVErificationVisible();
            } else
                Toast.makeText(this, jsonObj.getString("Message"), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void resetPAssword(int userID, String code, String password) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("userid", "" + userID));
        nameValuePairs.add(new BasicNameValuePair("code", code));
        nameValuePairs.add(new BasicNameValuePair("newPassword", password));

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
                AppHelper.getInstance().hideProgressDialog();
                afterResetPassword(obj);
            }
        }, true);

        String[] params = {this.getString(R.string.url_offline) + "ResetPassword"};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    private void afterResetPassword(Object obj) {
        try {
            AppHelper.getInstance().hideProgressDialog();
            JSONObject jsonObj = new JSONObject(obj.toString());
            String state = "";
            if (jsonObj.has("state"))
                state = jsonObj.getString("state");
            if (state.equals("error")) {
                Toast.makeText(this, jsonObj.getString("Message"), Toast.LENGTH_LONG).show();
            } else
                startActivity(new Intent(this, SignInActivity.class));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setVErificationVisible() {
        etNewPwd.setVisibility(View.VISIBLE);
        etVerificationCode.setVisibility(View.VISIBLE);
        tvVerficationInstruction.setVisibility(View.VISIBLE);
        btnSend.setText("Change password");
        isToSendPassword = true;
    }

    public void sendEmail(View view) {

        if (!isToSendPassword)
            if (AppHelper.isEmailValid(etEmail.getText().toString()))
                checkEmail(etEmail.getText().toString());
            else
                Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_LONG).show();
        else {
            if (!etVerificationCode.getText().toString().trim().isEmpty())
                if (AppHelper.isPasswordValid(etNewPwd.getText().toString()))
                    resetPAssword(userID, etVerificationCode.getText().toString(), etNewPwd.getText().toString());
                else
                    Toast.makeText(this, "Invalid Password", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Verification code can not be empty", Toast.LENGTH_LONG).show();
        }

    }
}
