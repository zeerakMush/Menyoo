package com.dd.menyoo.fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.menyoo.R;
import com.dd.menyoo.TabActivity;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Suggestions extends BaseFragment implements View.OnClickListener {

    EditText etComment;
    Button btnSubmit;

    public Suggestions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_suggestions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etComment = (EditText) view.findViewById(R.id.et_comment);
        btnSubmit = (Button) view.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                submitEmail();
                break;
        }
    }

    private void submitEmail() {

        if (!etComment.getText().toString().trim().isEmpty()) {
            String subject = "Menyoo Suggestions (" + AppController.getCurrentRestaurent()
                    .getRestaurantName() + ")";
            String message = "Name:" + AppController.getLoginUser().getFirstName() + " " +
                    AppController.getLoginUser().getLastName() + "\nEmail Address: "
                    + AppController.getLoginUser().getEmailAddress() + "\nRestaurant: "
                    + AppController.getCurrentRestaurent().getRestaurantName() + "\n" +
                    "\nFeedback: " + etComment.getText().toString();
            postEmail(message);
            //sendEmail(AppController.getCurrentRestaurent().getFeedbackEmail(),subject,message);
        } else {
            Toast.makeText(getActivity(), "Comment Section is empty", Toast.LENGTH_LONG).show();
        }
    }


    /*    private void sendEmail(String emailAddress, String subject, String message) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", emailAddress, null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivityForResult(Intent.createChooser(emailIntent, "Send email..."), 2);
        }*/
    protected void postEmail(String feedback) {
        AppHelper.getInstance().showProgressDialog("Please wait",getActivity());
        List<NameValuePair> nameValuePairs = new ArrayList<>(
                5);
        nameValuePairs.add(new BasicNameValuePair("UserId", "" + AppController.getLoginUser().getUserId()));
        nameValuePairs.add(new BasicNameValuePair("restaurantId", "" + AppController.getCurrentRestaurent().getRestaurantID()));
        nameValuePairs.add(new BasicNameValuePair("feedBackEmail", "" + AppController.getCurrentRestaurent().getFeedbackEmail()));
        nameValuePairs.add(new BasicNameValuePair("FeedBack", feedback));

        NetworkManagerOld httpMan = new NetworkManagerOld(getActivity(),
                NetworkManagerOld.EnCallType.POSTFORLOGIN, NetworkManagerOld.ReturnTypeForReponse.String,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                // TODO Auto-generated method stub
                AppHelper.getInstance().hideProgressDialog();
                Log.e("Menyoo", "Some Error");
                AppHelper.showConnectionAlert(getActivity());

                // / afterCatlogUpdate(null);
            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                Log.e("Menyoo", obj.toString());
                //afterGetQuestion(obj);
                ((TabActivity) getActivity()).replaceFragment(new Action(), true);
                AppHelper.getInstance().hideProgressDialog();
            }
        });
        String url = "EmailFeedBack";
        String[] params = {getString(R.string.url_offline) + url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


}
