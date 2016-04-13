package com.dd.menyoo.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd.menyoo.R;
import com.dd.menyoo.common.AppController;

/**
 * A simple {@link Fragment} subclass.
 */
public class Loyality extends BaseFragment {


    public Loyality() {
        // Required empty public constructor
    }

    TextView tvProgrammeName,tvLoyaltyDeatils,tvLoyaltyRequirment,tvUserName,tvLevels;
    ImageView ivStamp1,ivStamp2,ivStamp3,ivStamp4,ivStamp5;
    LinearLayout llStamps;
    int stampsToshow;
    int range=5;
    int loyaltyStamps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loyality, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvProgrammeName = (TextView)view.findViewById(R.id.tv_programme_name);
        tvLoyaltyDeatils = (TextView)view.findViewById(R.id.tv_loyalty_details);
        tvLoyaltyRequirment = (TextView)view.findViewById(R.id.tv_loyaltyRequirment);
        tvUserName = (TextView)view.findViewById(R.id.tv_username);
        tvLevels = (TextView)view.findViewById(R.id.tv_levels);
        ivStamp1 = (ImageView)view.findViewById(R.id.iv_stamp1);
        ivStamp2 = (ImageView)view.findViewById(R.id.iv_stamp2);
        ivStamp3 = (ImageView)view.findViewById(R.id.iv_stamp3);
        ivStamp4 = (ImageView)view.findViewById(R.id.iv_stamp4);
        ivStamp5 = (ImageView)view.findViewById(R.id.iv_stamp5);
        llStamps = (LinearLayout)view.findViewById(R.id.ll_stamps);
        loyaltyStamps = AppController.getLoginUser().getLoyalityPoints();
        loyaltyStamps = 83;
        setView();
    }

    public void setView(){
        tvUserName.setText(String.format("%s, %s", AppController.getLoginUser().getFirstName(), AppController.getLoginUser().getLastName()));
        tvProgrammeName.setText(String.format("%s Reward Programme",AppController.getCurrentRestaurent().getRestaurantName()));
        tvLoyaltyRequirment.setText(AppController.getCurrentRestaurent().getLoyalityRequirements());
        tvLoyaltyDeatils.setText(AppController.getCurrentRestaurent().getLoyalityDetails());
        setLevels();
    }

    private void setLevels() {
        int level = loyaltyStamps/range;
        if(level==0&&loyaltyStamps>0)
            level =1;
        tvLevels.setText(String.format("Level %d(%d)",level,1));
        stampsToshow = loyaltyStamps%range;
        stampsShow();
    }

    public void stampsShow(){
        int to=0;
        if(stampsToshow==0&&loyaltyStamps>0){
            to = 0;
        }else if(stampsToshow>0){
            to = (range-stampsToshow)%5;
        }else{
            to=range%5;
        }
        for(int i=0 ;i<llStamps.getChildCount()-to;i++){
            ((ImageView)llStamps.getChildAt(i)).setImageResource(R.drawable.nature);
        }
    }

}
