package com.dd.menyoo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.common.SharedPrefManager;
import com.dd.menyoo.model.FacebookUserModel;
import com.dd.menyoo.model.UserModel;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends BaseClass implements FacebookCallback<LoginResult>{

    LoginButton fbLoginButton;
    CallbackManager callbackManager;
    SharedPrefManager sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        if(sharedPrefManager.checkIfUserIsLoggedIn()!=null){
            AppController.setLoginUser(sharedPrefManager.checkIfUserIsLoggedIn());
            showRestaurantActivity();
        }
        initViews();
    }
    public void initViews(){
        fbLoginButton = (LoginButton)findViewById(R.id.btn_fb);
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
        fbLoginButton.registerCallback(callbackManager, this);
        sharedPreferences = new SharedPrefManager(this);
    }


    public void toMenuActivity(View view) {
        startActivity(new Intent(this,SignInActivity.class));
    }

    public void toRegister(View view) {
        startActivity(new Intent(this,RegistrationActivity.class));

    }

    private void showRestaurantActivity() {
        Intent intentAct = new Intent(LoginActivity.this,
                MenuActivity.class);
        intentAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentAct);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
      //  onSuccess(null);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        final AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null) {
            Log.e("Fb", AccessToken.getCurrentAccessToken().getToken());
            GraphRequest request = GraphRequest.newMeRequest(
                    token,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.v("LoginActivity", response.toString());
                            AppHelper.getInstance().showProgressDialog("Please Wait",LoginActivity.this);
                            // Application code
                            try {
                                Profile profile = Profile.getCurrentProfile();
                                /*String firstName = profile.getFirstName();
                                String lastName = profile.getLastName();*/
                                String name = object.getString("name");
                                String firstName = name.split(" ")[0];
                                String lastName = name.split(" ")[1];
                                String userID= object.getString("id");
                                String proficPic;
                                if(profile!=null){
                                    proficPic = profile.getProfilePictureUri(40,40).toString();
                                }else
                                    proficPic="";
                                String accessToken = token.getToken();
                                String email = object.getString("email");
                                RegisterThroughFacebook(new FacebookUserModel(firstName,lastName,userID,
                                        proficPic,accessToken,email));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    public void RegisterThroughFacebook(FacebookUserModel fbUser){
        List<NameValuePair> nameValuePairs = new ArrayList<>(
                2);
        nameValuePairs.add(new BasicNameValuePair("firstName",
               fbUser.getFirstName()));
        nameValuePairs.add(new BasicNameValuePair("lastName",
                fbUser.getLastName()));
        nameValuePairs.add(new BasicNameValuePair("email",
               fbUser.getEmail()));
        nameValuePairs.add(new BasicNameValuePair("fbAccessToken",
                fbUser.getAccessToken()));
        nameValuePairs.add(new BasicNameValuePair("phoneNumber",
                " "));
        nameValuePairs.add(new BasicNameValuePair("fbUserId",
                fbUser.getUserID()));
        nameValuePairs.add(new BasicNameValuePair("fbImage",
                fbUser.getProficPic()));


        NetworkManagerOld httpMan = new NetworkManagerOld(this,
                NetworkManagerOld.EnCallType.POSTFORLOGIN, NetworkManagerOld.ReturnTypeForReponse.JSON,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                // TODO Auto-generated method stub
                AppHelper.showConnectionAlert(LoginActivity.this);

            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                Log.e("Menyoo",obj.toString());
                afterUserLogin(obj);
            }
        });

        String[] params = {this.getString(R.string.url_offline)
                + "FBRegisterLogin"};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    public void afterUserLogin(Object object){
        UserModel user ;
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            AppHelper.getInstance().hideProgressDialog();
            if(!jsonObject.has("Status")){
                int id= jsonObject.getInt("Id");
                String firstName = jsonObject.getString("FirstName");
                String lastName = jsonObject.getString("LastName");
                String email = jsonObject.getString("Email");
                String phoneNumber= jsonObject.getString("PhoneNumber");
                String displayPicture = jsonObject.getString("DisplayPictureFile");
                user = new UserModel(id,firstName,lastName,email,phoneNumber,displayPicture);
                AppController.setLoginUser(user);
                sharedPreferences.SetUserIsLoggedIn(user);
                showRestaurantActivity();
            }else{
                String msg = jsonObject.getString("Message");
                Toast.makeText(LoginActivity.this,msg,Toast.LENGTH_LONG).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancel() {
        Log.v("LoginActivity", "cancel");
    }

    @Override
    public void onError(FacebookException error) {
        Log.v("LoginActivity", error.getCause().toString());
    }



}

