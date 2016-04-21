package com.dd.menyoo.fragment;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dd.menyoo.R;
import com.dd.menyoo.TabActivity;
import com.dd.menyoo.adapter.CheckBillAdapter;
import com.dd.menyoo.adapter.ClaimAdapter;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.model.CheckBillModel;
import com.dd.menyoo.model.LTYModel;
import com.dd.menyoo.model.LoyaltyModel;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class Loyality extends BaseFragment implements View.OnClickListener{


    public Loyality() {
        // Required empty public constructor
    }

    TextView tvProgrammeName, tvLoyaltyDeatils, tvLoyaltyRequirment,
            tvUserName, tvLevels,tvReadMore;
    ScrollView svMain ;
    ProgressBar pbWait;
    Button btnClaim;
    LinearLayout llAddStamps;
    private LoyaltyModel loyaltyDetails;
    int stampsToshow;
    int range = 5;
    int loyaltyStamps;
    boolean isReadMore = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loyality, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvProgrammeName = (TextView) view.findViewById(R.id.tv_programme_name);
        tvLoyaltyDeatils = (TextView) view.findViewById(R.id.tv_loyalty_details);
        tvLoyaltyRequirment = (TextView) view.findViewById(R.id.tv_loyaltyRequirment);
        tvUserName = (TextView) view.findViewById(R.id.tv_username);
        tvLevels = (TextView) view.findViewById(R.id.tv_levels);
        tvReadMore = (TextView) view.findViewById(R.id.tv_readmore);tvReadMore.setOnClickListener(this);
        llAddStamps = (LinearLayout) view.findViewById(R.id.ll_addstamps);
        svMain = (ScrollView) view.findViewById(R.id.sv_all);
        pbWait = (ProgressBar) view.findViewById(R.id.pb_menu);

        btnClaim = (Button)view.findViewById(R.id.btn_claim);
        btnClaim.setOnClickListener(this);btnClaim.setEnabled(false);
        getLoyaltyDescription();
        loyaltyStamps = AppController.getLoginUser().getLoyalityPoints();
        loyaltyStamps = 83;

        setView();
    }

    public void setView() {
        tvUserName.setText(String.format("%s, %s", AppController.getLoginUser().getFirstName(), AppController.getLoginUser().getLastName()));
        tvProgrammeName.setText(String.format("%s Reward Programme", AppController.getCurrentRestaurent().getRestaurantName()));
        tvLoyaltyRequirment.setText(AppController.getCurrentRestaurent().getLoyalityRequirements());
        tvLoyaltyDeatils.setText(AppController.getCurrentRestaurent().getLoyalityDetails());
        ViewTreeObserver vto = tvLoyaltyRequirment.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (AppHelper.isTextViewEllipsized(tvLoyaltyRequirment)) {
                    tvReadMore.setVisibility(View.VISIBLE);
                }
                else
                    tvReadMore.setVisibility(View.GONE);
            }
        });

    }


    private void setLevels() {
        int level = loyaltyStamps / range;
        if (level == 0 && loyaltyStamps > 0)
            level = 1;
        for(int i=0;i<loyaltyDetails.getLevels().size();i++){
            if(i==0&& loyaltyDetails.getAllTimeStamps()
                    <=loyaltyDetails.getLevels().get(i)){
                level = i+1;
                break;
            }else if(i!=0&&loyaltyDetails.getAllTimeStamps()>loyaltyDetails.getLevels().get(i-1)
                    &&loyaltyDetails.getAllTimeStamps()<=loyaltyDetails.getLevels().get(i)){
                level=i+1;
                break;
            }else{
                level=i+1;
            }
        }
        tvLevels.setText(String.format("Level %d (%d stamps)", level, loyaltyDetails.getCurrentStamps()));
        stampsToshow = loyaltyStamps % range;
    }

    boolean isAddingExtraRow = false;


    public void addStamps() {
        int stampsRow = loyaltyDetails.getMaxStamps() / 5;
        int stampsToDisplayInLastRow=0;
        totalImageView = new ArrayList<>();

        if(loyaltyDetails.getMaxStamps()%5!=0){
            isAddingExtraRow = true;
            stampsToDisplayInLastRow = loyaltyDetails.getMaxStamps()%5;
            stampsRow = stampsRow+1;
        }
        for (int i = 0; i < stampsRow; i++){
            int toHide;
            if(i==0&&isAddingExtraRow)
                toHide = 5- stampsToDisplayInLastRow;
            else
                toHide = 0;
            addTotalStamps(toHide);
        }
        setCurrentStamps(true);
        setLevels();
        setClaimButton();
    }

    ArrayList<ImageView> totalImageView;

    public void addTotalStamps(int toHide) {

        LayoutInflater vi = (LayoutInflater) getActivity().getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = vi.inflate(R.layout.loyalty_flower_item, null);
        LinearLayout llStamps = (LinearLayout) view.findViewById(R.id.ll_stamps);

        for(int iv=llStamps.getChildCount()-1;iv>=0;iv--)
            totalImageView.add((ImageView)(llStamps.getChildAt(iv)));
        for(int i=llStamps.getChildCount()-1 ;i>=5-toHide;i--){
            (llStamps.getChildAt(i)).setVisibility(View.GONE);
        }
        // insert into main view
        llAddStamps.addView(view, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    public void setCurrentStamps (boolean isToReverse){
        if(isToReverse)
            Collections.reverse(totalImageView);
        int currentStamps = loyaltyDetails.getCurrentStamps();
        if(currentStamps>loyaltyDetails.getMaxStamps())
            currentStamps = loyaltyDetails.getMaxStamps();
        for(int i =0;i<currentStamps;i++)
            totalImageView.get(i).setImageResource(R.drawable.clover_small);
        for(int j=currentStamps;j<totalImageView.size();j++)
            totalImageView.get(j).setImageResource(R.drawable.lock_small);
        setLTYStamps();
    }

    public void setLTYStamps (){
        for(LTYModel lty :loyaltyDetails.getLTY()){
            if(lty.getRewardOnstamps()<=loyaltyDetails.getCurrentStamps())
                totalImageView.get(lty.getRewardOnstamps()-1).setImageResource(R.drawable.clover);
            else
                totalImageView.get(lty.getRewardOnstamps()-1).setImageResource(R.drawable.lock);
        }
    }

    public void setClaimButton(){
        if(loyaltyDetails.getLTY().size()>0&&loyaltyDetails.getCurrentStamps()>=loyaltyDetails.getLTY().get(0).getRewardOnstamps())
            btnClaim.setEnabled(true);
        else
            btnClaim.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_claim:
                if(((TabActivity)getActivity()).checkIsTableAcquired())
                    showClaimDialog();
                else
                    ((TabActivity) getActivity()).showTableDialog();
                break;
            case R.id.tv_readmore:
                if(!isReadMore){
                    tvLoyaltyRequirment.setMaxLines(Integer.MAX_VALUE);
                    tvReadMore.setText("Read less");
                    isReadMore = true;
                }else{
                    tvLoyaltyRequirment.setMaxLines(4);
                    tvReadMore.setText("Read more");
                    isReadMore = false;
                }

                break;
        }
    }
    Dialog dialog;
    public void showClaimDialog(){
        AppHelper.getInstance().hideProgressDialog();

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.claim_dialog);

        RecyclerView rvClaim = (RecyclerView) dialog.findViewById(R.id.rv_claim);
        ClaimAdapter claimAdapter = new ClaimAdapter(getActivity(),this);
        rvClaim.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        claimAdapter.setData(loyaltyDetails.getLTY(),loyaltyDetails.getCurrentStamps());
        rvClaim.setAdapter(claimAdapter);
        dialog.show();
    }

    public void afterClaimOrder(int claims){
        dialog.dismiss();
        loyaltyDetails.setCurrentStamps(loyaltyDetails.getCurrentStamps()-claims);
        setCurrentStamps(false);
        setClaimButton();
        setLevels();
    }

    //region Loyalty Service
    protected void getLoyaltyDescription() {
        //AppHelper.getInstance().showProgressDialog("Please Wait",getActivity());
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                2);
        NetworkManagerOld httpMan = new NetworkManagerOld(getActivity(),
                NetworkManagerOld.EnCallType.POSTFORALL, NetworkManagerOld.ReturnTypeForReponse.String,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                // TODO Auto-generated method stub
               // AppHelper.getInstance().hideProgressDialog();
                AppHelper.showConnectionAlert(getActivity());
                pbWait.setVisibility(View.GONE);
                // / afterCatlogUpdate(null);
            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                Log.e("Menyoo", obj.toString());
               // AppHelper.getInstance().hideProgressDialog();
                pbWait.setVisibility(View.GONE);
                afterGetLoyaltyDetail(obj);
            }
        });
        String url = String.format("LoyalityService?userId=%d&restaurantId=%d",
                AppController.getLoginUser().getUserId(),
                AppController.getCurrentRestaurent().getRestaurantID());

        String[] params = {getString(R.string.url_offline) + url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }


    public void afterGetLoyaltyDetail(Object jObj) {
        try {
            JSONObject jsonObject = new JSONObject(jObj.toString());
            int crrentStamps = jsonObject.getInt("CurrentStamps");
            ArrayList<LTYModel> ltyModel = getLtys(jsonObject.getJSONArray("LTY"));
            ArrayList<Integer> levels = getLevels(jsonObject.getJSONArray("Levels"));
            int maxStamps = jsonObject.getInt("MaxStamps");
            int alltimeStamps = jsonObject.getInt("AllTimeStamps");

            loyaltyDetails = new LoyaltyModel(crrentStamps, ltyModel, levels,
                    maxStamps,alltimeStamps);
            addStamps();
            svMain.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<LTYModel> getLtys(JSONArray jLTY) {
        ArrayList<LTYModel> ltyArray = new ArrayList<>();
        try {
            for (int i = 0; i < jLTY.length(); i++) {
                int RewardOnstamps = jLTY.getJSONObject(i).getInt("RewardOnstamps");
                String Requirements = jLTY.getJSONObject(i).getString("Requirements");
                String claimMessage = jLTY.getJSONObject(i).getString("ClaimMessage");
                ltyArray.add(new LTYModel(RewardOnstamps,Requirements,claimMessage));
            }
            return ltyArray;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<Integer> getLevels(JSONArray jLevel){
        ArrayList<Integer> levels = new ArrayList<>();
        try {
            for(int i=0;i<jLevel.length();i++){
                int level = jLevel.getJSONObject(i).getInt("LevelLimit");
                levels.add(level);
            }
            return levels;
        } catch (JSONException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    // endregion
}
