package com.dd.menyoo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.menyoo.R;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.model.UserModel;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends BaseFragment {


    public Profile() {
        // Required empty public constructor
    }

    RelativeLayout ll_main;
    TextView tvFirstName,tvLastName,tvPhoneNumber,tvEmailAddress;
    ImageView ivDisplayPic;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ll_main = (RelativeLayout)view.findViewById(R.id.ll_main);
        tvFirstName = (TextView)view.findViewById(R.id.firstName);
        tvLastName = (TextView)view.findViewById(R.id.lastName);
        tvEmailAddress = (TextView)view.findViewById(R.id.emailAddress);
        tvPhoneNumber = (TextView)view.findViewById(R.id.phone_number);
        ivDisplayPic = (ImageView)view.findViewById(R.id.display_pic);

        ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        setValue();
    }
    public void setValue(){
        UserModel userModel = AppController.getLoginUser();
        if(userModel!=null){
            tvFirstName.setText(String.format("First Name : %s",userModel.getFirstName()));
            tvLastName.setText(String.format("Last Name : %s",userModel.getLastName()));
            tvEmailAddress.setText(String.format("Email : %s",userModel.getEmailAddress()));
            tvPhoneNumber.setText(String.format("Phone Number : %s",userModel.getPhoneNumber()));
            if(userModel.getDisplayPicturePAth()!=null&&userModel.getDisplayPicturePAth().length()>0){
                Picasso.with(getActivity()).load(userModel.getDisplayPicturePAth()).into(ivDisplayPic);
            }
        }
    }
}
