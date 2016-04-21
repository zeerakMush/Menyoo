package com.dd.menyoo.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.menyoo.R;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.fragment.BaseFragment;
import com.dd.menyoo.fragment.Loyality;
import com.dd.menyoo.model.CategoryExtra;
import com.dd.menyoo.model.CheckBillModel;
import com.dd.menyoo.model.LTYModel;
import com.dd.menyoo.model.OrderModel;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ClaimAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<LTYModel> mDataList;
    private int currentStamps;
    BaseFragment fragment;
    Context mCtx;

    public ClaimAdapter(Context ctx, BaseFragment fragment) {
        this.mCtx = ctx;
        this.fragment = fragment;
    }

    public void setData(ArrayList<LTYModel> data,int currentStamps) {
        if (mDataList != data) {
            mDataList = data;
            this.currentStamps = currentStamps;
            notifyDataSetChanged();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.lty_row, parent, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, final int position) {
        final ItemViewHolder holder = (ItemViewHolder) rawHolder;
        final LTYModel item = mDataList.get(position);
        holder.btnClaim.setText(item.getRequirements());
        if(currentStamps<item.getRewardOnstamps())
            holder.btnClaim.setEnabled(false);
        else
            holder.btnClaim.setEnabled(true);
        holder.btnClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                claimLoyaltyDescription(item.getRewardOnstamps(),item.getClaimMessage());

            }
        });
    }

    protected void claimLoyaltyDescription(final int stamps, final String claimMessage) {
        AppHelper.getInstance().showProgressDialog("Please Wait",mCtx);
        List<NameValuePair> nameValuePairs = new ArrayList<>(
                2);
        NetworkManagerOld httpMan = new NetworkManagerOld(mCtx,
                NetworkManagerOld.EnCallType.POSTFORALL, NetworkManagerOld.ReturnTypeForReponse.String,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                // TODO Auto-generated method stub
                AppHelper.showConnectionAlert(mCtx);
                AppHelper.getInstance().hideProgressDialog();
                // / afterCatlogUpdate(null);
            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                Log.e("Menyoo", obj.toString());
                AppHelper.getInstance().hideProgressDialog();
//                //{"Status":"Success","Message":"loyalty claimed"})
                afterClaimLoyalty(obj,stamps,claimMessage);
            }
        });
        //ClaimLoyality(int Userid,int billId,int stamps)
        String url = String.format("ClaimLoyality?Userid=%d&billId=%d&stamps=%d",
                AppController.getLoginUser().getUserId(),
                AppController.getBillId(),stamps);

        String[] params = {mCtx.getString(R.string.url_offline) + url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    private void afterClaimLoyalty(Object obj,int stamps,String claimMessage) {
        try {
            JSONObject jObj = new JSONObject(obj.toString());
            String status = jObj.getString("Status");
            if(status.equals("Success")){
                Toast.makeText(mCtx,claimMessage,Toast.LENGTH_LONG).show();
                ((Loyality)fragment).afterClaimOrder(stamps);
            }else{
                Toast.makeText(mCtx,"LTY Not Claimed",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private Button btnClaim;
        public ItemViewHolder(View itemView) {
            super(itemView);
            btnClaim = (Button) itemView.findViewById(R.id.btn_claim);
        }
    }
}
