package com.dd.menyoo.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.menyoo.R;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.model.CheckBillModel;
import com.dd.menyoo.model.OrderHistoryModel;
import com.dd.menyoo.model.OrderModel;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 01-Mar-16.
 */
public class OrderHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<OrderHistoryModel> mDataList;
    Context mCtx;
    View.OnClickListener _listener;

    public OrderHistoryAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public void setData(ArrayList<OrderHistoryModel> data) {
        if (mDataList != data) {
            mDataList = data;
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.order_histor_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, final int position) {
        final ItemViewHolder holder = (ItemViewHolder) rawHolder;
        final OrderHistoryModel ohm = mDataList.get(position);
        holder.tvResName.setText(ohm.getResName());
        holder.tvOrderDate.setText(ohm.getCreatedDate());
        holder.rlOrder.setTag(ohm);
        if(_listener!=null)
            holder.rlOrder.setOnClickListener(_listener);
    }



    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvResName, tvOrderDate;
        private RelativeLayout rlOrder;
        public ItemViewHolder(View itemView) {
            super(itemView);
            tvResName = (TextView) itemView.findViewById(R.id.tv_res_name);
            tvOrderDate = (TextView) itemView.findViewById(R.id.tv_order_date);
            rlOrder = (RelativeLayout) itemView.findViewById(R.id.rl_orders);
        }
    }

    public void set_listener(View.OnClickListener _listener) {
        this._listener = _listener;
    }
}


